package controller;

import model.Ghost;
import model.GhostBlinky;
import model.GhostPinky;
import model.Map;
import model.ObjectGame;
import model.PacMan;
import model.Point;
import model.SuperPoint;
import view.GamePanel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Controlador principal del juego. Implementa Runnable para correr a 60 FPS.
 * Coordina el modelo (Pacman, fantasmas, mapa) con la vista (GamePanel).
 * También gestiona el audio directamente (sin AudioManager ni HashMap).
 */
public class GameController implements Runnable {

    private static final int FPS = 60;
    private static final long NANOS_POR_FRAME = 1_000_000_000L / FPS;
    private static final int FRAMES_ANIMACION = 8;

    // Total de puntos y super-puntos en el mapa 21×21
    private static final int TOTAL_POINTS = 185;

    // ── Modelos ───────────────────────────────────────────────────────
    private final GamePanel vista;
    private final Map mapa;
    private final PacMan pacman;
    private final Ghost[] fantasmas; // [0] = Blinky, [1] = Pinky

    // ── Control del bucle ─────────────────────────────────────────────
    private Thread hiloJuego;
    private volatile boolean enEjecucion;
    private String estado = "inicio";
    private int puntosComidos;
    private int contadorAnimacion;

    // ── Audio (campos individuales, sin HashMap) ──────────────────────
    private Clip sonidoComerPunto;
    private Clip sonidoMuertePacman;
    private Clip sonidoComerFantasma;
    private Clip sonidoFantasmasAzules;
    private Clip sonidoIntro;

    public GameController(GamePanel vista) {
        this.vista = vista;
        this.mapa = new Map();
        this.pacman = new PacMan();

        // Crear los dos fantasmas en un array
        fantasmas = new Ghost[2];
        fantasmas[0] = new GhostBlinky();
        fantasmas[1] = new GhostPinky();

        puntosComidos = 0;
        contadorAnimacion = 0;

        // Cargar sonidos
        sonidoComerPunto = cargarSonido("atepellot.wav");
        sonidoMuertePacman = cargarSonido("died.wav");
        sonidoComerFantasma = cargarSonido("ateghost.wav");
        sonidoFantasmasAzules = cargarSonido("blue_ghosts.wav");
        sonidoIntro = cargarSonido("intro.wav");

        // Inyectar modelos en la vista y registrar el teclado
        vista.assignModel(mapa, pacman, fantasmas);
        vista.setGameStatus(estado);
        vista.addKeyListener(new InputHandler(pacman));
    }

    /**
     * Inicia el hilo del bucle de juego y reproduce el sonido de intro.
     */
    public synchronized void iniciar() {
        if (enEjecucion) return;
        enEjecucion = true;

        // Reproducir intro en hilo separado para no bloquear la interfaz
        new Thread(new Runnable() {
            public void run() {
                reproducirSonido(sonidoIntro);
                try {
                    Thread.sleep(4500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        hiloJuego = new Thread(this, "PacmanGameLoop");
        hiloJuego.start();
    }

    /**
     * Detiene limpiamente el bucle y libera el audio.
     */
    public synchronized void detener() {
        enEjecucion = false;
        if (hiloJuego != null) {
            try {
                hiloJuego.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        cerrarAudio();
    }

    /**
     * Bucle principal a 60 FPS. Actualiza el estado del juego y solicita
     * el repintado al Event Dispatch Thread de Swing.
     */
    @Override
    public void run() {
        long ultimoTiempo = System.nanoTime();
        double delta = 0;

        while (enEjecucion) {
            long ahora = System.nanoTime();
            delta += (ahora - ultimoTiempo) / (double) NANOS_POR_FRAME;
            ultimoTiempo = ahora;

            if (delta >= 1) {
                actualizar();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        vista.repaint();
                    }
                });
                delta--;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                enEjecucion = false;
            }
        }
    }

    // ─── Lógica de actualización ──────────────────────────────────────

    private void actualizar() {
        // Inicio → Jugando
        if (estado.equals("inicio") && pacman.isEnter()) {
            estado = "jugando";
            vista.setGameStatus(estado);
            return;
        }

        // GameOver → Inicio
        if (estado.equals("gameover") && pacman.isEnter()) {
            reiniciarPartida();
            estado = "inicio";
            vista.setGameStatus(estado);
            return;
        }

        if (!estado.equals("jugando") || pacman.isPaused()) return;

        // Sin vidas → Game Over
        if (pacman.getLives() < 1) {
            estado = "gameover";
            vista.setGameStatus(estado);
            return;
        }

        // Animación de la boca cada FRAMES_ANIMACION ciclos
        contadorAnimacion++;
        if (contadorAnimacion >= FRAMES_ANIMACION) {
            pacman.updateSprite(pacman.getDirection());
            contadorAnimacion = 0;
        }

        moverPacman();
        moverFantasmas();
        comprobarColisionesPersonajes();

        // Victoria: todos los puntos comidos
        if (puntosComidos >= TOTAL_POINTS) {
            estado = "gameover";
            vista.setGameStatus(estado);
        }
    }

    private void moverPacman() {
        int nuevoX = pacman.getX() + pacman.calculateDx();
        int nuevoY = pacman.getY() + pacman.calculateDy();

        if (!hayMuro(pacman.getHitBox(nuevoX, nuevoY))) {
            pacman.update();
            comprobarColisionConObjetos();
        }
    }

    private void moverFantasmas() {
        // Blinky escala su velocidad según la puntuación
        ((GhostBlinky) fantasmas[0]).comprobarVelocidad(pacman.getScore());

        for (int i = 0; i < fantasmas.length; i++) {
            Ghost f = fantasmas[i];
            int dx = f.calculateDx() * f.getSpeed();
            int dy = f.calculateDy() * f.getSpeed();
            Rectangle proyeccion = f.getHitBox(f.getX() + dx, f.getY() + dy);

            if (!hayMuro(proyeccion)) {
                f.update();
            } else {
                f.changeDirection(pacman.getX(), pacman.getY());
            }
        }
    }

    /**
     * Devuelve true si la hitbox dada choca con algún muro del tablero.
     */
    private boolean hayMuro(Rectangle hitbox) {
        ObjectGame[][] objs = mapa.getObjects();
        for (int fila = 0; fila < objs.length; fila++) {
            for (int col = 0; col < objs[fila].length; col++) {
                ObjectGame o = objs[fila][col];
                if (o != null && o.getType() == ObjectGame.WALL
                        && hitbox.intersects(o.getHitBox())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void comprobarColisionConObjetos() {
        Rectangle rPac = pacman.getHitBox();
        ObjectGame[][] objs = mapa.getObjects();

        for (int fila = 0; fila < objs.length; fila++) {
            for (int col = 0; col < objs[fila].length; col++) {
                ObjectGame o = objs[fila][col];
                if (o == null || !o.isActive()) continue;
                if (!rPac.intersects(o.getHitBox())) continue;

                if (o.getType() == ObjectGame.POINT) {
                    o.setActive(false);
                    pacman.addPoints(Point.VALUE);
                    reproducirSonido(sonidoComerPunto);
                    puntosComidos++;
                } else if (o.getType() == ObjectGame.SUPER_POINT) {
                    o.setActive(false);
                    pacman.addPoints(SuperPoint.VALUE);
                    reproducirSonido(sonidoComerPunto);
                    reproducirSonido(sonidoFantasmasAzules);
                    for (int i = 0; i < fantasmas.length; i++) {
                        fantasmas[i].setStateEatable(true);
                    }
                    puntosComidos++;
                }
            }
        }
    }

    private void comprobarColisionesPersonajes() {
        Rectangle rPac = pacman.getHitBox();

        for (int i = 0; i < fantasmas.length; i++) {
            Ghost f = fantasmas[i];
            if (!rPac.intersects(f.getHitBox())) continue;

            if (f.isStateEatable()) {
                reproducirSonido(sonidoComerFantasma);
                pacman.addPoints(100);
                f.restart();
            } else {
                reproducirSonido(sonidoMuertePacman);
                pacman.lossLife();
                pacman.resetPosition();
                for (int j = 0; j < fantasmas.length; j++) {
                    fantasmas[j].restart();
                }
                return;
            }
        }

        // Colisión entre los dos fantasmas: invierten dirección
        if (fantasmas[0].getHitBox().intersects(fantasmas[1].getHitBox())) {
            fantasmas[0].reverseDirection();
            fantasmas[1].reverseDirection();
        }
    }

    private void reiniciarPartida() {
        pacman.resetAll();
        for (int i = 0; i < fantasmas.length; i++) {
            fantasmas[i].restart();
        }
        mapa.uploadMap();
        puntosComidos = 0;
    }

    // ─── Audio ────────────────────────────────────────────────────────

    /**
     * Carga un archivo .wav desde /resources/sounds/ y devuelve un Clip listo para usar.
     */
    private Clip cargarSonido(String archivo) {
        try {
            InputStream is = getClass().getResourceAsStream("/sounds/" + archivo);
            if (is == null) {
                System.err.println("Sonido no encontrado: " + archivo);
                return null;
            }
            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            System.err.println("Error cargando sonido: " + archivo);
            return null;
        }
    }

    /**
     * Reproduce un clip desde el inicio. Si ya sonaba, lo reinicia.
     */
    private void reproducirSonido(Clip clip) {
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * Detiene y cierra todos los clips de audio.
     */
    private void cerrarAudio() {
        Clip[] todos = {sonidoComerPunto, sonidoMuertePacman,
                sonidoComerFantasma, sonidoFantasmasAzules, sonidoIntro};
        for (int i = 0; i < todos.length; i++) {
            if (todos[i] != null) {
                if (todos[i].isRunning()) todos[i].stop();
                todos[i].close();
            }
        }
    }
}


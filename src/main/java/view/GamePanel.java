package view;

import model.Entity;
import model.Ghost;
import model.Map;
import model.ObjectGame;
import model.PacMan;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Vista principal del juego. Solo dibuja, no tiene lógica de juego.
 * Recibe los modelos del controlador y los renderiza en pantalla.
 */
public class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // Dimensiones del panel (tablero 504 px + HUD 166 px = 670)
    public static final int WIDTH = 670;
    public static final int HEIGHT = 510;

    private final BufferedImage logo;

    private String gameStatus = "inicio";

    private Map mapa;
    private PacMan pacman;
    private Ghost[] ghosts;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        this.logo = Entity.uploadImage("logo_uam.gif");
    }

    /**
     * El controlador inyecta los modelos aquí para que la vista los pinte.
     */
    public void asignModels(Map mapa, PacMan pacman, Ghost[] fantasmas) {
        this.mapa = mapa;
        this.pacman = pacman;
        this.ghosts = fantasmas;
    }

    /**
     * El controlador avisa aquí cuando cambia el estado del juego.
     */
    public void setGameStatus(String estado) {
        this.gameStatus = estado;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (gameStatus.equals("inicio")) {
            drawInstructions(g2d);
        } else if (gameStatus.equals("jugando")) {
            drawGame(g2d);
        } else if (gameStatus.equals("gameover")) {
            drawGameOver(g2d);
        }
    }

    private void drawInstructions(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Verdana", Font.BOLD, 60));
        g2d.drawString("PAC-MAN", 160, 200);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 24));
        g2d.drawString("Pulse [Intro] para comenzar", 110, 280);

        g2d.setFont(new Font("Verdana", Font.PLAIN, 16));
        g2d.drawString("Flechas: mover  -  P: pausar", 160, 340);

        if (logo != null) {
            g2d.drawImage(logo, WIDTH / 2 - 40, 390, this);
        }
    }

    private void drawGame(Graphics2D g2d) {
        if (mapa == null || pacman == null) return;
        drawGameboard(g2d);
        drawPacMan(g2d);
        drawGhosts(g2d);
        drawHud(g2d);
    }

    private void drawGameboard(Graphics2D g2d) {
        ObjectGame[][] objs = mapa.getObjects();
        for (int fila = 0; fila < objs.length; fila++) {
            for (int col = 0; col < objs[fila].length; col++) {
                ObjectGame o = objs[fila][col];
                if (o != null && o.getType() != ObjectGame.EMPTY && o.isActive()) {
                    g2d.drawImage(o.getSprite(), col * Map.TAM_CELDA, fila * Map.TAM_CELDA, this);
                }
            }
        }
    }


    private void drawPacMan(Graphics2D g2d) {
        if (pacman.isActive()) {
            g2d.drawImage(pacman.getSprite(), pacman.getX(), pacman.getY(), this);
        }
    }

    private void drawGhosts(Graphics2D g2d) {
        if (ghosts == null) return;
        for (int i = 0; i < ghosts.length; i++) {
            Ghost f = ghosts[i];
            if (f.isActive()) {
                g2d.drawImage(f.getSprite(), f.getX(), f.getY(), this);
            }
        }
    }

    private void drawHud(Graphics2D g2d) {
        int hudX = 514; // justo después del tablero (21 cols × 24 px = 504 px)

        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 18));
        g2d.drawString("PUNTUACIÓN", hudX, 40);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 28));
        g2d.drawString(String.valueOf(pacman.getScore()), hudX + 20, 78);

        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 18));
        g2d.drawString("VIDAS", hudX + 20, 120);

        for (int i = 0; i < pacman.getLives(); i++) {
            g2d.drawImage(pacman.getSprite(), hudX + i * 30, 132, this);
        }

        if (logo != null) {
            g2d.drawImage(logo, hudX + 10, 340, this);
        }

        if (pacman.isPaused()) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(hudX, 430, 130, 55);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Verdana", Font.BOLD, 22));
            g2d.drawString("PAUSA", hudX + 18, 465);
        }
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 60));
        g2d.drawString("Game Over", 130, 220);

        g2d.setFont(new Font("Verdana", Font.BOLD, 22));
        g2d.drawString("Pulse [Intro] para reintentar", 110, 290);
    }
}

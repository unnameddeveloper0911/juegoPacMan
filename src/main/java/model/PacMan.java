package model;

import java.awt.image.BufferedImage;

public class PacMan extends Entity{

    //Constantes para darle la posición inicial a PacMan
    private static final int X_INITIAL=240;
    private static final int Y_INITIAL=240;
    private static final int SPEED=1;
    private static final int INITIAL_LIVES=3;

    //Coordenadas caminos laterales (Fila X pixeles)
    //Ejemplo: fila 9 x 24 px

    //Alto tablero
    private static final int Y_PATH=216;
    //Ancho tablero
    private static final int X_PATH=504;

    //Puntaje
    private int score=0;

    //Vidas
    private int lives=3;

    //Boca abierta?
    private boolean openMouth;

    //Para pausar
    private boolean paused;

    //Enter
    private boolean enter;


    //Getters y setters


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public boolean isOpenMouth() {
        return openMouth;
    }

    public void setOpenMouth(boolean openMouth) {
        this.openMouth = openMouth;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isEnter() {
        return enter;
    }

    public void setEnter(boolean enter) {
        this.enter = enter;
    }

    public PacMan(){
        super(uploadImage("pacman_left.png"),X_INITIAL,Y_INITIAL);
        this.score=0;
        this.lives=INITIAL_LIVES;
        this.openMouth=true;
        this.paused=false;
        setDirection(DIR_LEFT);
    }

    /**
     * Método para reiniciar la posición cuando pierde una vida
     */
    public void resetPosition(){
        setX(X_INITIAL);
        setY(Y_INITIAL);
        setActive(true);
        setSprite(uploadImage("pacman_left.png"));
    }

    /**
     * metodo opara resetear a pac man
     */
    public void resetAll(){
        resetPosition();
        this.lives=INITIAL_LIVES;
        this.score=0;
    }

    /**
     * Mueve a Pacman según su dirección actual y gestiona el túnel lateral.
     */
    @Override
    public void update() {
        // Túnel: sale por la derecha → entra por la izquierda
        if (getX() > X_PATH && getY() == Y_PATH) {
            setX(0);
        }
        // Túnel: sale por la izquierda → entra por la derecha
        if (getX() < 0 && getY() == Y_PATH) {
            setX(X_PATH);
        }

        setX(getX() + calculateDx() * SPEED);
        setY(getY() + calculateDy() * SPEED);
    }

    /**
     * Suma puntos a la puntuación acumulada.
     */
    public void sumarPuntos(int puntos) {
        this.score += puntos;
    }

    /**
     * Descuenta una vida.
     */
    public void perderVida() {
        if (lives > 0) lives--;
    }

    /**
     * Actualiza el sprite según la dirección y alterna la animación de boca.
     * Se llama cada ciertos frames para que la boca "masque".
     */
    public void actualizarSprite(int d) {
        String archive;
        if (openMouth) {
            archive = "pacman_closed.png";
        } else {
            if (d == DIR_LEFT) archive = "pacman_left.png";
            else if (d == DIR_RIGHT) archive = "pacman_right.png";
            else if (d == DIR_UP) archive = "pacman_up.png";
            else if (d == DIR_DOWN) archive = "pacman_down.png";
            else archive = "pacman_left.png";
        }
        setSprite(uploadImage(archive));
        openMouth = !openMouth;
    }

    /**
     * Activa o desactiva la pausa.
     */
    public void alternarPausa() {
        paused = !paused;
    }

    public PacMan(BufferedImage sprite, int x, int y) {
        super(sprite, x, y);
    }



}

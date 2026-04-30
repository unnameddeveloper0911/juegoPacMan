package model;

/**
 * Fantasma rosa (Pinky). Persigue a Pacman con velocidad constante.
 */
public class GhostPinky extends Ghost {

    private static final int X_INITIAL = 264;  // col 11 × 24 px
    private static final int Y_INITIAL = 192;  // fila 8 × 24 px
    private static final String SPRITE = "ghost_pink.png";

    public GhostPinky() {
        super(SPRITE,X_INITIAL, Y_INITIAL,  DIR_LEFT, 1);
    }

    @Override
    public void restart() {
        setX(X_INITIAL);
        setY(Y_INITIAL);
        setSprite(uploadImage(SPRITE));
        setSprite(getSprite());
        setDirection(DIR_LEFT);
        setStateEatable(false);
        setActive(true);
    }

    /**
     * IA de persecución idéntica a Blinky pero sin aumento de velocidad.
     */
    @Override
    public void changeDirection(int posPacX, int posPacY) {
        int d = getDirection();
        if (d == DIR_LEFT || d == DIR_RIGHT) {
            if (posPacY < getY()) setDirection(DIR_UP);
            else if (posPacY > getY()) setDirection(DIR_DOWN);
        } else if (d == DIR_UP || d == DIR_DOWN) {
            if (posPacX < getX()) setDirection(DIR_LEFT);
            else if (posPacX > getX()) setDirection(DIR_RIGHT);
        }
    }
}

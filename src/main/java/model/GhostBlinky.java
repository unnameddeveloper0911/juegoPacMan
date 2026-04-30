package model;
    /**
     * Fantasma rojo (Blinky). Persigue a Pacman y aumenta su velocidad
     * conforme Pacman acumula más puntos.
     */
    public class GhostBlinky extends Ghost {

        private static final int X_INICIAL = 216;  // col 9 × 24 px
        private static final int Y_INICIAL = 192;  // fila 8 × 24 px
        private static final String SPRITE = "ghost_red.png";

        public GhostBlinky() {
            super(SPRITE, X_INICIAL , Y_INICIAL, DIR_RIGHT, 1);
        }

        @Override
        public void restart() {
            setX(X_INICIAL);
            setY(Y_INICIAL);
            setSprite(uploadImage(SPRITE));
            setSprite(getSprite());
            setDirection(DIR_RIGHT);
            setStateEatable(false);
            setActive(true);
            setSpeed(1);
        }

        /**
         * Método persecución: cuando choca con un muro cambia al eje donde Pacman
         * está más lejos. Alterna ejes (X ↔ Y) según la dirección previa.
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

        /**
         * Aumenta la velocidad de Blinky según los puntos de Pacman.
         */
        public void comprobarVelocidad(int puntosPacman) {
            if (puntosPacman > 300) setSpeed(4);
            else if (puntosPacman > 200) setSpeed(3);
            else if (puntosPacman > 100) setSpeed(2);
            else setSpeed(1);
        }
    }

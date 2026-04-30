
package model;

/**
 * Tablero/laberinto del juego. Contiene el diseño del nivel (mapa 21×21)
 * y construye la matriz de objetos (Muro, Punto, SuperPunto o vacío).
 * <p>
 * Códigos del nivel:
 * 0  = celda vacía (transitable)
 * 1  = muro
 * 2  = punto
 * 3  = super-punto
 * -1  = puerta de la jaula de fantasmas (transitable, no es muro)
 */
public class Map {

    /**
     * Tamaño en píxeles de cada celda.
     */
    public static final int TAM_CELDA = 24;

    /**
     * Número de filas del mapa.
     */
    public static final int ROWS = 21;

    /**
     * Número de columnas del mapa.
     */
    public static final int COLUMNS = 21;

    /**
     * Matriz de objetos instanciados.
     */
    private ObjectGame[][] objects;

    /**
     * Diseño del nivel 21×21.
     * Fila 9 es el túnel lateral (las columnas 0-3 y 17-20 son vacías fuera del muro).
     * La casa de fantasmas está en las filas 7-10, columnas 8-12.
     * Pacman empieza en fila 18, col 10 (centro-inferior).
     */
    private final int[][] firstLevel = {
            // col: 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20
            /* f0 */ {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            /* f1 */ {1, 3, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 1},
            /* f2 */ {1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 1, 2, 1},
            /* f3 */ {1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 1, 2, 1},
            /* f4 */ {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            /* f5 */ {1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 2, 1},
            /* f6 */ {1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1},
            /* f7 */ {1, 1, 1, 1, 2, 1, 2, 1, 0, 0, 1, 0, 0, 1, 2, 1, 2, 1, 1, 1, 1},
            /* f8 */ {1, 1, 1, 1, 2, 1, 2, 1, 0, 0, -1, 0, 0, 1, 2, 1, 2, 1, 1, 1, 1},
            /* f9 */ {0, 0, 0, 0, 2, 0, 2, 0, 0, 1, 1, 1, 0, 0, 2, 0, 2, 0, 0, 0, 0},
            /*f10 */ {1, 1, 1, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 1, 1, 1},
            /*f11 */ {1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1},
            /*f12 */ {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            /*f13 */ {1, 2, 1, 1, 2, 1, 1, 1, 2, 2, 1, 2, 2, 1, 1, 1, 2, 1, 1, 2, 1},
            /*f14 */ {1, 3, 2, 1, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 1, 2, 3, 1},
            /*f15 */ {1, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 2, 1, 1},
            /*f16 */ {1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1},
            /*f17 */ {1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1},
            /*f18 */ {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            /*f19 */ {1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 2, 1},
            /*f20 */ {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    public Map() {
        uploadMap();
    }

    /**
     * Crea los objetos del tablero a partir del planoNivel.
     * Se llama en el constructor y también al reiniciar la partida.
     */
    public final void uploadMap() {
        objects = new ObjectGame[ROWS][COLUMNS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int code = firstLevel[row][col];
                int px = col * TAM_CELDA;
                int py = row * TAM_CELDA;
                if (code == 1) objects[row][col] = new Wall(px, py);
                else if (code == 2) objects[row][col] = new Point(px, py);
                else if (code == 3) objects[row][col] = new SuperPoint(px, py);
                else objects[row][col] = new ObjectGame();
            }
        }
    }

    public ObjectGame[][] getObjects() {
        return objects;
    }

    public int getFilas() {
        return ROWS;
    }

    public int getColumnas() {
        return COLUMNS;
    }
}


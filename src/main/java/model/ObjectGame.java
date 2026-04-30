package model;

import java.awt.image.BufferedImage;

/**
 * Clase base de los objetos estáticos del tablero: muros, puntos y super-puntos.
 * Los objetos estáticos no se mueven, por eso actualizar() está vacío.
 */
public class ObjectGame extends Entity {

    // Constantes de tipo (reemplaza al enum TipoObjeto)
    public static final int EMPTY = 0;  //VACIO
    public static final int WALL = 1;  //MURO
    public static final int POINT = 2; //PUNTO
    public static final int SUPER_POINT = 3;  //SUPER PUNTO

    /**
     * Indica de qué tipo es este objeto (VACIO, MURO, PUNTO o SUPER_PUNTO).
     */
    private int type;

    /**
     * Constructor para celdas vacías (sin sprite ni colisión).
     */
    public ObjectGame() {
        super(0, 0, null);
        this.type = EMPTY;
        setActive(false);
    }

    /**
     * Constructor para objetos con posición, sprite y tipo.
     */
    public ObjectGame(int x, int y, BufferedImage sprite, int type) {
        super(x, y, sprite);
        this.type = type;
    }

    @Override
    public void update() {
        // OJOOOOOOOOOOOOOOOOOO CON ESOOOOOOOOOOO Los objetos estáticos no se actualizan
    }

    public int getType() {
        return type;
    }
}


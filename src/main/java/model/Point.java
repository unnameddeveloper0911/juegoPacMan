package model;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * El punto es estatico.
 * En la matriz esta representada por el nuemero 2
 * Cuando PacMan se lo come le dan 10 puntos
 */

public class Point extends ObjectGame{
    public static final int VALUE=10;

    public Point(int x, int y){
        super(x,y,uploadImage("pill.png"),POINT);
    }
}

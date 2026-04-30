package model;

/**
 * Clase principal the PacMan, getsiona su movimiento
 * <p>
 * "Look, it's PAcMAAAN. Waka Waka" -Unknow
 */

public class Wall extends ObjectGame{



    public Wall(int x, int y){
        super(x,y,uploadImage("Wall.png"),SUPER_POINT);
    }

}

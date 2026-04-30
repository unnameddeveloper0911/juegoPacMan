package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Clase abtracta base para todas las entidades del juego.
 * Define posición, sprite, dirección y posición.
 * Las subclases deben implementar el metodo actualizar()
 */

public abstract class Entity {

    //Constantes manejo dirección entidad
    protected static final int DIR_NONE=0;
    protected static final int DIR_LEFT=1;
    protected static final int DIR_UP=2;
    protected static final int DIR_RIGHT=3;
    protected static final int DIR_DOWN=4;


    private int x;
    private int y;
    private int width;
    private int height;
    private BufferedImage sprite;
    private boolean active;
    private int direction;

    public Entity(BufferedImage sprite,int x, int y ) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;

        if(sprite != null){
            this.width = sprite.getWidth();
            this.height = sprite.getHeight();
        }

        this.active=true;
        this.direction=DIR_NONE;
    }

    /**
     * Cada clase define su propia clase de actualización de entidad
     */

    public abstract void update();

    /**
     * Metdodo para verificar colisión
     */

    public Rectangle getHitBox(){
        return new Rectangle(x,y,width,height);
    }

    /**
     * Clase para predecir una colisión futura
     * @param px
     * @param py
     * @return
     */

    public Rectangle getHitBox(int px, int py){
        return new Rectangle(px,py,width,height);
    }

    /**
     * Calcula el desplazamiento horizontal según la dirección actual.
     * Devuelve -1, 0 o +1.
     */
    public int calculateDx() {
        if (direction == DIR_LEFT) return -1;
        if (direction == DIR_RIGHT) return 1;
        return 0;
    }

    /**
     * Calcula el desplazamiento vertical según la dirección actual.
     * Devuelve -1, 0 o +1.
     */
    public int calculateDy() {
        if (direction == DIR_UP) return -1;
        if (direction == DIR_DOWN) return 1;
        return 0;
    }

    /**
     * Carga una imagen desde la carpeta de recursos.
     * Uso: Entidad.cargarImagen("pacman_left.png")
     */
    public static BufferedImage uploadImage(String nombre) {
        try {
            InputStream is = Entity.class.getResourceAsStream("/resources/images/" + nombre);
            if (is == null) {
                System.err.println("Imagen no encontrada: " + nombre);
                return null;
            }
            return ImageIO.read(is);
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + nombre);
            return null;
        }
    }

    //getters y setters

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;

        if(sprite != null){
            this.width = sprite.getWidth();
            this.height = sprite.getHeight();
        }

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }



}

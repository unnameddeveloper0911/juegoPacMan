package model;

import java.awt.image.BufferedImage;

/**
 * Clase abstracta de la cual se originaran los fantasmas
 * Define su comportamiento
 */

public abstract class Ghost extends Entity {

    //constante tiempo de estado comestible
    //540= 9s * 60fps
    private static final int DURATION_EATABLE=540;

    private int speed; //velocidad
    private boolean stateEatable; //estado comestible
    private int countEatable; //contador de comestible
    private BufferedImage originalSprite;

    public Ghost(String sprite,int x, int y, int dirInitial, int speed) {
        super(uploadImage(sprite), x, y);
        this.speed=speed;
        this.stateEatable=false;
        this.countEatable=0;
        this.originalSprite=getSprite();
        setDirection(dirInitial);

    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isStateEatable() {
        return stateEatable;
    }

    public int getCountEatable() {
        return countEatable;
    }

    public void setCountEatable(int countEatable) {
        this.countEatable = countEatable;
    }

    public BufferedImage getOriginalSprite() {
        return originalSprite;
    }

    public void setOriginalSprite(BufferedImage originalSprite) {
        this.originalSprite = originalSprite;
    }

    /**
     * Cada subclase decide como cambiar de dirección al chocar con un muro
     */

    public abstract void changeDirection(int posPacX, int posPacY);

    /**
     * Reiniciar el fantasma a su estado y posición inicial
     */

    public abstract void restart();


    /**
     * Metodo para invertir la dirección del fantasma
     */

    public void reverseDirection() {
        int direction=getDirection();
        if(direction==DIR_LEFT){
            setDirection(DIR_RIGHT);
        }
        else if(direction==DIR_RIGHT){
            setDirection(DIR_LEFT);
        }else if(direction==DIR_UP){
            setDirection(DIR_DOWN);
        }else if(direction==DIR_DOWN){
            setDirection(DIR_UP);
        }
    }

    /**
     * Activar o desactivar estado comible
     */

    public void setStateEatable(boolean active) {
        if(active){
            stateEatable=true;
            countEatable = DURATION_EATABLE;
            setSprite(uploadImage("ghost_teal.png"));
        }
    }

    /**
     *Se sobreescribe el metodo update() para mover a los fantasmas
     * y descontar el contador comestible
     */

    @Override
    public void update(){
        setX(getX()+calculateDx()*speed);
        setY(getY()+calculateDy()*speed);

        if(stateEatable){
            countEatable--;
            if(countEatable<=0){
                stateEatable=false;
                setSprite(originalSprite);
            }
        }
    }




}

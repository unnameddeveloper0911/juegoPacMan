package controller;

import model.Entity;
import model.PacMan;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Manejador de teclado. Traduce las teclas del jugador en cambios sobre el
 * modelo Pacman (dirección, pausa y enter).
 */
public class InputHandler extends KeyAdapter {

    private final PacMan pacman;

    public InputHandler(PacMan pacman) {
        this.pacman = pacman;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();

        if (tecla == KeyEvent.VK_LEFT) {
            pacman.setDirection(Entity.DIR_LEFT);
            pacman.updateSprite(Entity.DIR_LEFT);
        } else if (tecla == KeyEvent.VK_RIGHT) {
            pacman.setDirection(Entity.DIR_RIGHT);
            pacman.updateSprite(Entity.DIR_RIGHT);
        } else if (tecla == KeyEvent.VK_UP) {
            pacman.setDirection(Entity.DIR_UP);
            pacman.updateSprite(Entity.DIR_UP);
        } else if (tecla == KeyEvent.VK_DOWN) {
            pacman.setDirection(Entity.DIR_DOWN);
            pacman.updateSprite(Entity.DIR_DOWN);
        } else if (tecla == KeyEvent.VK_P) {
            pacman.updatePaused();
        } else if (tecla == KeyEvent.VK_ENTER) {
            pacman.setEnter(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();

        if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_RIGHT ||
                tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_DOWN) {
            pacman.setDirection(Entity.DIR_NONE);
        } else if (tecla == KeyEvent.VK_ENTER) {
            pacman.setEnter(false);
        }
    }
}
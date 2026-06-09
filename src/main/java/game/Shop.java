package game;

import helpers.InputHandler;

//TODO: DECIDE WHETHER IN-GAME SHOP OR UI SHOP
//ACTUALLY BOTH CAN BE IMPLEMENTED INSIDE GAME OR SELECTION CLASSES?
public class Shop {

    private final InputHandler inputHandler;
    private final GameState gameState;

    public Shop(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
    }

    public void shopLoop() {

    }

}

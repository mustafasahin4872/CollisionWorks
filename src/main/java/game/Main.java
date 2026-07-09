package game;

import helpers.InputHandler;
import game.GameState.STATE;

public class Main {

    public static final String RESOURCES_ROOT = "src/main/resources/";
    public static final String IMAGES_ROOT = RESOURCES_ROOT + "images/";

    public static GameState gameState;
    public static InputHandler inputHandler;

    public static final Long GAME_START = System.currentTimeMillis();

    // manage selection, in-game, shop screen calls according to gameState
    // create corresponding maps and frames, then run and edit gameState
    public static void main(String[] args) throws Exception {

        Frame.setCanvas();
        gameState = new GameState();
        inputHandler = new InputHandler();

        MainSelection mainSelection = new MainSelection(inputHandler, gameState);
        AccessorySelection accessorySelection = new AccessorySelection(inputHandler, gameState);
        LevelSelection levelSelection = new LevelSelection(inputHandler, gameState);
        Game game = new Game(inputHandler, gameState);
        Shop shop = new Shop(inputHandler, gameState);

        while (gameState.getState() != STATE.QUIT) {

            switch (gameState.getState()) {
                case SELECTION -> mainSelection.mainSelectionLoop();
                case ACCESSORY -> accessorySelection.accessorySelectionLoop();
                case GAME -> levelSelection.levelSelectionLoop();
                case SHOP, ALTERNATE1, ALTERNATE2 -> shop.shopLoop();
                case NEXT, PASSED -> game.gameLoop();
                // case SHOP is handled inside gameLoop and selectionLoop
                // case PAUSE and DEAD is handled inside gameLoop
                // case QUIT is unreachable.
            }
        }

    }



}

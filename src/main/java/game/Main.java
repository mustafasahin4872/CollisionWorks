package game;

import helpers.InputHandler;

public class Main {

    public static final String RESOURCES_ROOT = "src/main/resources/";
    public static final String IMAGES_ROOT = RESOURCES_ROOT + "images/";

    public static GameState gameState;
    public static InputHandler inputHandler;

    public static final Long GAME_START = System.currentTimeMillis();

    // manage selection, in-game, shop screen calls according to gameState
    // create corresponding maps and frames, then run and edit gameState
    public static void main(String[] args){

        Frame.setCanvas();
        gameState = new GameState();
        inputHandler = new InputHandler();

        SkinSelection skinSelection = new SkinSelection(inputHandler, gameState);
        LevelSelection levelSelection = new LevelSelection(inputHandler, gameState);
        Shop shop = new Shop(inputHandler, gameState);
        Game game = new Game(inputHandler, gameState);

        while (gameState.getState() != GameState.STATE.QUIT) {

            switch (gameState.getState()) {
                case SELECTION -> skinSelection.skinSelectionLoop();
                case GAME -> levelSelection.levelSelectionLoop();
                case SHOP -> shop.shopLoop();
                case NEXT -> game.gameLoop();
                case PASSED -> {
                    gameState.nextLevel();
                    game.gameLoop();
                }
                case ALTERNATE1 -> {} // add alternate1
                case ALTERNATE2 -> {} // add alternate2
                // case SHOP is handled inside gameLoop and selectionLoop
                // case PAUSE and DEAD is handled inside gameLoop
                // case QUIT is unreachable.
            }
        }

    }



}

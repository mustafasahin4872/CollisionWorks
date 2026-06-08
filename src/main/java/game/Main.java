package game;

public class Main {

    public static final String RESOURCES_ROOT = "src/main/resources/";
    public static final String IMAGES_ROOT = RESOURCES_ROOT + "images/";

    public static GameState gameState;
    public static InputHandler inputHandler;

    // manage selection, in-game, shop screen calls according to gameState
    // create corresponding maps and frames, then run and edit gameState
    public static void main(String[] args){

        Frame.setCanvas();
        gameState = new GameState();
        inputHandler = new InputHandler();

        Selection selection = new Selection(inputHandler, gameState);
        Game game = new Game(inputHandler, gameState);
        Shop shop = new Shop(inputHandler, gameState);

        while (gameState.getState() != GameState.STATE.QUIT) {

            switch (gameState.getState()) {
                case SELECTION -> selection.selectionLoop();
                case NEXT -> game.gameLoop();
                case PASSED -> {
                    gameState.nextLevel();
                    game.gameLoop();
                }
                case ALTERNATE1 -> {} // add alternate1
                case ALTERNATE2 -> {} // add alternate2
                case SHOP -> shop.shopLoop();
                // case PAUSE and DEAD is handled inside gameLoop
                // case QUIT is unreachable.
            }
        }

    }



}

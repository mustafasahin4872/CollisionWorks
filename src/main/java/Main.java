import game.core.Game;
import game.core.GameState;
import game.io.Frame;
import game.ui.AccessorySelection;
import game.ui.LevelSelection;
import game.ui.MainSelection;
import game.ui.Shop;
import game.io.InputHandler;
import game.core.GameState.STATE;

public class Main {

    // manage selection, in-game, shop screen calls according to gameState
    // create corresponding maps and frames, then run and edit gameState
    public static void main(String[] args) throws Exception {

        Frame.setCanvas();
        GameState.gameState = new GameState();
        final InputHandler inputHandler = new InputHandler();

        MainSelection mainSelection = new MainSelection(inputHandler, GameState.gameState);
        AccessorySelection accessorySelection = new AccessorySelection(inputHandler, GameState.gameState);
        LevelSelection levelSelection = new LevelSelection(inputHandler, GameState.gameState);
        Game game = new Game(inputHandler, GameState.gameState);
        Shop shop = new Shop(inputHandler, GameState.gameState);

        while (GameState.gameState.getState() != STATE.QUIT) {

            switch (GameState.gameState.getState()) {
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

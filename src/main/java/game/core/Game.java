package game.core;

import game.io.Frame;
import game.ui.GameScreen;
import game.io.InputHandler;
import mapobjects.entities.Player;
import game.core.GameState.STATE;
import game.io.InputHandler.ArrowData;
import game.io.InputHandler.MouseData;


// create the necessary map and run the while loop
public class Game {

    private final InputHandler inputHandler;
    private final GameState gameState;

    public Game(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
    }

    public void gameLoop() {

        // create the corresponding NORMAL or IN_BETWEEN map
        GameMap gameMap;
        if (gameState.getState() == STATE.PASSED) {
            gameMap = new GameMap(gameState, MapMaker.MapType.IN_BETWEEN);
        } else { // STATE.NEXT
            gameMap = new GameMap(gameState, MapMaker.MapType.NORMAL);
        }

        Frame frame = new Frame(gameMap.getWidth(), gameMap.getHeight());
        GameScreen gameScreen = new GameScreen(gameState);
        Player player = gameState.getPlayer();

        gameState.setState(STATE.GAME);

        while (gameState.getState() == STATE.GAME || gameState.getState() == STATE.PAUSE || gameState.getState() == STATE.DEAD) {

            long startTime = System.currentTimeMillis();

            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();
            gameScreen.processInput(mouseData);

            if (gameState.getState() == STATE.GAME) {
                //takes input and sets regarding player data fields
                ArrowData arrowData = inputHandler.getArrowData();
                //uses moveDirection to update velocities
                player.acceptInput(arrowData);
                player.updateVelocity();
                //checks for collisions with player and the on tile effects, also updates the map objects
                gameMap.callMapObjects();
                //updates player position and other attributes at last
                player.update();
            }

            //sets the frame to center player if it is not in the edges of gameMap
            //if the frame would get out of the gameMap, set frame to show up to edge
            double[] frameCenter = frame.setFrameCenter(player.getX(), player.getY());
            gameScreen.updateValues(frameCenter[0], frameCenter[1]);
            gameMap.setFrameTileRange();

            //draws everything
            draw(gameMap, player, gameScreen);

            player.resetInput();

            int timeElapsed = (int)(System.currentTimeMillis()- startTime);
            if (timeElapsed<Frame.PAUSE) {
                Frame.pause(Frame.PAUSE-timeElapsed);
            }

        }

        if (gameState.getState() == STATE.PASSED) {
            gameState.nextLevel();
        }

    }

    private static void draw(GameMap gameMap, Player player, GameScreen gameScreen) {
        Frame.clear();
        gameMap.draw();
        player.draw();
        player.drawProjectiles();
        player.drawAccessories();
        gameScreen.draw();
        Frame.show();
    }


}

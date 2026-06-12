package game;

import helpers.InputHandler;
import lib.StdDraw;

import java.awt.*;

import static helpers.DrawMethods.drawText;

// The shop screen inside selection
public class Shop {

    private final InputHandler inputHandler;
    private final GameState gameState;

    public Shop(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
    }

    private final GameMap backgroundMap = new GameMap(new GameState(1, -1), helpers.MapType.SELECTION);

    public void shopLoop() {
        while (gameState.getState() == GameState.STATE.SHOP) {
            inputHandler.takeInput();
            InputHandler.MouseData mouseData = inputHandler.getMouseData();
            InputHandler.ArrowData arrowData = inputHandler.getArrowData();

            processInput(mouseData, arrowData);

            StdDraw.clear();
            StdDraw.setXscale(0, Frame.X_SCALE);
            StdDraw.setYscale(Frame.Y_SCALE, 0);

            backgroundMap.draw();
            draw();

            StdDraw.show();
            StdDraw.pause(8 * Frame.PAUSE);
        }
    }

    private void processInput(InputHandler.MouseData mouseData, InputHandler.ArrowData arrowData) {
        if (mouseData.pressed) {
            gameState.setState(GameState.STATE.SELECTION);
        }
    }

    private void draw() {
        Font titleFont = new Font("Monospaced", Font.BOLD, 50);
        drawText("SHOP SCREEN DUMMY", Frame.X_SCALE / 2.0, Frame.Y_SCALE / 2.0, titleFont, StdDraw.WHITE);

        Font subFont = new Font("Monospaced", Font.PLAIN, 20);
        drawText("(Click anywhere to return)", Frame.X_SCALE / 2.0, Frame.Y_SCALE / 2.0 + 50, subFont,
                StdDraw.GRAY);
    }

}

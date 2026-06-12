package game;

import helpers.InputHandler;
import helpers.MapType;
import lib.StdDraw;
import mapobjects.component.Box;

import java.awt.*;

import static helpers.CollisionMethods.isIn;
import static helpers.DrawMethods.*;
import static helpers.InputHandler.MouseData;
import static helpers.InputHandler.ArrowData;
import static mapobjects.category.GridObject.TILE_SIDE;

// governs level selection
// runs when gameState.getState() == STATE.GAME
public class LevelSelection {

    private final InputHandler inputHandler;
    private final GameState gameState;

    public static final GameMap[] WORLDS = {
            new GameMap(new GameState(1, 0), MapType.SELECTION),
            new GameMap(new GameState(2, 0), MapType.SELECTION),
            new GameMap(new GameState(3, 0), MapType.SELECTION),
            new GameMap(new GameState(4, 0), MapType.SELECTION)
    };

    public static final Color[] WORLD_COLORS = {
            new Color(119, 14, 155, 255),
            new Color(25, 127, 180),
            new Color(233, 116, 49),
            new Color(254, 208, 36)
    };

    private int currentWorldIndex = 0;
    private int currentLevelIndex = 0;

    private final NavigationUI navigationUI = new NavigationUI();
    private final LevelsUI levelsUI = new LevelsUI();

    public LevelSelection(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
    }

    public void levelSelectionLoop() {
        StdDraw.setXscale(0, Frame.X_SCALE);
        StdDraw.setYscale(Frame.Y_SCALE, 0);

        while (gameState.getState() == GameState.STATE.GAME) {
            inputHandler.takeInput();
            InputHandler.MouseData mouseData = inputHandler.getMouseData();
            InputHandler.ArrowData arrowData = inputHandler.getArrowData();

            processInput(mouseData, arrowData);

            StdDraw.clear();

            GameMap currentWorld = WORLDS[currentWorldIndex];
            currentWorld.draw();

            draw();

            StdDraw.show();
            StdDraw.pause(8 * Frame.PAUSE);
        }

        if (gameState.getState() == GameState.STATE.NEXT) {
            gameState.worldIndex = currentWorldIndex + 1;
            gameState.levelIndex = currentLevelIndex + 1;
            if (gameState.player != null) {
                gameState.player.setWorldIndex(currentWorldIndex + 1);
            }
        }
    }

    public void processInput(MouseData mouseData, ArrowData arrowData) {
        navigationUI.processInput(mouseData);
        levelsUI.processInput(mouseData, arrowData);
    }

    public void draw() {
        levelsUI.draw();
    }

    private class LevelsUI {

        public static final String[] WORLD_NAMES = {
                "THE SPRING FESTIVAL", "INTO THE ICE CAVE", "TO THE TOP OF THE VOLCANO", "CRYSTAL PALACE"
        };

        public static final double WORLD_NAME_Y_OFFSET = 2.5 * TILE_SIDE;

        public static final double LEVEL_BOX_SIZE = TILE_SIDE;
        public static final double LEVEL_BOX_START_X = 4.5 * TILE_SIDE;
        public static final double LEVEL_BOX_X_GAP = 3.0 * TILE_SIDE;
        public static final double LEVEL_BOX_START_Y_OFFSET = 4.5 * TILE_SIDE;
        public static final double LEVEL_BOX_Y_GAP = 2.0 * TILE_SIDE;

        public static final Box[] LEVEL_BOXES = new Box[12];

        static {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    LEVEL_BOXES[i * 4 + j] = new Box(
                            LEVEL_BOX_START_X + j * LEVEL_BOX_X_GAP,
                            LEVEL_BOX_START_Y_OFFSET + i * LEVEL_BOX_Y_GAP,
                            LEVEL_BOX_SIZE, LEVEL_BOX_SIZE);
                }
            }
        }

        public void processInput(MouseData mouseData, ArrowData arrowData) {

            boolean rightArrowPressed = arrowData.xDirection == 1;
            boolean leftArrowPressed = arrowData.xDirection == -1;
            boolean upArrowPressed = arrowData.yDirection == -1;
            boolean downArrowPressed = arrowData.yDirection == 1;

            if (rightArrowPressed)
                currentLevelIndex++;
            if (leftArrowPressed)
                currentLevelIndex--;
            if (downArrowPressed)
                currentLevelIndex += 4;
            if (upArrowPressed)
                currentLevelIndex -= 4;

            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean mousePressed = mouseData.pressed;

            if (mousePressed) {
                for (int i = 0; i < LEVEL_BOXES.length; i++) {
                    if (isIn(mouseX, mouseY, LEVEL_BOXES[i])) {
                        currentLevelIndex = i;
                    }
                }
            }

            currentLevelIndex = (currentLevelIndex + 12) % 12;

            if (arrowData.space) {
                gameState.setState(GameState.STATE.NEXT);
            }

        }

        public void draw() {

            Font titleFont = new Font("Monospaced", Font.BOLD, 30);
            drawText(WORLD_NAMES[currentWorldIndex], Frame.X_SCALE / 2.0, WORLD_NAME_Y_OFFSET, titleFont,
                    WORLD_COLORS[currentWorldIndex]);

            for (int i = 0; i < LEVEL_BOXES.length; i++) {
                Box currentButton = LEVEL_BOXES[i];

                Color outlineColor = (i == currentLevelIndex) ? StdDraw.WHITE : WORLD_COLORS[currentWorldIndex];
                StdDraw.setPenColor(outlineColor);
                drawRectangleOutline(currentButton, THICKNESS.THIN);

                textInsideBox(currentButton, String.valueOf(i + 1), StdDraw.WHITE, 16);
            }
        }
    }

    private class NavigationUI {

        public static final double ARROW_BOX_SIZE = 2.0 * TILE_SIDE;

        public static final Box[] LEFT_ARROW_BOX = {
                new Box(1 * TILE_SIDE, 5 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
                new Box(1 * TILE_SIDE, 5 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
                new Box(1 * TILE_SIDE, 5 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
                new Box(1 * TILE_SIDE, 8 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE)
        };

        public static final Box[] RIGHT_ARROW_BOX = {
                new Box(Frame.X_SCALE - 1 * TILE_SIDE, 8 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
                new Box(Frame.X_SCALE - 1 * TILE_SIDE, 7 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
                new Box(Frame.X_SCALE - 1 * TILE_SIDE, 4 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
                null // last world, no more levels
        };

        private void processInput(InputHandler.MouseData mouseData) {
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean mousePressed = mouseData.pressed;
            if (mousePressed) {
                if (isIn(mouseX, mouseY, LEFT_ARROW_BOX[currentWorldIndex])) {
                    if (LevelSelection.this.currentWorldIndex == 0) {
                        gameState.setState(GameState.STATE.SELECTION);
                    } else {
                        LevelSelection.this.currentWorldIndex--;
                        currentLevelIndex = 0;
                    }
                } else if (RIGHT_ARROW_BOX[currentWorldIndex] != null
                        && isIn(mouseX, mouseY, RIGHT_ARROW_BOX[currentWorldIndex])) {
                    LevelSelection.this.currentWorldIndex++;
                    currentLevelIndex = 0;
                }
            }

        }
    }

}

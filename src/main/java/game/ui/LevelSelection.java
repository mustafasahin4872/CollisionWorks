package game.ui;

import game.core.Frame;
import game.core.GameMap;
import game.core.GameState;
import game.core.MapMaker;
import game.io.InputHandler;
import helpers.utils.UIButton;
import helpers.utils.UIButton.*;
import lib.StdDraw;
import mapobjects.components.Box;

import java.awt.*;

import static helpers.methods.CollisionMethods.isIn;
import static helpers.methods.DrawMethods.*;
import static game.io.InputHandler.MouseData;
import static game.io.InputHandler.ArrowData;
import static mapobjects.traits.GridObject.TILE_SIDE;

// governs level selection
// runs when gameState.getState() == STATE.GAME
public class LevelSelection {

    private final InputHandler inputHandler;
    private final GameState gameState;

    public static final GameMap[] WORLDS = {
            new GameMap(new GameState(1, 0), MapMaker.MapType.SELECTION),
            new GameMap(new GameState(2, 0), MapMaker.MapType.SELECTION),
            new GameMap(new GameState(3, 0), MapMaker.MapType.SELECTION),
            new GameMap(new GameState(4, 0), MapMaker.MapType.SELECTION)
    };

    public static final Color[] WORLD_COLORS = {
            new Color(119, 14, 155, 255),
            new Color(25, 127, 180),
            new Color(233, 116, 49),
            new Color(244, 157, 8)
    };

    private static final int LEVELS_PER_ROW = 4;
    private static final int LEVEL_ROW_NUM = 3;

    private final Index worldIndex = new Index(WORLDS.length);
    private final Index levelIndex = new Index(LEVEL_ROW_NUM * LEVELS_PER_ROW);

    private final UIButton[] leftButtons = new UIButton[WORLDS.length];
    private final UIButton[] rightButtons = new UIButton[WORLDS.length - 1];
    private final LevelsUI levelsUI = new LevelsUI();

    public LevelSelection(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
        configureNavigationButtons();
    }

    private void configureNavigationButtons() {
        final double ARROW_BOX_SIZE = 2.0 * TILE_SIDE;

        final Box[] LEFT_ARROW_BOXES = {
            new Box(1 * TILE_SIDE, 5 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(1 * TILE_SIDE, 5 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(1 * TILE_SIDE, 5 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(1 * TILE_SIDE, 8 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE)
        };

        final Box[] RIGHT_ARROW_BOXES = {
            new Box(game.core.Frame.X_SCALE - 1 * TILE_SIDE, 8 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(game.core.Frame.X_SCALE - 1 * TILE_SIDE, 7 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(game.core.Frame.X_SCALE - 1 * TILE_SIDE, 4 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE)
            // last world, no more levels
        };

        for (int i = 0; i<LEFT_ARROW_BOXES.length; i++) {
            if (i == 0) {
                leftButtons[0] = new StateButton(LEFT_ARROW_BOXES[0], gameState, GameState.STATE.SELECTION);
            } else {
                leftButtons[i] = new IndexButton(LEFT_ARROW_BOXES[i], worldIndex, IndexButton.TYPE.DECREMENT);
            }
        }

        for (int i = 0; i<RIGHT_ARROW_BOXES.length; i++) {
            rightButtons[i] = new IndexButton(RIGHT_ARROW_BOXES[i], worldIndex, IndexButton.TYPE.INCREMENT);
        }
    }

    public void levelSelectionLoop() {
        StdDraw.setXscale(0, game.core.Frame.X_SCALE);
        StdDraw.setYscale(game.core.Frame.Y_SCALE, 0);

        while (gameState.getState() == GameState.STATE.GAME) {
            inputHandler.takeInput();
            InputHandler.MouseData mouseData = inputHandler.getMouseData();
            InputHandler.ArrowData arrowData = inputHandler.getArrowData();

            processInput(mouseData, arrowData);

            StdDraw.clear();

            GameMap currentWorld = WORLDS[worldIndex.getCurrent()];
            currentWorld.draw();

            draw();

            StdDraw.show();
            StdDraw.pause(game.core.Frame.PAUSE);
        }

        if (gameState.getState() == GameState.STATE.NEXT) {
            gameState.setWorldIndex(worldIndex.getCurrent() + 1);
            gameState.setLevelIndex(levelIndex.getCurrent() + 1);
            if (gameState.getPlayer() != null) {
                gameState.getPlayer().setWorldIndex(worldIndex.getCurrent() + 1);
            }
        }
    }

    public void processInput(MouseData mouseData, ArrowData arrowData) {
        leftButtons[worldIndex.getCurrent()].processInput(mouseData, arrowData);
        if (worldIndex.getCurrent() != WORLDS.length - 1) {
            rightButtons[worldIndex.getCurrent()].processInput(mouseData, arrowData);
        }
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

        public static final Box[] LEVEL_BOXES = new Box[LEVELS_PER_ROW * LEVEL_ROW_NUM];

        static {
            for (int i = 0; i < LEVEL_ROW_NUM; i++) {
                for (int j = 0; j < LEVELS_PER_ROW; j++) {
                    LEVEL_BOXES[i * LEVELS_PER_ROW + j] = new Box(
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
                levelIndex.increment();
            if (leftArrowPressed)
                levelIndex.decrement();
            if (downArrowPressed)
                levelIndex.increment(4);
            if (upArrowPressed)
                levelIndex.decrement(4);

            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean clicked = mouseData.clicked;

            if (clicked) {
                for (int i = 0; i < LEVEL_BOXES.length; i++) {
                    if (isIn(mouseX, mouseY, LEVEL_BOXES[i])) {
                        levelIndex.setValue(i);
                    }
                }
            }

            if (arrowData.space) {
                gameState.setState(GameState.STATE.NEXT);
            }

        }

        public void draw() {

            Font titleFont = new Font("Monospaced", Font.BOLD, 30);
            drawText(WORLD_NAMES[worldIndex.getCurrent()], Frame.X_SCALE / 2.0, WORLD_NAME_Y_OFFSET, titleFont,
                    WORLD_COLORS[worldIndex.getCurrent()]);

            for (int i = 0; i < LEVEL_BOXES.length; i++) {
                Box currentButton = LEVEL_BOXES[i];

                Color outlineColor = (i == levelIndex.getCurrent()) ? StdDraw.WHITE : WORLD_COLORS[worldIndex.getCurrent()];
                drawRectangleOutline(currentButton, outlineColor, THICKNESS.THIN);

                Font font = new Font("Arial", Font.PLAIN, 16);
                textInsideBox(currentButton, String.valueOf(i + 1), StdDraw.WHITE, font);
            }
        }
    }

}

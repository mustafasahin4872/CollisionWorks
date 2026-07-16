package game.ui;

import game.io.Frame;
import game.core.GameMap;
import game.core.GameState;
import game.core.MapMaker;
import game.io.InputHandler;
import game.ui.components.Index;
import game.ui.components.UIButton;
import game.ui.components.UIButton.*;
import mapobjects.components.Box;
import game.io.Drawer.THICKNESS;
import game.io.Drawer.TextDrawer;
import game.io.Drawer.OutlineDrawer;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static helpers.CollisionMethods.isIn;
import static game.io.InputHandler.MouseData;
import static game.io.InputHandler.ArrowData;
import static mapobjects.traits.schemas.GridObject.TILE_SIDE;

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
            new Box(Frame.X_SCALE - 1 * TILE_SIDE, 8 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(Frame.X_SCALE - 1 * TILE_SIDE, 7 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(Frame.X_SCALE - 1 * TILE_SIDE, 4 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE)
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
        Frame.setDefaultScale();

        while (gameState.getState() == GameState.STATE.GAME) {
            inputHandler.takeInput();
            InputHandler.MouseData mouseData = inputHandler.getMouseData();
            InputHandler.ArrowData arrowData = inputHandler.getArrowData();

            processInput(mouseData, arrowData);

            Frame.clear();

            GameMap currentWorld = WORLDS[worldIndex.getCurrent()];
            currentWorld.draw();

            draw();

            Frame.show();
            Frame.pause(Frame.PAUSE);
        }

        if (gameState.getState() == GameState.STATE.NEXT) {
            gameState.setWorldIndex(worldIndex.getCurrent() + 1);
            gameState.setLevelIndex(levelsUI.getLevelIndex() + 1);
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
        private static final Box WORLD_NAME_BOX = new Box(Frame.X_SCALE / 2.0, WORLD_NAME_Y_OFFSET, 0, 0);

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

        private final Index levelIndex = new Index(LEVEL_ROW_NUM * LEVELS_PER_ROW);
        private final Set<GenericIndexKey> arrowKeys = new HashSet<>(Set.of(
            new GenericIndexKey(GenericIndexKey.GenericKey.RIGHT_ARROW, levelIndex, 1),
            new GenericIndexKey(GenericIndexKey.GenericKey.LEFT_ARROW, levelIndex, -1),
            new GenericIndexKey(GenericIndexKey.GenericKey.UP_ARROW, levelIndex, -4),
            new GenericIndexKey(GenericIndexKey.GenericKey.DOWN_ARROW, levelIndex, 4)
        ));

        private final OutlineDrawer[] outlineDrawers = new OutlineDrawer[LEVELS_PER_ROW*LEVEL_ROW_NUM];
        private final TextDrawer[] textDrawers = new TextDrawer[LEVELS_PER_ROW*LEVEL_ROW_NUM];
        private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 30);
        private final TextDrawer worldNameDrawer = new TextDrawer(WORLD_NAME_BOX, TITLE_FONT);

        public LevelsUI() {

            Font textFont = new Font("Arial", Font.PLAIN, 16);
            for (int i = 0; i<LEVELS_PER_ROW * LEVEL_ROW_NUM; i++) {
                outlineDrawers[i] = new OutlineDrawer(LEVEL_BOXES[i], Color.BLACK, THICKNESS.THIN);
                textDrawers[i] = new TextDrawer(LEVEL_BOXES[i], String.valueOf(i + 1), Color.WHITE, textFont);
            }

        }

        private int getLevelIndex() {
            return levelIndex.getCurrent();
        }

        public void processInput(MouseData mouseData, ArrowData arrowData) {

            for (GenericIndexKey arrowKey : arrowKeys) arrowKey.processInput(mouseData, arrowData);

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

            worldNameDrawer.setText(WORLD_NAMES[worldIndex.getCurrent()]);
            worldNameDrawer.setTextColor(WORLD_COLORS[worldIndex.getCurrent()]);
            worldNameDrawer.draw();

            for (int i = 0; i < LEVEL_BOXES.length; i++) {
                Box currentButton = LEVEL_BOXES[i];

                Color outlineColor = (i == levelIndex.getCurrent()) ? Color.WHITE : WORLD_COLORS[worldIndex.getCurrent()];
                outlineDrawers[i].setOutlineColor(outlineColor);
                outlineDrawers[i].draw();
                textDrawers[i].draw();
            }
        }
    }

}

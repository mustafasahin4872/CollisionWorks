package game;

import game.InputHandler.MouseData;
import game.InputHandler.ArrowData;
import game.GameState.STATE;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;

import java.awt.*;
import java.util.Arrays;

import static game.Selection.accessories;
import static mapobjects.category.GridObject.TILE_SIDE;
import static helpers.CollisionMethods.isIn;
import static helpers.DrawMethods.drawRectangleOutline;
import static helpers.DrawMethods.textInsideBox;
import static helpers.DrawMethods.drawText;

public class SelectionScreen {

    public static final Color[] WORLD_COLORS = {
            new Color(1, 1, 1),
            new Color(119, 14, 155, 255),
            new Color(25, 127, 180),
            new Color(233, 116, 49),
            new Color(199, 193, 189)
    };

    private final GameState gameState;

    private final LevelSelectionUI levelSelectionUI = new LevelSelectionUI();
    private final PrimaryUI primaryUI = new PrimaryUI();
    private final ShopUI shopUI = new ShopUI();

    private int currentSkinIndex = 0;
    private int currentAccessoryIndex = 0;
    private final boolean[] accessoryChosen = new boolean[accessories.length];
    private int currentWorldIndex = 1;
    private int currentLevelIndex = 1;


    public SelectionScreen(GameState gameState) {
        this.gameState = gameState;
    }

    public int getCurrentWorldIndex() {
        return currentWorldIndex;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public int getCurrentSkinIndex() {
        return currentSkinIndex;
    }

    public int getCurrentAccessoryIndex() {
        return currentAccessoryIndex;
    }

    public boolean[] getAccessoryChosen() {
        return accessoryChosen;
    }

    public void processInput(MouseData mouseData, ArrowData arrowData) {
        STATE state = gameState.getState();

        if (state == STATE.GAME) {
            levelSelectionUI.processInput(mouseData, arrowData);
        } else if (state == STATE.SELECTION) {
            primaryUI.processInput(mouseData, arrowData);
        } else if (state == STATE.SHOP) {
            shopUI.processInput(mouseData, arrowData);
        }

    }

    public void draw() {

        STATE state = gameState.getState();

        if (state == STATE.GAME) {
            levelSelectionUI.draw();
        } else if (state == STATE.SELECTION) {
            primaryUI.draw();
        } else if (state == STATE.SHOP) {
            shopUI.draw();
        }
    }

    // ----------------------------------------------------------------------------------------------------------
    // UI CLASSES

    private class ShopUI {
        private void processInput(MouseData mouseData, ArrowData arrowData) {
            if (mouseData.pressed) {
                gameState.setState(STATE.SELECTION);
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

    // governs level selection and moving in between worlds / primary ui
    private class LevelSelectionUI {

        public static final String[] WORLD_NAMES = {
                "THE SPRING FESTIVAL", "INTO THE ICE CAVE", "TO THE TOP OF THE VOLCANO" // crystal palace
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

        private final NavigationUI navigationUI = new NavigationUI();

        private void processInput(MouseData mouseData, ArrowData arrowData) {

            navigationUI.processInput(mouseData);

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
                        currentLevelIndex = i + 1;
                    }
                }
            }

            currentLevelIndex = ((currentLevelIndex - 1) % 12 + 12) % 12 + 1;


            if (arrowData.space) {
                gameState.setState(STATE.NEXT);
            }

        }

        private void draw() {

            Font titleFont = new Font("Monospaced", Font.BOLD, 30);
            drawText(WORLD_NAMES[currentWorldIndex - 1], Frame.X_SCALE / 2.0, WORLD_NAME_Y_OFFSET, titleFont,
                    WORLD_COLORS[currentWorldIndex]);

            for (int i = 0; i < LEVEL_BOXES.length; i++) {
                Box currentButton = LEVEL_BOXES[i];

                Color outlineColor = (i + 1 == currentLevelIndex) ? StdDraw.WHITE : WORLD_COLORS[currentWorldIndex];
                StdDraw.setPenColor(outlineColor);
                drawRectangleOutline(currentButton);

                textInsideBox(currentButton, String.valueOf(i + 1), StdDraw.WHITE, 16);
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
                    null
            };

            private void processInput(MouseData mouseData) {
                double mouseX = mouseData.mouseX;
                double mouseY = mouseData.mouseY;
                boolean mousePressed = mouseData.pressed;
                int index = currentWorldIndex - 1;

                if (mousePressed) {
                    if (isIn(mouseX, mouseY, LEFT_ARROW_BOX[index])) {
                        if (currentWorldIndex == 1) {
                            gameState.setState(STATE.SELECTION);
                        } else {
                            currentWorldIndex--;
                            currentLevelIndex = 1;
                        }
                    } else if (RIGHT_ARROW_BOX[index] != null && isIn(mouseX, mouseY, RIGHT_ARROW_BOX[index])) {
                        currentWorldIndex++;
                        currentLevelIndex = 1;
                    }
                }

            }
        }

    }

    // governs skin selection, accessory selection and moving to level selection - shop
    private class PrimaryUI {

        private final NavigationUI navigationUI = new NavigationUI();
        private final SkinSelectionUI skinSelectionUI = new SkinSelectionUI();
        private final AccessorySelectionUI accessorySelectionUI = new AccessorySelectionUI();

        private void processInput(MouseData mouseData, ArrowData arrowData) {
            navigationUI.processInput(mouseData);
            skinSelectionUI.processInput(mouseData, arrowData);
            accessorySelectionUI.processInput(mouseData);
        }

        private void draw() {
            skinSelectionUI.draw();
            accessorySelectionUI.draw();
        }

        private class SkinSelectionUI {

            private final Player[] skins = Selection.skins;

            private static final double PLAYER_NAME_Y_OFFSET = 2.15 * TILE_SIDE;
            private static final double SKIN_BOX_SIZE = TILE_SIDE;
            private static final double SKIN_LEFT_X = 5.5 * TILE_SIDE;
            private static final double SKIN_RIGHT_X_OFFSET = 7.5 * TILE_SIDE;

            private static final Box SKIN_LEFT_BOX = new Box(SKIN_LEFT_X, PLAYER_NAME_Y_OFFSET, SKIN_BOX_SIZE,
                    SKIN_BOX_SIZE);
            private static final Box SKIN_RIGHT_BOX = new Box(Frame.X_SCALE - SKIN_RIGHT_X_OFFSET,
                    PLAYER_NAME_Y_OFFSET, SKIN_BOX_SIZE, SKIN_BOX_SIZE);

            private void processInput(MouseData mouseData, ArrowData arrowData) {
                double mouseX = mouseData.mouseX;
                double mouseY = mouseData.mouseY;
                boolean mousePressed = mouseData.pressed;

                boolean rightArrowPressed = arrowData.xDirection == 1;
                boolean leftArrowPressed = arrowData.xDirection == -1;

                if (rightArrowPressed)
                    currentSkinIndex++;
                if (leftArrowPressed)
                    currentSkinIndex--;

                if (mousePressed) {
                    if (isIn(mouseX, mouseY, SKIN_LEFT_BOX)) {
                        currentSkinIndex--;
                    } else if (isIn(mouseX, mouseY, SKIN_RIGHT_BOX)) {
                        currentSkinIndex++;
                    }
                }
                currentSkinIndex = (currentSkinIndex + skins.length) % skins.length;
            }

            private void draw() {
                Player currentSkin = skins[currentSkinIndex];
                Color color = StdDraw.BLACK;

                double centerX = (SKIN_LEFT_X + (Frame.X_SCALE - SKIN_RIGHT_X_OFFSET)) / 2;
                Font nameFont = new Font("Monospaced", Font.BOLD, 50);
                drawText(currentSkin.getPlayerName(), centerX, PLAYER_NAME_Y_OFFSET, nameFont,
                        color);

                Font accessoryFont = new Font("Monospaced", Font.BOLD, 30);
                textInsideBox(SKIN_LEFT_BOX, "<", color, accessoryFont);
                textInsideBox(SKIN_RIGHT_BOX, ">", color, accessoryFont);
            }
        }

        private class AccessorySelectionUI {

            private static final double ACCESSORY_BOX_SIZE = TILE_SIDE;
            private static final double ACCESSORY_Y_OFFSET = 7.0 * TILE_SIDE;
            private static final double ACCESSORY_LEFT_X = 4.5 * TILE_SIDE;
            private static final double ACCESSORY_RIGHT_X_OFFSET = 6.5 * TILE_SIDE;
            private static final double ACCESSORY_CHOOSE_Y_OFFSET = 8.0 * TILE_SIDE;

            private static final Box ACCESSORY_LEFT_BOX = new Box(ACCESSORY_LEFT_X, ACCESSORY_Y_OFFSET,
                    ACCESSORY_BOX_SIZE, ACCESSORY_BOX_SIZE);
            private static final Box ACCESSORY_RIGHT_BOX = new Box(Frame.X_SCALE - ACCESSORY_RIGHT_X_OFFSET,
                    ACCESSORY_Y_OFFSET, ACCESSORY_BOX_SIZE, ACCESSORY_BOX_SIZE);
            private static final Box ACCESSORY_CHOOSE_BOX = new Box(
                    (ACCESSORY_LEFT_X + (Frame.X_SCALE - ACCESSORY_RIGHT_X_OFFSET)) / 2, ACCESSORY_CHOOSE_Y_OFFSET,
                    ACCESSORY_BOX_SIZE, ACCESSORY_BOX_SIZE);

            private void processInput(MouseData mouseData) {
                double mouseX = mouseData.mouseX;
                double mouseY = mouseData.mouseY;
                boolean mousePressed = mouseData.pressed;

                if (mousePressed) {
                    if (isIn(mouseX, mouseY, ACCESSORY_LEFT_BOX)) {
                        currentAccessoryIndex--;
                    } else if (isIn(mouseX, mouseY, ACCESSORY_RIGHT_BOX)) {
                        currentAccessoryIndex++;
                    }

                    currentAccessoryIndex = (currentAccessoryIndex + accessories.length) % accessories.length;

                    if (isIn(mouseX, mouseY, ACCESSORY_CHOOSE_BOX)) {
                        accessoryChosen[currentAccessoryIndex] = !accessoryChosen[currentAccessoryIndex];
                        if (accessoryChosen[0]) {
                            Arrays.fill(accessoryChosen, false);
                        }
                    }
                }
            }

            private void draw() {
                Accessory currentAccessory = accessories[currentAccessoryIndex];
                Color color = StdDraw.BLACK;

                Font accessoryFont = new Font("Monospaced", Font.BOLD, 30);
                String accessoryName = currentAccessory != null ? currentAccessory.getAccessoryName() : "no accessory";
                drawText(accessoryName, ACCESSORY_CHOOSE_BOX.getCenterX(), ACCESSORY_LEFT_BOX.getCenterY(),
                        accessoryFont,
                        color);

                textInsideBox(ACCESSORY_LEFT_BOX, "<", color, accessoryFont);
                textInsideBox(ACCESSORY_RIGHT_BOX, ">", color, accessoryFont);

                if (accessoryChosen[currentAccessoryIndex]) {
                    textInsideBox(ACCESSORY_CHOOSE_BOX, "✅", color, accessoryFont);
                } else {
                    textInsideBox(ACCESSORY_CHOOSE_BOX, "❎", color, accessoryFont);
                }
            }
        }

        // move to level selection or shop
        private class NavigationUI {

            public static final double ARROW_BOX_SIZE = 2.0 * TILE_SIDE;
            private static final Box ARROW_BOX = new Box(Frame.X_SCALE - 1 * TILE_SIDE, 8 * TILE_SIDE, ARROW_BOX_SIZE,
                    ARROW_BOX_SIZE);

            public static final double SHOP_BOX_X = 8 * TILE_SIDE;
            public static final double SHOP_BOX_Y = 11 * TILE_SIDE;
            public static final double SHOP_BOX_SIZE = 4.0 * TILE_SIDE;
            public static final Box SHOP_BOX = new Box(SHOP_BOX_X, SHOP_BOX_Y, SHOP_BOX_SIZE, SHOP_BOX_SIZE);

            private void processInput(MouseData mouseData) {

                double mouseX = mouseData.mouseX;
                double mouseY = mouseData.mouseY;
                boolean mousePressed = mouseData.pressed;

                if (mousePressed) {

                    if (isIn(mouseX, mouseY, ARROW_BOX)) {
                        gameState.setState(STATE.GAME);
                    } else if (isIn(mouseX, mouseY, SHOP_BOX)) {
                        gameState.setState(STATE.SHOP);
                    }

                }

            }

        }

    }

}

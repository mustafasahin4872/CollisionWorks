package game;

import helpers.InputHandler;
import helpers.InputHandler.MouseData;
import helpers.InputHandler.ArrowData;
import game.GameState.STATE;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mapobjects.category.GridObject.TILE_SIDE;
import static helpers.CollisionMethods.isIn;
import static helpers.DrawMethods.textInsideBox;
import static helpers.DrawMethods.drawText;

// governs skin selection, accessory selection and moving to level selection - shop
public class SkinSelection {

    private final InputHandler inputHandler;
    private final GameState gameState;

    private final GameMap backgroundMap = new GameMap(new GameState(1, -1), helpers.MapType.SELECTION);

    private List<Player> skins;
    private List<Accessory> accessories;

    private int currentSkinIndex = 0;
    private int currentAccessoryIndex = 0;
    private boolean[] accessoryChosen;

    private static final double DRAW_BIG_MULTIPLIER = 2.5;

    private final NavigationUI navigationUI = new NavigationUI();
    private final SkinSelectionUI skinSelectionUI = new SkinSelectionUI();
    private final AccessorySelectionUI accessorySelectionUI = new AccessorySelectionUI();

    public SkinSelection(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
    }

    void processInput(MouseData mouseData, ArrowData arrowData) {
        navigationUI.processInput(mouseData);
        skinSelectionUI.processInput(mouseData, arrowData);
        accessorySelectionUI.processInput(mouseData);
    }

    void draw() {
        skinSelectionUI.draw();
        accessorySelectionUI.draw();
    }

    public void skinSelectionLoop() {

        skins = gameState.getSkins();
        accessories = gameState.getAccessories();
        if (accessoryChosen == null) accessoryChosen = new boolean[accessories.size()];
        accessoryChosen = Arrays.copyOf(accessoryChosen, accessories.size());

        for (Player skin : skins) {
            skin.setSpawnPoint(backgroundMap.getSpawnPoint());
            skin.restart();
        }

        for (Accessory accessory : accessories) {
            if (accessory != null) {
                accessory.setAlone(false);
                accessory.update();
            }
        }

        while (gameState.getState() == STATE.SELECTION) {
            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();
            ArrowData arrowData = inputHandler.getArrowData();

            processInput(mouseData, arrowData);

            StdDraw.clear();
            StdDraw.setXscale(0, Frame.X_SCALE);
            StdDraw.setYscale(Frame.Y_SCALE, 0);

            backgroundMap.draw();

            Player currentSkin = skins.get(currentSkinIndex);
            currentSkin.drawBig(DRAW_BIG_MULTIPLIER);

            for (int i = 0; i < accessoryChosen.length; i++) {
                Accessory accessory = accessories.get(i);
                boolean isNull = accessory == null;
                boolean isTriedOn = i == currentAccessoryIndex;
                boolean isSelected = accessoryChosen[i];
                if (isNull) continue;
                if (isTriedOn || isSelected) {
                    accessory.setPlayer(currentSkin);
                    accessory.drawBig(DRAW_BIG_MULTIPLIER);
                }
            }

            draw();

            StdDraw.show();
            StdDraw.pause(Frame.PAUSE);
        }

        if (gameState.getState() != GameState.STATE.QUIT) {
            Player player = skins.get(currentSkinIndex);
            java.util.ArrayList<Accessory> selectedAccessories = new java.util.ArrayList<>();

            for (int i = 0; i < accessoryChosen.length; i++) {
                if (accessoryChosen[i]) {
                    Accessory accessory = accessories.get(i);
                    accessory.setPlayer(player);
                    selectedAccessories.add(accessories.get(i));
                }
            }
            player.setAccessories(selectedAccessories.toArray(new Accessory[0]));
            gameState.setPlayer(player);
        }
    }

    private class SkinSelectionUI {

        private static final double PLAYER_NAME_Y_OFFSET = 2.15 * TILE_SIDE;
        private static final double SKIN_BOX_SIZE = TILE_SIDE;
        private static final double SKIN_LEFT_X = 5.5 * TILE_SIDE;
        private static final double SKIN_RIGHT_X_OFFSET = 7.5 * TILE_SIDE;

        private static final Box SKIN_LEFT_BOX = new Box(SKIN_LEFT_X, PLAYER_NAME_Y_OFFSET, SKIN_BOX_SIZE,
            SKIN_BOX_SIZE);
        private static final Box SKIN_RIGHT_BOX = new Box(Frame.X_SCALE - SKIN_RIGHT_X_OFFSET,
            PLAYER_NAME_Y_OFFSET, SKIN_BOX_SIZE, SKIN_BOX_SIZE);

        public void processInput(MouseData mouseData, ArrowData arrowData) {
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
            currentSkinIndex = (currentSkinIndex + skins.size()) % skins.size();
        }

        public void draw() {
            Player currentSkin = skins.get(currentSkinIndex);
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

                currentAccessoryIndex = (currentAccessoryIndex + accessories.size()) % accessories.size();

                if (isIn(mouseX, mouseY, ACCESSORY_CHOOSE_BOX)) {
                    accessoryChosen[currentAccessoryIndex] = !accessoryChosen[currentAccessoryIndex];
                    if (accessoryChosen[0]) {
                        Arrays.fill(accessoryChosen, false);
                    }
                }
            }
        }

        private void draw() {
            Accessory currentAccessory = accessories.get(currentAccessoryIndex);
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

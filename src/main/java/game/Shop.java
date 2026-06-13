package game;

import helpers.MapType;
import lib.StdDraw;
import mapobjects.category.MapObject;
import mapobjects.component.Box;
import helpers.InputHandler;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;

import java.awt.*;
import java.util.Arrays;

import static helpers.CollisionMethods.isIn;
import static helpers.DrawMethods.drawRectWithOutline;
import static helpers.DrawMethods.textInsideBox;
import static helpers.FrameBox.CENTER_X;
import static helpers.FrameBox.CENTER_Y;
import static mapobjects.category.GridObject.TILE_SIDE;
import static helpers.InputHandler.MouseData;
import static game.GameState.STATE;
import static helpers.HelperMethods.capitalize;

// The shop screen in selection phase
// Displays 4(hardcoded) different item types to buy

/*
TODO:
    1) ACCESSORIES BEING DRAWN INACCURATELY SINCE THEY ARE LINKED TO PLAYER - FIXED!
    2) BUYABLE ARRAY COMPLETE REDESIGN
    3) IMPLEMENT THE GAMESTATE.BOUGHT_... LOGIC, ADD TO SKIN SELECTION
    4) UNRELATED: ALL GAMESTATE FIELDS ARE PUBLIC, FIX!!!
    5) LONG TERM: ADD CURRENCY AND SEPARATE PLAYER RECORDS TO KEEP PROGRESS
 */


public class Shop {

    private final InputHandler inputHandler;
    private final GameState gameState;

    private final GameMap backgroundMap = new GameMap(new GameState(4, -1), MapType.SELECTION);

    private final NavigationUI navigationUI = new NavigationUI();
    private final ShopUI shopUI = new ShopUI();
    private final BuyScreen buyScreen = new BuyScreen();

    private final int[] indexes = new int[4];
    private final boolean[] selected = new boolean[4];

    private static final double DRAW_BIG_MULTIPLIER = 2.0;

    // the skins that are attainable from the shop
    private static final Player[] SKINS = {
        new Player.RegularPlayer(),
        new Player.AnimatedPlayer("Mike")
    };


    // the accessories that are attainable from the shop
    private static final Accessory[] ACCESSORIES = {
        new Accessory.Hat("fedora"),
        new Accessory.Tie("tie"),
        new Accessory.Headpiece("coquette"),
        new Accessory.Necklace("dollar"),
        new Accessory.Necklace("sorcerer"),
        new Accessory.Pin("star"),
        new Accessory.Pin("sheriff")
    };

    private static final MapObject[][] BUYABLES = {
        SKINS, ACCESSORIES, SKINS, ACCESSORIES
    };

    public Shop(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
    }

    public void shopLoop() {

        StdDraw.setXscale(0, Frame.X_SCALE);
        StdDraw.setYscale(Frame.Y_SCALE, 0);

        Box skinBox = ShopUI.SHOP_BOXES[0];
        double[] spawnPoint = skinBox.getCenterCoordinates();
        for (Player skin : SKINS) {
            skin.setSpawnPoint(spawnPoint);
            skin.restart();
        }

        Box accessoryBox = ShopUI.SHOP_BOXES[1];
        Player player = gameState.player; // in buy screen, accessories are displayed on top of the player
        double[] displayCenter = BuyScreen.BOUGHT_BOX.getCenterCoordinates();
        player.setCenterCoordinates(displayCenter[0], displayCenter[1]);
        player.setAccessories(new Accessory[0]);
        for (Accessory accessory : ACCESSORIES) {
            accessory.setPlayer(player);
            accessory.setCenterCoordinates(accessoryBox.getCenterX(), accessoryBox.getCenterY());
            accessory.setAlone(true);
        }

        while (gameState.getState() == STATE.SHOP || gameState.getState() == STATE.PAUSE) {
            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();

            processInput(mouseData);

            StdDraw.clear();

            draw();

            StdDraw.show();
            StdDraw.pause(8 * Frame.PAUSE);
        }
    }

    private void processInput(MouseData mouseData) {
        if (gameState.getState() == STATE.SHOP) {
            navigationUI.processInput(mouseData);
            shopUI.processInput(mouseData);
        } else if (gameState.getState() == STATE.PAUSE) {
            buyScreen.processInput(mouseData);
        }

    }

    private void draw() {

        backgroundMap.draw();
        for (int i = 0; i<2; i++) { //TODO: CHANGE 2 TO 4 WHEN OTHER BUYABLES ADDED
            BUYABLES[i][indexes[i]].drawBig(DRAW_BIG_MULTIPLIER);
        }

        shopUI.draw();
        if (gameState.getState() == STATE.PAUSE) buyScreen.draw();
    }


    private MapObject getSelectedItem() {
        int selectedIndex = 0;
        for (int i = 0; i<4; i++) {
            if (selected[i]) {
                selectedIndex = i;
                break;
            }
        }
        return BUYABLES[selectedIndex][indexes[selectedIndex]];
    }


    private class ShopUI {

        private static final double SHOP_BOX_SIDE = 2*TILE_SIDE;

        private static final double ARROWS_SIDE = TILE_SIDE;
        private static final double ARROWS_GAP = 0.5 * TILE_SIDE;

        private static final double BOX_GAP = 0.25 * TILE_SIDE;
        private static final double NAME_BOX_WIDTH = 2.5 * TILE_SIDE;
        private static final double NAME_BOX_HEIGHT = 0.6 * TILE_SIDE;

        private static final double LABEL_BOX_WIDTH = 3*TILE_SIDE;
        private static final double LABEL_BOX_HEIGHT = 0.6*TILE_SIDE;


        private static final Box[] SHOP_BOXES = new Box[]{
            new Box(5 * TILE_SIDE, 4 * TILE_SIDE, SHOP_BOX_SIDE, SHOP_BOX_SIDE),
            new Box(13 * TILE_SIDE, 4 * TILE_SIDE, SHOP_BOX_SIDE, SHOP_BOX_SIDE),
            new Box(5 * TILE_SIDE, 8 * TILE_SIDE, SHOP_BOX_SIDE, SHOP_BOX_SIDE),
            new Box(13 * TILE_SIDE, 8 * TILE_SIDE, SHOP_BOX_SIDE, SHOP_BOX_SIDE)
        };

        private static final Box[][] ARROW_BOXES = new Box[4][2];
        private static final Box[] NAME_BOXES = new Box[4];
        private static final Box[] LABEL_BOXES = new Box[4];

        static {
            for (int i = 0; i<4; i++) {
                Box box = SHOP_BOXES[i];

                double x = box.getCenterX();
                double xShiftArrow = SHOP_BOX_SIDE/2 + ARROWS_GAP + ARROWS_SIDE/2;
                double y = box.getCenterY();

                ARROW_BOXES[i][0] = new Box(x - xShiftArrow, y, ARROWS_SIDE, ARROWS_SIDE);
                ARROW_BOXES[i][1] = new Box(x + xShiftArrow, y, ARROWS_SIDE, ARROWS_SIDE);

                double yShiftBuy = SHOP_BOX_SIDE/2 + BOX_GAP + NAME_BOX_HEIGHT /2;

                NAME_BOXES[i] = new Box(x, y + yShiftBuy, NAME_BOX_WIDTH, NAME_BOX_HEIGHT);
                LABEL_BOXES[i] = new Box(x, y - yShiftBuy, LABEL_BOX_WIDTH, LABEL_BOX_HEIGHT);
            }
        }

        private static final String[] LABELS = {"Skins", "Accessories", "Buffs", "ADD_LATER"};


        private void processInput(MouseData mouseData) {

            boolean pressed = mouseData.pressed;
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;

            if (pressed) {

                for (int i = 0; i<4; i++) {

                    if (isIn(mouseX, mouseY, NAME_BOXES[i])) {
                        selected[i] = true;
                        gameState.setState(STATE.PAUSE);
                        break;
                    }

                    int len = BUYABLES[i].length;

                    if (isIn(mouseX, mouseY, ARROW_BOXES[i][0])) {
                        indexes[i] = ((indexes[i] - 1) + len) % len;
                        break;
                    } else if (isIn(mouseX, mouseY, ARROW_BOXES[i][1])) {
                        indexes[i] = ((indexes[i] + 1) + len) % len;
                        break;
                    }

                }

            }

        }

        private void draw() {
            Font arrowFont = new Font("Monospaced", Font.BOLD, 25);
            Font nameFont = new Font("Monospaced", Font.BOLD, 20);
            Color outlineColor = StdDraw.BLACK;
            Color textColor = StdDraw.WHITE;
            Color nameColor = new Color(34, 171, 160);
            Color buyColor = new Color(16, 78, 6);

            for (int i = 0; i<4; i++) {
                textInsideBox(ARROW_BOXES[i][0], "<", outlineColor, arrowFont);
                textInsideBox(ARROW_BOXES[i][1], ">", outlineColor, arrowFont);

                drawRectWithOutline(LABEL_BOXES[i], nameColor, outlineColor);
                textInsideBox(LABEL_BOXES[i], LABELS[i], textColor, nameFont);

                drawRectWithOutline(NAME_BOXES[i], buyColor, outlineColor);
                String name = BUYABLES[i][indexes[i]].getName().split("/")[0];
                textInsideBox(NAME_BOXES[i], capitalize(name), textColor, nameFont);
            }
        }

    }

    private class BuyScreen {

        private static final double BUTTON_HEIGHT = TILE_SIDE;
        private static final double BUTTON_WIDTH = 5 * TILE_SIDE;
        private static final double BUTTON_GAP = 0.5 * TILE_SIDE;
        private static final double SMALL_BUTTON_WIDTH = (BUTTON_WIDTH - BUTTON_GAP)/2;
        private static final double SHOP_BOX_SIDE = ShopUI.SHOP_BOX_SIDE;
        private static final double SCREEN_UP_SPACE = 2 * TILE_SIDE;
        private static final double SCREEN_WIDTH = BUTTON_WIDTH + 2 * BUTTON_GAP;
        private static final double SCREEN_HEIGHT = SHOP_BOX_SIDE + BUTTON_HEIGHT * 2 + BUTTON_GAP * 4 + SCREEN_UP_SPACE;

        private static final Box SCREEN_BOX = new Box(CENTER_X, CENTER_Y, SCREEN_WIDTH, SCREEN_HEIGHT);
        private static final Box BOUGHT_BOX;
        private static final Box QUESTION_BOX;
        private static final Box YES_BOX;
        private static final Box NO_BOX;

        // yes and no boxes location logic
        static {
            double x = CENTER_X;
            double xShift = BUTTON_GAP/2 + SMALL_BUTTON_WIDTH/2;
            double y = CENTER_Y + SCREEN_HEIGHT/2 - BUTTON_GAP - BUTTON_HEIGHT / 2;
            YES_BOX = new Box(x - xShift, y, SMALL_BUTTON_WIDTH, BUTTON_HEIGHT);
            NO_BOX = new Box(x + xShift, y, SMALL_BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        // question box location logic
        static {
            double y = CENTER_Y + SCREEN_HEIGHT / 2 - 2 * BUTTON_GAP - 1.5 * BUTTON_HEIGHT;
            QUESTION_BOX = new Box(CENTER_X, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        static {
            double y = ((CENTER_Y - SCREEN_HEIGHT/2) + (QUESTION_BOX.getCenterY() - QUESTION_BOX.getHeight()/2)) / 2;
            BOUGHT_BOX = new Box(CENTER_X, y, SHOP_BOX_SIDE, SHOP_BOX_SIDE);
        }

        private void processInput(MouseData mouseData) {
            boolean pressed = mouseData.pressed;
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;

            if (pressed) {
                if (isIn(mouseX, mouseY, YES_BOX)) {
                    MapObject selected = getSelectedItem();
                    if (selected instanceof Player s) {
                        gameState.buySkin(s);
                    } else if (selected instanceof Accessory a) {
                        gameState.buyAccessory(a);
                    }
                    gameState.setState(STATE.SHOP);
                } else if (isIn(mouseX, mouseY, NO_BOX)) {
                    Arrays.fill(selected, false);
                    gameState.setState(STATE.SHOP);
                }
            }
        }

        private void draw() {

            MapObject buyable = getSelectedItem();

            String name = buyable.getName().split("/")[0];
            String question = "Buy: " + name + "?";
            Color buttonColor = new Color(23, 148, 9);
            Font font = new Font("Monospaced", Font.BOLD, 25);

            Color textColor = StdDraw.BLACK;
            Color outlineColor = StdDraw.WHITE;
            Color buyColor = new Color(16, 78, 6);

            drawRectWithOutline(SCREEN_BOX, buyColor, textColor);
            drawRectWithOutline(NO_BOX, buttonColor, outlineColor);
            textInsideBox(NO_BOX, "NO", textColor, font);
            drawRectWithOutline(YES_BOX, buttonColor, outlineColor);
            textInsideBox(YES_BOX, "YES", textColor, font);
            drawRectWithOutline(QUESTION_BOX, buttonColor, outlineColor);
            textInsideBox(QUESTION_BOX, question, textColor, font);

            // TODO: IS THIS CODE MESSY OR ACCEPTABLE?

            // record initial position of buyable
            double x = buyable.getX();
            double y = buyable.getY();
            // move buyable to BOUGHT_BOX
            buyable.setCenterCoordinates(BOUGHT_BOX.getCenterX(), BOUGHT_BOX.getCenterY());

            if (buyable instanceof Accessory accessory) {
                // draw accessory on top of the current player
                accessory.setAlone(false);
                gameState.player.drawBig(DRAW_BIG_MULTIPLIER);
                buyable.drawBig(DRAW_BIG_MULTIPLIER);
                // isolate accessory from player
                accessory.setAlone(true);
            } else {
                buyable.drawBig(DRAW_BIG_MULTIPLIER);
            }

            // reset position
            buyable.setCenterCoordinates(x, y);

        }

    }

    // move back to skin selection
    private class NavigationUI {

        public static final double BACK_BOX_X = 9 * TILE_SIDE;
        public static final double BACK_BOX_Y = 0 * TILE_SIDE;
        public static final double BACK_BOX_SIZE = 4.0 * TILE_SIDE;
        public static final Box BACK_BOX = new Box(BACK_BOX_X, BACK_BOX_Y, BACK_BOX_SIZE, BACK_BOX_SIZE);

        private void processInput(MouseData mouseData) {

            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean mousePressed = mouseData.pressed;

            if (mousePressed) {
                if (isIn(mouseX, mouseY, BACK_BOX)) {
                    gameState.setState(STATE.SELECTION);
                }
            }

        }

    }



}

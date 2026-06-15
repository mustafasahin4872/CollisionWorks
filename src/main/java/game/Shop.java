package game;

import helpers.FrameBox;
import helpers.MapType;
import helpers.ShopEntry;
import lib.StdDraw;
import mapobjects.category.MapObject;
import mapobjects.component.Box;
import helpers.InputHandler;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Buff;
import mapobjects.mapobject.Player;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static game.Main.IMAGES_ROOT;
import static game.Main.main;
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
// Displays 3(hardcoded) different item types to buy
@SuppressWarnings("rawtypes")
public class Shop {

    private static final int N = 3;

    private final InputHandler inputHandler;
    private final GameState gameState;

    private final GameMap backgroundMap = new GameMap(new GameState(4, -1), MapType.SELECTION);

    private final NavigationUI navigationUI = new NavigationUI();
    private final ShopUI shopUI = new ShopUI();
    private final BuyScreen buyScreen = new BuyScreen();

    private final int[] indexes = new int[N];
    private final boolean[] selected = new boolean[N];

    private static final double DRAW_BIG_MULTIPLIER = 2.0;

    private final List<List<? extends ShopEntry<?>>> buyables;
    private final List<ShopEntry<Player>> skins;
    private final List<ShopEntry<Accessory>> accessories;
    private final List<ShopEntry<Buff>> buffs;

    public Shop(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
        skins = gameState.getBuyableSkins();
        accessories = gameState.getBuyableAccessories();
        buffs = gameState.getBuyableBuffs();
        buyables = List.of(skins, accessories, buffs);
    }

    public void shopLoop() {

        StdDraw.setXscale(0, Frame.X_SCALE);
        StdDraw.setYscale(Frame.Y_SCALE, 0);

        Box skinBox = ShopUI.SHOP_BOXES[0];
        double[] spawnPoint = skinBox.getCenterCoordinates();
        for (ShopEntry<Player> entry : skins) {
            Player skin = entry.getItem();
            skin.setSpawnPoint(spawnPoint);
            skin.restart();
        }

        Box accessoryBox = ShopUI.SHOP_BOXES[1];
        for (ShopEntry<Accessory> entry : accessories) {
            Accessory accessory = entry.getItem();
            accessory.setCenterCoordinates(accessoryBox.getCenterX(), accessoryBox.getCenterY());
            accessory.setAlone(true);
        }

        Box buffBox = ShopUI.SHOP_BOXES[2];
        for (ShopEntry<Buff> entry : buffs) {
            Buff buff = entry.getItem();
            buff.setCenterCoordinates(buffBox.getCenterX(), buffBox.getCenterY());
        }

        while (gameState.getState() == STATE.SHOP || gameState.getState() == STATE.PAUSE) {
            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();

            processInput(mouseData);

            StdDraw.clear();

            draw();

            StdDraw.show();
            StdDraw.pause(Frame.PAUSE);
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
        for (int i = 0; i < N; i++) {
            getShopEntry(i).getItem().drawBig(DRAW_BIG_MULTIPLIER);
        }

        shopUI.draw();
        if (gameState.getState() == STATE.PAUSE)
            buyScreen.draw();
    }

    private ShopEntry<?> getShopEntry(int i) {
        return buyables.get(i).get(indexes[i]);
    }

    private ShopEntry getSelectedItem() {
        int selectedIndex = -1;
        for (int i = 0; i < N; i++) {
            if (selected[i]) {
                selectedIndex = i;
                break;
            }
        }
        return getShopEntry(selectedIndex);
    }


    private class ShopUI {

        private static final double SHOP_BOX_SIDE = 2 * TILE_SIDE;

        private static final Box[] SHOP_BOXES = new Box[] {
            new Box(4.5 * TILE_SIDE, 6 * TILE_SIDE, SHOP_BOX_SIDE, SHOP_BOX_SIDE),
            new Box(9 * TILE_SIDE, 6 * TILE_SIDE, SHOP_BOX_SIDE, SHOP_BOX_SIDE),
            new Box(13.5 * TILE_SIDE, 6 * TILE_SIDE, SHOP_BOX_SIDE, SHOP_BOX_SIDE),
            };

        private static final double ARROWS_SIDE = 0.5 * TILE_SIDE;
        private static final double ARROWS_GAP = 0.5 * TILE_SIDE;

        private static final double BOX_GAP = 0.25 * TILE_SIDE;

        private static final double LABEL_BOX_WIDTH = 3 * TILE_SIDE;
        private static final double LABEL_BOX_HEIGHT = 0.6 * TILE_SIDE;
        private static final double NAME_BOX_WIDTH = 2.75 * TILE_SIDE;
        private static final double NAME_BOX_HEIGHT = 0.6 * TILE_SIDE;
        private static final double PRICE_BOX_WIDTH = 2.5 * TILE_SIDE;
        private static final double PRICE_BOX_HEIGHT = 0.6 * TILE_SIDE;
        private static final double BUY_BOX_WIDTH = 2.5 * TILE_SIDE;
        private static final double BUY_BOX_HEIGHT = 0.6 * TILE_SIDE;

        private static final Box[][] ARROW_BOXES = new Box[N][2];
        private static final Box[] NAME_BOXES = new Box[N];
        private static final Box[] LABEL_BOXES = new Box[N];
        private static final Box[] PRICE_BOXES = new Box[N];
        private static final Box[] BUY_BOXES = new Box[N];

        static {
            for (int i = 0; i < N; i++) {
                Box box = SHOP_BOXES[i];

                double x = box.getCenterX();
                double xShiftArrow = SHOP_BOX_SIDE / 2 + ARROWS_GAP + ARROWS_SIDE / 2;
                double y = box.getCenterY();

                ARROW_BOXES[i][0] = new Box(x - xShiftArrow, y, ARROWS_SIDE, ARROWS_SIDE);
                ARROW_BOXES[i][1] = new Box(x + xShiftArrow, y, ARROWS_SIDE, ARROWS_SIDE);

                double yShiftSmall = SHOP_BOX_SIDE / 2 + 4 * BOX_GAP + NAME_BOX_HEIGHT / 2;
                double yShiftBig = yShiftSmall + NAME_BOX_HEIGHT/2 + BOX_GAP + LABEL_BOX_HEIGHT/2;

                NAME_BOXES[i] = new Box(x, y - yShiftSmall, NAME_BOX_WIDTH, NAME_BOX_HEIGHT);
                LABEL_BOXES[i] = new Box(x, y - yShiftBig, LABEL_BOX_WIDTH, LABEL_BOX_HEIGHT);
                PRICE_BOXES[i] = new Box(x, y + yShiftSmall, PRICE_BOX_WIDTH, PRICE_BOX_HEIGHT);
                BUY_BOXES[i] = new Box(x, y + yShiftBig, BUY_BOX_WIDTH, BUY_BOX_HEIGHT);
            }
        }

        private static final double CURRENCY_Y = TILE_SIDE;
        private static final double GEMS_X = BUY_BOXES[0].getCenterX();
        private static final double COINS_X = BUY_BOXES[2].getCenterX();

        private static final Box GEM_BOX = new Box(GEMS_X, CURRENCY_Y, TILE_SIDE, TILE_SIDE);
        private static final Box COIN_BOX = new Box(COINS_X, CURRENCY_Y, TILE_SIDE, TILE_SIDE);

        private static final String[] LABELS = { "Skins", "Accessories", "Buffs"};

        private void processInput(MouseData mouseData) {

            boolean pressed = mouseData.pressed;
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;

            if (pressed) {

                for (int i = 0; i < N; i++) {

                    if (isIn(mouseX, mouseY, BUY_BOXES[i])) {

                        ShopEntry shopEntry = getShopEntry(i);
                        if (shopEntry.isSold()) return;
                        if (!gameState.canAfford(shopEntry)) return;

                        selected[i] = true;
                        gameState.setState(STATE.PAUSE);
                        break;
                    }

                    int len = buyables.get(i).size();

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
            Font bigFont = new Font("Monospaced", Font.BOLD, 25);
            Font smallFont = new Font("Monospaced", Font.BOLD, 20);
            Color outlineColor = StdDraw.BLACK;

            Color textColor = StdDraw.WHITE;
            Color labelColor = new Color(34, 171, 160);
            Color nameColor = new Color(21, 106, 132);

            Color canBuy = new Color(16, 78, 6);
            Color cantBuy = new Color(113, 6, 6);

            String gemFile = IMAGES_ROOT + "ui/gem.png";
            String coinFile = IMAGES_ROOT + "ui/coin.png";

            StdDraw.picture(GEM_BOX.getCenterX(), GEM_BOX.getCenterY(), gemFile, GEM_BOX.getWidth(), GEM_BOX.getHeight());
            StdDraw.picture(COIN_BOX.getCenterX(), COIN_BOX.getCenterY(), coinFile, COIN_BOX.getWidth(), COIN_BOX.getHeight());
            textInsideBox(GEM_BOX, gameState.getGemAmount() + "");
            textInsideBox(COIN_BOX, gameState.getCoinAmount() + "");

            for (int i = 0; i < N; i++) {
                ShopEntry shopEntry = getShopEntry(i);
                boolean sold = shopEntry.isSold();
                boolean positive = gameState.canAfford(shopEntry) || sold;
                Color buyColor = (positive) ? canBuy : cantBuy;

                textInsideBox(ARROW_BOXES[i][0], "<", outlineColor, bigFont);
                textInsideBox(ARROW_BOXES[i][1], ">", outlineColor, bigFont);

                drawRectWithOutline(LABEL_BOXES[i], labelColor, outlineColor);
                textInsideBox(LABEL_BOXES[i], LABELS[i], textColor, smallFont);

                drawRectWithOutline(NAME_BOXES[i], nameColor, outlineColor);
                String name = getShopEntry(i).getName().split("/")[0];
                textInsideBox(NAME_BOXES[i], capitalize(name), textColor, smallFont);


                drawRectWithOutline(PRICE_BOXES[i], buyColor, outlineColor);

                String price = " " + shopEntry.getCost(); // 1 space to right to offset the gem's space
                String fileName = (shopEntry.isCosmetic()) ? gemFile : coinFile;
                double side = PRICE_BOX_HEIGHT * 0.8;
                double textWidth = 0.6 * smallFont.getSize() * price.length();

                Box box = new Box(PRICE_BOXES[i].getCenterX() - side/2, PRICE_BOXES[i].getCenterY(), PRICE_BOX_WIDTH - side, PRICE_BOX_HEIGHT);
                textInsideBox(box, price, textColor, smallFont);
                StdDraw.picture(PRICE_BOXES[i].getCenterX() + textWidth/2, PRICE_BOXES[i].getCenterY(), fileName, side, side);

                drawRectWithOutline(BUY_BOXES[i], buyColor, outlineColor);
                String text = (sold) ? "SOLD" : "BUY";
                textInsideBox(BUY_BOXES[i], text, textColor, bigFont);

                if (sold) {
                    double s = SHOP_BOX_SIDE * 1.5;
                    StdDraw.picture(SHOP_BOXES[i].getCenterX(), SHOP_BOXES[i].getCenterY(), IMAGES_ROOT + "ui/sold.png", s, s);
                }

            }

        }

    }


    private class BuyScreen {

        private static final double BUTTON_HEIGHT = TILE_SIDE;
        private static final double BUTTON_WIDTH = 5 * TILE_SIDE;
        private static final double BUTTON_GAP = 0.5 * TILE_SIDE;
        private static final double SMALL_BUTTON_WIDTH = (BUTTON_WIDTH - BUTTON_GAP) / 2;
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
            double xShift = BUTTON_GAP / 2 + SMALL_BUTTON_WIDTH / 2;
            double y = CENTER_Y + SCREEN_HEIGHT / 2 - BUTTON_GAP - BUTTON_HEIGHT / 2;
            YES_BOX = new Box(x - xShift, y, SMALL_BUTTON_WIDTH, BUTTON_HEIGHT);
            NO_BOX = new Box(x + xShift, y, SMALL_BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        // question box location logic
        static {
            double y = CENTER_Y + SCREEN_HEIGHT / 2 - 2 * BUTTON_GAP - 1.5 * BUTTON_HEIGHT;
            QUESTION_BOX = new Box(CENTER_X, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        static {
            double y = ((CENTER_Y - SCREEN_HEIGHT / 2) + (QUESTION_BOX.getCenterY() - QUESTION_BOX.getHeight() / 2)) / 2;
            BOUGHT_BOX = new Box(CENTER_X, y, SHOP_BOX_SIDE, SHOP_BOX_SIDE);
        }

        private void processInput(MouseData mouseData) {
            boolean pressed = mouseData.pressed;
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;

            if (pressed) {
                if (isIn(mouseX, mouseY, YES_BOX)) {
                    gameState.buy(getSelectedItem());
                    Arrays.fill(selected, false);
                    gameState.setState(STATE.SHOP);
                } else if (isIn(mouseX, mouseY, NO_BOX)) {
                    Arrays.fill(selected, false);
                    gameState.setState(STATE.SHOP);
                }
            }
        }

        private void draw() {

            ShopEntry buyable = getSelectedItem();

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

            MapObject mapObject = buyable.getItem();

            if (mapObject instanceof Accessory accessory) {
                accessory.setAlone(false);
                Player player = gameState.getPlayer();
                accessory.setPlayer(player);
                player.drawBigAt(BOUGHT_BOX.getCenterX(), BOUGHT_BOX.getCenterY(), DRAW_BIG_MULTIPLIER);
                accessory.drawBigAt(BOUGHT_BOX.getCenterX(), BOUGHT_BOX.getCenterY(), DRAW_BIG_MULTIPLIER);
                accessory.setAlone(true);
            } else {
                mapObject.drawBigAt(BOUGHT_BOX.getCenterX(), BOUGHT_BOX.getCenterY(), DRAW_BIG_MULTIPLIER);
            }

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

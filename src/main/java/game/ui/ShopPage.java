package game.ui;

import game.core.Frame;
import game.core.GameMap;
import game.core.GameState;
import game.core.MapMaker;
import game.data.ShopEntry;
import game.io.InputHandler;
import helpers.utils.FrameBox;
import helpers.utils.Index;
import helpers.utils.TextDisplay;
import helpers.utils.UIButton;
import lib.StdDraw;
import mapobjects.traits.Equippable;
import mapobjects.traits.MapObject;
import mapobjects.components.Box;
import mapobjects.entities.Accessory;
import mapobjects.entities.Buff;
import mapobjects.entities.Gun;
import mapobjects.entities.Player;
import helpers.utils.UIButton.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static game.core.Main.IMAGES_ROOT;
import static helpers.methods.CollisionMethods.isIn;
import static helpers.methods.DrawMethods.*;
import static helpers.utils.FrameBox.*;
import static mapobjects.traits.GridObject.TILE_SIDE;
import static game.io.InputHandler.*;
import static game.core.GameState.STATE;
import static helpers.methods.HelperMethods.capitalize;


// The shop screen in selection phase
// Displays 3(hardcoded) different item types to buy
public class ShopPage {

    private static final int N = 3;
    private boolean configured = false;
    private int displayNum;
    private STATE from, current, to;

    private final InputHandler inputHandler;
    private final GameState gameState;

    private GameMap backgroundMap;

    private final UIButton[] UIButtons = new UIButton[2];
    private final BuyScreen buyScreen = new BuyScreen();
    private final DisplayUI[] uis = new DisplayUI[N];
    private final CurrencyUI currencyUI = new CurrencyUI();

    private static final double DRAW_BIG_MULTIPLIER = 2.0;

    public ShopPage(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
        final double[][] CENTERS = new double[][]{
            new double[]{4.5 * TILE_SIDE, 6 * TILE_SIDE},
            new double[]{9 * TILE_SIDE, 6 * TILE_SIDE},
            new double[]{13.5 * TILE_SIDE, 6 * TILE_SIDE}
        };
        for (int i = 0; i<N; i++) {
            uis[i] = new DisplayUI(gameState, CENTERS[i][0], CENTERS[i][1]);
        }

    }

    @SafeVarargs
    public final void configure(STATE from, STATE current, STATE to, List<ShopEntry>... displays) {

        if (displayNum > N) {
            System.out.println("CONFIG ERROR: MORE DISPLAYS THAN INTENDED");
            return;
        }

        configureState(from, current, to);
        configureUIButtons();

        displayNum = displays.length;

        updateBuyables(displays);

        configured = true;

    }

    private void configureState(STATE from, STATE current, STATE to) {

        this.from = from;
        this.current = current;
        this.to = to;

        backgroundMap = new GameMap(new GameState(4, -1), MapMaker.MapType.SELECTION);
        if (to == null) {
            backgroundMap = new GameMap(new GameState(4, -2), MapMaker.MapType.SELECTION);
        }

    }

    private void configureUIButtons() {
        final double BOX_SIZE = 4.0 * TILE_SIDE;

        final double BOX_X = 9 * TILE_SIDE;
        final double BACK_BOX_Y = 0 * TILE_SIDE;
        final double FORWARD_BOX_Y = 11 * TILE_SIDE;

        final Box BACK_BOX = new Box(BOX_X, BACK_BOX_Y, BOX_SIZE, BOX_SIZE);
        final Box FORWARD_BOX = new Box(BOX_X, FORWARD_BOX_Y, BOX_SIZE, BOX_SIZE);

        UIButtons[0] = new UIButton.StateButton(BACK_BOX, gameState, from);
        UIButtons[1] = new UIButton.StateButton(FORWARD_BOX, gameState, to);
    }

    /// IMPORTANT: UPDATE METHODS MUST BE CALLED IF NEW DISPLAYS OR ITEMS ARE ADDED!
    private void updateBuyables(List<ShopEntry>[] displays) {
        displayNum = displays.length;
        if (displayNum == 0) {
            uis[0] = null;
            uis[1] = null;
            uis[2] = null;
        } else if (displayNum == 1) {
            uis[0] = null;
            uis[1].configure(displays[0]);
            uis[2] = null;
        } else if (displayNum == 2) {
            uis[0].configure(displays[0]);
            uis[1] = null;
            uis[2].configure(displays[1]);
        } else {
            uis[0].configure(displays[0]);
            uis[1].configure(displays[1]);
            uis[2].configure(displays[2]);
        }
    }


    public void shopLoop() throws Exception {

        if (!configured) throw new Exception("CANNOT RUN SHOP PAGE WITHOUT CONFIGURATION");

        StdDraw.setXscale(0, game.core.Frame.X_SCALE);
        StdDraw.setYscale(game.core.Frame.Y_SCALE, 0);

        while (gameState.getState() == current || gameState.getState() == STATE.PAUSE) {

            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();
            ArrowData arrowData = inputHandler.getArrowData();

            processInput(mouseData, arrowData);
            StdDraw.clear();

            draw();

            StdDraw.show();
            StdDraw.pause(game.core.Frame.PAUSE);
        }

    }

    private void processInput(MouseData mouseData, ArrowData arrowData) {
        if (gameState.getState() == current) {
            for (UIButton button : UIButtons) button.processInput(mouseData, arrowData);
            for (DisplayUI ui : uis) {
                if (ui != null) ui.processInput(mouseData, arrowData);
            }
        } else if (gameState.getState() == STATE.PAUSE) {
            buyScreen.processInput(mouseData);
        }

    }

    private void draw() {

        backgroundMap.draw();

        for (DisplayUI ui : uis) {
            if (ui != null) ui.draw();
        }

        currencyUI.draw();

        if (gameState.getState() == STATE.PAUSE) {
            buyScreen.draw();
        }

    }

    private ShopEntry getToBeBoughtEntry() {
        for (DisplayUI ui : uis) {
            if (ui != null && ui.buyButton.isPressed()) return ui.getCurrentShopEntry();
        }
        return null;
    }

    private void resetBuyScreenTriggered() {
        for (DisplayUI ui : uis) {
            if (ui != null) ui.buyButton.reset();
        }
    }


    private static class DisplayUI {

        private final GameState gameState;

        private List<ShopEntry> buyables = new ArrayList<>();
        private final Index index = new Index(1);
        private final IndexButton[] INDEX_BUTTONS = new IndexButton[2];
        private final BooleanButton statsButton;
        private final BooleanButton descriptionButton;
        private final BooleanButton buyButton;

        private final List<TextDisplay> descriptions = new ArrayList<>();
        private final List<TextDisplay> stats = new ArrayList<>();

        private final Box SHOP_BOX;
        private final Box[] ARROW_BOXES;
        private final Box NAME_BOX;
        private final Box LABEL_BOX;
        private final Box PRICE_BOX;
        private final Box BUY_BOX;
        private final Box DESCRIPTION_BOX;
        private final Box STATS_BOX;

        public DisplayUI(GameState gameState, double centerX, double centerY) {
            this.gameState = gameState;

            final double SHOP_BOX_WIDTH = 3 * TILE_SIDE;
            final double SHOP_BOX_HEIGHT = 3 * TILE_SIDE;
            final double ARROWS_SIDE = 0.5 * TILE_SIDE;
            final double INFO_SIDE = 0.5 * TILE_SIDE;
            final double LABEL_BOX_WIDTH = 3 * TILE_SIDE;
            final double LABEL_BOX_HEIGHT = 0.6 * TILE_SIDE;
            final double NAME_BOX_WIDTH = 2.75 * TILE_SIDE;
            final double NAME_BOX_HEIGHT = 0.6 * TILE_SIDE;
            final double PRICE_BOX_WIDTH = 2.5 * TILE_SIDE;
            final double PRICE_BOX_HEIGHT = 0.6 * TILE_SIDE;
            final double BUY_BOX_WIDTH = 2.5 * TILE_SIDE;
            final double BUY_BOX_HEIGHT = 0.6 * TILE_SIDE;
            final double BOX_GAP = 0.25 * TILE_SIDE;

            this.SHOP_BOX = new Box(centerX, centerY, SHOP_BOX_WIDTH, SHOP_BOX_HEIGHT);
            this.ARROW_BOXES = new Box[]{
                new Box(centerX - (SHOP_BOX_WIDTH / 2 + BOX_GAP + ARROWS_SIDE/2), centerY, ARROWS_SIDE, ARROWS_SIDE),
                new Box(centerX + (SHOP_BOX_WIDTH / 2 + BOX_GAP + ARROWS_SIDE/2), centerY, ARROWS_SIDE, ARROWS_SIDE)
            };

            this.NAME_BOX = new Box(centerX, centerY - (SHOP_BOX_HEIGHT/2 + BOX_GAP + NAME_BOX_HEIGHT/2), NAME_BOX_WIDTH, NAME_BOX_HEIGHT);
            this.LABEL_BOX = new Box(centerX, centerY - (SHOP_BOX_HEIGHT/2 + BOX_GAP + NAME_BOX_HEIGHT + BOX_GAP + LABEL_BOX_HEIGHT/2), LABEL_BOX_WIDTH, LABEL_BOX_HEIGHT);
            this.PRICE_BOX = new Box(centerX, centerY + (SHOP_BOX_HEIGHT/2 + BOX_GAP + PRICE_BOX_HEIGHT / 2), PRICE_BOX_WIDTH, PRICE_BOX_HEIGHT);
            this.BUY_BOX = new Box(centerX, centerY + (SHOP_BOX_HEIGHT/2 + BOX_GAP + PRICE_BOX_HEIGHT + BOX_GAP + BUY_BOX_HEIGHT/2), BUY_BOX_WIDTH, BUY_BOX_HEIGHT);
            this.DESCRIPTION_BOX = new Box(centerX + (NAME_BOX_WIDTH/2 + BOX_GAP + INFO_SIDE/2), NAME_BOX.getCenterY(), INFO_SIDE, INFO_SIDE);
            this.STATS_BOX = new Box(centerX - (NAME_BOX_WIDTH/2 + BOX_GAP + INFO_SIDE/2), NAME_BOX.getCenterY(), INFO_SIDE, INFO_SIDE);

            INDEX_BUTTONS[0] = new IndexButton(ARROW_BOXES[0], index, IndexButton.TYPE.DECREMENT);
            INDEX_BUTTONS[1] = new IndexButton(ARROW_BOXES[1], index, IndexButton.TYPE.INCREMENT);

            statsButton = new BooleanButton(STATS_BOX);
            descriptionButton = new BooleanButton(DESCRIPTION_BOX);
            statsButton.linkButton(descriptionButton);
            descriptionButton.linkButton(statsButton);

            buyButton = new BooleanButton(BUY_BOX);
        }

        public void configure(List<ShopEntry> buyables) {

            FrameBox.updateCenter(game.core.Frame.X_SCALE/2, Frame.Y_SCALE/2);

            final int LINE_HEIGHT = 15;
            final int size = getFontSizeForHeight(LINE_HEIGHT, new Font("Arial", Font.PLAIN, 100));
            final Font infoFont = new Font("Arial", Font.PLAIN, size);

            this.buyables = buyables;
            index.setN(buyables.size());
            descriptions.clear();
            stats.clear();
            for (ShopEntry buyable : buyables) {
                Equippable item = buyable.getItem();
                item.setCenterCoordinates(SHOP_BOX.getCenterX(), SHOP_BOX.getCenterY());
                if (item instanceof Accessory a) a.setAlone(true);
                descriptions.add(new TextDisplay(SHOP_BOX, item.getDescription(), infoFont, true));
                stats.add(new TextDisplay(SHOP_BOX, item.getStats(), infoFont, true));
            }
        }

        public void processInput(MouseData mouseData, ArrowData arrowData) {

            ShopEntry shopEntry = getCurrentShopEntry();

            if (shopEntry != null && !shopEntry.isSold()) {
                if (gameState.canAfford(shopEntry)) buyButton.processInput(mouseData, arrowData);
                if (buyButton.isPressed()) gameState.setState(STATE.PAUSE);
                descriptionButton.processInput(mouseData, arrowData);
                statsButton.processInput(mouseData, arrowData);
            }

            if (!descriptionButton.isPressed() && !statsButton.isPressed()) {
                INDEX_BUTTONS[0].processInput(mouseData, arrowData);
                INDEX_BUTTONS[1].processInput(mouseData, arrowData);
            }

        }

        private ShopEntry getCurrentShopEntry() {
            return buyables.get(index.getCurrent());
        }

        private String getLabel() {
            Equippable mapObject = buyables.get(index.getCurrent()).getItem();
            return switch (mapObject) {
                case Player p -> "Skins";
                case Accessory.Headwear _ -> "Headwears";
                case Accessory.Neckwear _ -> "Neckwears";
                case Accessory.Brooch _ -> "Brooches";
                case Buff _ -> "Buffs";
                case Gun _ -> "Guns";
                default -> {
                    System.out.println("INVALID TYPE - LABEL NAME INVALID");
                    yield "INVALID";
                }

            };

        }

        private void draw() {
            Font bigFont = new Font("Monospaced", Font.BOLD, 25);
            Font smallFont = new Font("Monospaced", Font.BOLD, 20);
            Color outlineColor = StdDraw.BLACK;

            Color shopColor = new Color(0, 88, 188);
            Color descriptionColor = new Color(230, 114, 230);
            Color statsColor = new Color(244, 157, 8);

            Color textColor = StdDraw.WHITE;
            Color labelColor = new Color(34, 171, 160);
            Color nameColor = new Color(21, 106, 132);

            Color canBuy = new Color(16, 78, 6);
            Color cantBuy = new Color(113, 6, 6);

            ShopEntry shopEntry = getCurrentShopEntry();
            if (shopEntry == null) return;
            boolean sold = shopEntry.isSold();
            boolean positive = gameState.canAfford(shopEntry) || sold;
            Color buyColor = (positive) ? canBuy : cantBuy;

            textInsideBox(ARROW_BOXES[0], "<", outlineColor, bigFont);
            textInsideBox(ARROW_BOXES[1], ">", outlineColor, bigFont);

            drawRectWithOutline(LABEL_BOX, labelColor, outlineColor);
            textInsideBox(LABEL_BOX, getLabel(), textColor, smallFont);

            drawRectWithOutline(NAME_BOX, nameColor, outlineColor);
            String name = shopEntry.getName().split("/")[0];
            textInsideBox(NAME_BOX, capitalize(name), textColor, smallFont);

            if (descriptionButton.isPressed()) {
                drawRectWithOutline(SHOP_BOX, descriptionColor, outlineColor);
                drawRectWithOutline(DESCRIPTION_BOX, shopColor, outlineColor);
                drawRectWithOutline(STATS_BOX, statsColor, outlineColor);
                textInsideBox(DESCRIPTION_BOX, "↩", StdDraw.BLACK, smallFont);
                textInsideBox(STATS_BOX, "∑", StdDraw.BLACK, smallFont);
                descriptions.get(index.getCurrent()).draw();
            } else if (statsButton.isPressed()){
                drawRectWithOutline(SHOP_BOX, statsColor, outlineColor);
                drawRectWithOutline(DESCRIPTION_BOX, descriptionColor, outlineColor);
                drawRectWithOutline(STATS_BOX, shopColor, outlineColor);
                textInsideBox(DESCRIPTION_BOX, "ⓘ", StdDraw.BLACK, smallFont);
                textInsideBox(STATS_BOX, "↩", StdDraw.BLACK, smallFont);
                stats.get(index.getCurrent()).draw();
            } else {
                drawRectWithOutline(SHOP_BOX, shopColor, outlineColor);
                drawRectWithOutline(DESCRIPTION_BOX, descriptionColor, outlineColor);
                drawRectWithOutline(STATS_BOX, statsColor, outlineColor);
                textInsideBox(DESCRIPTION_BOX, "ⓘ", StdDraw.BLACK, smallFont);
                textInsideBox(STATS_BOX, "∑", StdDraw.BLACK, smallFont);
                shopEntry.getItem().drawBig(DRAW_BIG_MULTIPLIER);
            }

            drawRectWithOutline(PRICE_BOX, buyColor, outlineColor);

            String coinFile = IMAGES_ROOT + "ui/coin.png";
            String gemFile = IMAGES_ROOT + "ui/gem.png";
            String price = " " + shopEntry.getCoinCost(); // 1 space to right to offset the gem's space
            String fileName = coinFile; // TODO: DISPLAY BOTH CURRENCIES
            double side = PRICE_BOX.getHeight() * 0.8;
            double textWidth = 0.6 * smallFont.getSize() * price.length();

            Box box = new Box(PRICE_BOX.getCenterX() - side/2, PRICE_BOX.getCenterY(), PRICE_BOX.getWidth() - side, PRICE_BOX.getHeight());
            textInsideBox(box, price, textColor, smallFont);
            StdDraw.picture(PRICE_BOX.getCenterX() + textWidth/2, PRICE_BOX.getCenterY(), fileName, side, side);

            drawRectWithOutline(BUY_BOX, buyColor, outlineColor);
            String text = (sold) ? "SOLD" : "BUY";
            textInsideBox(BUY_BOX, text, textColor, bigFont);

            if (sold) {
                double s = SHOP_BOX.getWidth() * 1.5;
                StdDraw.picture(SHOP_BOX.getCenterX(), SHOP_BOX.getCenterY(), IMAGES_ROOT + "ui/sold.png", s, s);
            }

        }

    }

    private class CurrencyUI {
        private static final double GEMS_X = 4.5 * TILE_SIDE;
        private static final double COINS_X = 13.5 * TILE_SIDE;
        private static final double CURRENCY_Y = TILE_SIDE;

        private static final Box GEM_BOX = new Box(GEMS_X, CURRENCY_Y, TILE_SIDE, TILE_SIDE);
        private static final Box COIN_BOX = new Box(COINS_X, CURRENCY_Y, TILE_SIDE, TILE_SIDE);

        private void draw() {
            String gemFile = IMAGES_ROOT + "ui/gem.png";
            String coinFile = IMAGES_ROOT + "ui/coin.png";

            StdDraw.picture(GEM_BOX.getCenterX(), GEM_BOX.getCenterY(), gemFile, GEM_BOX.getWidth(), GEM_BOX.getHeight());
            StdDraw.picture(COIN_BOX.getCenterX(), COIN_BOX.getCenterY(), coinFile, COIN_BOX.getWidth(), COIN_BOX.getHeight());
            textInsideBox(GEM_BOX, gameState.getGemAmount() + "");
            textInsideBox(COIN_BOX, gameState.getCoinAmount() + "");
        }

    }

    private class BuyScreen {

        private static final double BUTTON_HEIGHT = TILE_SIDE;
        private static final double BUTTON_WIDTH = 5 * TILE_SIDE;
        private static final double BUTTON_GAP = 0.5 * TILE_SIDE;
        private static final double SMALL_BUTTON_WIDTH = (BUTTON_WIDTH - BUTTON_GAP) / 2;
        private static final double SHOP_BOX_SIDE = 3 * TILE_SIDE;
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
            boolean clicked = mouseData.clicked;
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;

            if (clicked) {
                if (isIn(mouseX, mouseY, YES_BOX)) {
                    gameState.buy(getToBeBoughtEntry());
                    gameState.setState(current);
                    resetBuyScreenTriggered();
                } else if (isIn(mouseX, mouseY, NO_BOX)) {
                    gameState.setState(current);
                    resetBuyScreenTriggered();
                }
            }
        }

        private void draw() {

            ShopEntry buyable = getToBeBoughtEntry();
            if (buyable == null) return;

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

            Equippable item = buyable.getItem();

            if (item instanceof Accessory accessory) {
                accessory.setAlone(false);
                Player player = gameState.getPlayer();
                accessory.setPlayer(player);
                player.drawBigAt(BOUGHT_BOX.getCenterX(), BOUGHT_BOX.getCenterY(), DRAW_BIG_MULTIPLIER);
                accessory.drawBigAt(BOUGHT_BOX.getCenterX(), BOUGHT_BOX.getCenterY(), DRAW_BIG_MULTIPLIER);
                accessory.setAlone(true);
            } else {
                item.drawBigAt(BOUGHT_BOX.getCenterX(), BOUGHT_BOX.getCenterY(), DRAW_BIG_MULTIPLIER);
            }

        }

    }

}


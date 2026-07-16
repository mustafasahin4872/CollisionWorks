package game.ui;

import game.io.Frame;
import game.core.GameMap;
import game.core.GameState;
import game.core.MapMaker;
import game.ui.components.ShopEntry;
import game.io.InputHandler;
import game.ui.components.FrameBox;
import game.ui.components.Index;
import game.ui.components.TextDisplay;
import game.ui.components.UIButton;
import mapobjects.traits.schemas.Equippable;
import mapobjects.components.Box;
import game.io.Drawer.OutlinedBoxDrawer;
import game.io.Drawer.ClassicButtonDrawer;
import game.io.Drawer.TextDrawer;
import game.io.Drawer.THICKNESS;
import mapobjects.entities.Accessory;
import mapobjects.entities.Buff;
import mapobjects.entities.Gun;
import mapobjects.entities.Player;
import game.ui.components.UIButton.*;
import game.io.Drawer.PictureDrawer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static helpers.CollisionMethods.isIn;
import static helpers.TextMethods.*;
import static game.ui.components.FrameBox.*;
import static mapobjects.traits.schemas.GridObject.TILE_SIDE;
import static game.io.InputHandler.*;
import static game.core.GameState.STATE;
import static helpers.TextMethods.capitalize;

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
        final double[][] CENTERS = new double[][] {
                new double[] { 4.5 * TILE_SIDE, 6 * TILE_SIDE },
                new double[] { 9 * TILE_SIDE, 6 * TILE_SIDE },
                new double[] { 13.5 * TILE_SIDE, 6 * TILE_SIDE }
        };
        for (int i = 0; i < N; i++) {
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

        if (!configured)
            throw new Exception("CANNOT RUN SHOP PAGE WITHOUT CONFIGURATION");

        Frame.setDefaultScale();

        while (gameState.getState() == current || gameState.getState() == STATE.PAUSE) {

            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();
            ArrowData arrowData = inputHandler.getArrowData();

            processInput(mouseData, arrowData);
            Frame.clear();

            backgroundMap.draw();
            draw();

            Frame.show();
            Frame.pause(Frame.PAUSE);
        }

    }

    private void processInput(MouseData mouseData, ArrowData arrowData) {
        if (gameState.getState() == current) {
            for (UIButton button : UIButtons)
                button.processInput(mouseData, arrowData);
            for (DisplayUI ui : uis) {
                if (ui != null)
                    ui.processInput(mouseData, arrowData);
            }
        } else if (gameState.getState() == STATE.PAUSE) {
            buyScreen.processInput(mouseData);
        }

    }

    private void draw() {

        backgroundMap.draw();

        for (DisplayUI ui : uis) {
            if (ui != null)
                ui.draw();
        }

        currencyUI.draw();

        if (gameState.getState() == STATE.PAUSE) {
            buyScreen.draw();
        }

    }

    private ShopEntry getToBeBoughtEntry() {
        for (DisplayUI ui : uis) {
            if (ui != null && ui.buyButton.isPressed())
                return ui.getCurrentShopEntry();
        }
        return null;
    }

    private void resetBuyScreenTriggered() {
        for (DisplayUI ui : uis) {
            if (ui != null)
                ui.buyButton.reset();
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

        private static final Color SHOP_COLOR = new Color(0, 88, 188);
        private static final Color DESCRIPTION_COLOR = new Color(230, 114, 230);
        private static final Color STATS_COLOR = new Color(244, 157, 8);
        private static final Color TEXT_COLOR = Color.WHITE;
        private static final Color LABEL_COLOR = new Color(34, 171, 160);
        private static final Color NAME_COLOR = new Color(21, 106, 132);
        private static final Color OUTLINE_COLOR = Color.BLACK;
        private static final Color CAN_BUY_COLOR = new Color(16, 78, 6);
        private static final Color CANT_BUY_COLOR = new Color(113, 6, 6);

        private static final Font BIG_FONT = new Font("Monospaced", Font.BOLD, 25);
        private static final Font SMALL_FONT = new Font("Monospaced", Font.BOLD, 20);

        private static final String DESCRIPTION_SYMBOL = "ⓘ";
        private static final String STATS_SYMBOL = "∑";
        private static final String RETURN_SYMBOL = "↩";

        private final TextDrawer leftArrowDrawer;
        private final TextDrawer rightArrowDrawer;
        private final ClassicButtonDrawer labelDrawer;
        private final ClassicButtonDrawer nameDrawer;
        private final OutlinedBoxDrawer shopBoxDrawer;
        private final ClassicButtonDrawer descriptionDrawer;
        private final ClassicButtonDrawer statsDrawer;
        private final OutlinedBoxDrawer priceDrawer;
        private final ClassicButtonDrawer buyDrawer;
        private final PictureDrawer soldIconDrawer;

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
            this.ARROW_BOXES = new Box[] {
                    new Box(centerX - (SHOP_BOX_WIDTH / 2 + BOX_GAP + ARROWS_SIDE / 2), centerY, ARROWS_SIDE,
                            ARROWS_SIDE),
                    new Box(centerX + (SHOP_BOX_WIDTH / 2 + BOX_GAP + ARROWS_SIDE / 2), centerY, ARROWS_SIDE,
                            ARROWS_SIDE)
            };

            this.NAME_BOX = new Box(centerX, centerY - (SHOP_BOX_HEIGHT / 2 + BOX_GAP + NAME_BOX_HEIGHT / 2),
                    NAME_BOX_WIDTH, NAME_BOX_HEIGHT);
            this.LABEL_BOX = new Box(centerX,
                    centerY - (SHOP_BOX_HEIGHT / 2 + BOX_GAP + NAME_BOX_HEIGHT + BOX_GAP + LABEL_BOX_HEIGHT / 2),
                    LABEL_BOX_WIDTH, LABEL_BOX_HEIGHT);
            this.PRICE_BOX = new Box(centerX, centerY + (SHOP_BOX_HEIGHT / 2 + BOX_GAP + PRICE_BOX_HEIGHT / 2),
                    PRICE_BOX_WIDTH, PRICE_BOX_HEIGHT);
            this.BUY_BOX = new Box(centerX,
                    centerY + (SHOP_BOX_HEIGHT / 2 + BOX_GAP + PRICE_BOX_HEIGHT + BOX_GAP + BUY_BOX_HEIGHT / 2),
                    BUY_BOX_WIDTH, BUY_BOX_HEIGHT);
            this.DESCRIPTION_BOX = new Box(centerX + (NAME_BOX_WIDTH / 2 + BOX_GAP + INFO_SIDE / 2),
                    NAME_BOX.getCenterY(), INFO_SIDE, INFO_SIDE);
            this.STATS_BOX = new Box(centerX - (NAME_BOX_WIDTH / 2 + BOX_GAP + INFO_SIDE / 2), NAME_BOX.getCenterY(),
                    INFO_SIDE, INFO_SIDE);

            INDEX_BUTTONS[0] = new IndexButton(ARROW_BOXES[0], index, IndexButton.TYPE.DECREMENT);
            INDEX_BUTTONS[1] = new IndexButton(ARROW_BOXES[1], index, IndexButton.TYPE.INCREMENT);

            statsButton = new BooleanButton(STATS_BOX);
            descriptionButton = new BooleanButton(DESCRIPTION_BOX);
            statsButton.linkButton(descriptionButton);
            descriptionButton.linkButton(statsButton);

            buyButton = new BooleanButton(BUY_BOX);

            leftArrowDrawer = new TextDrawer(ARROW_BOXES[0], "<", OUTLINE_COLOR, BIG_FONT);
            rightArrowDrawer = new TextDrawer(ARROW_BOXES[1], ">", OUTLINE_COLOR, BIG_FONT);

            labelDrawer = new ClassicButtonDrawer(LABEL_BOX, LABEL_COLOR, Color.BLACK, THICKNESS.THIN, TEXT_COLOR,
                    SMALL_FONT);
            nameDrawer = new ClassicButtonDrawer(NAME_BOX, NAME_COLOR, Color.BLACK, THICKNESS.THIN, TEXT_COLOR,
                    SMALL_FONT);

            shopBoxDrawer = new OutlinedBoxDrawer(SHOP_BOX, SHOP_COLOR, OUTLINE_COLOR, THICKNESS.THIN);
            descriptionDrawer = new ClassicButtonDrawer(DESCRIPTION_BOX, DESCRIPTION_COLOR, Color.BLACK, THICKNESS.THIN,
                    DESCRIPTION_SYMBOL,
                    Color.BLACK, SMALL_FONT);
            statsDrawer = new ClassicButtonDrawer(STATS_BOX, STATS_COLOR, Color.BLACK, THICKNESS.THIN, STATS_SYMBOL,
                    Color.BLACK, SMALL_FONT);

            priceDrawer = new OutlinedBoxDrawer(PRICE_BOX, OUTLINE_COLOR, OUTLINE_COLOR, THICKNESS.THIN); // Color will
                                                                                                          // be set
            // dynamically
            buyDrawer = new ClassicButtonDrawer(BUY_BOX, OUTLINE_COLOR, Color.BLACK, THICKNESS.THIN, "BUY", TEXT_COLOR,
                    BIG_FONT); // Color and text
            // set dynamically

            soldIconDrawer = new PictureDrawer(new Box(SHOP_BOX.getCenterX(), SHOP_BOX.getCenterY(), SHOP_BOX.getWidth() * 1.5, SHOP_BOX.getWidth() * 1.5), "ui/", "sold");
        }

        public void configure(List<ShopEntry> buyables) {

            FrameBox.updateCenter(Frame.X_SCALE / 2, Frame.Y_SCALE / 2);

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
                if (item instanceof Accessory a)
                    a.setAlone(true);
                descriptions.add(new TextDisplay(SHOP_BOX, item.getDescription(), infoFont, true));
                stats.add(new TextDisplay(SHOP_BOX, item.getStats(), infoFont, true));
            }
        }

        public void processInput(MouseData mouseData, ArrowData arrowData) {

            ShopEntry shopEntry = getCurrentShopEntry();

            if (shopEntry != null && !shopEntry.isSold()) {
                if (gameState.canAfford(shopEntry))
                    buyButton.processInput(mouseData, arrowData);
                if (buyButton.isPressed())
                    gameState.setState(STATE.PAUSE);
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

            ShopEntry shopEntry = getCurrentShopEntry();
            if (shopEntry == null)
                return;
            boolean sold = shopEntry.isSold();
            boolean positive = gameState.canAfford(shopEntry) || sold;
            Color buyColor = (positive) ? CAN_BUY_COLOR : CANT_BUY_COLOR;

            leftArrowDrawer.draw();
            rightArrowDrawer.draw();

            labelDrawer.setText(getLabel());
            labelDrawer.draw();

            String name = shopEntry.getName().split("/")[0];
            nameDrawer.setText(capitalize(name));
            nameDrawer.draw();

            if (descriptionButton.isPressed()) {
                shopBoxDrawer.setBoxColor(DESCRIPTION_COLOR);
                shopBoxDrawer.draw();
                descriptionDrawer.setBoxColor(SHOP_COLOR);
                descriptionDrawer.setText(RETURN_SYMBOL);
                descriptionDrawer.draw();
                statsDrawer.setBoxColor(STATS_COLOR);
                statsDrawer.setText(STATS_SYMBOL);
                statsDrawer.draw();
                descriptions.get(index.getCurrent()).draw();
            } else if (statsButton.isPressed()) {
                shopBoxDrawer.setBoxColor(STATS_COLOR);
                shopBoxDrawer.draw();
                descriptionDrawer.setBoxColor(DESCRIPTION_COLOR);
                descriptionDrawer.setText(DESCRIPTION_SYMBOL);
                descriptionDrawer.draw();
                statsDrawer.setBoxColor(SHOP_COLOR);
                statsDrawer.setText(RETURN_SYMBOL);
                statsDrawer.draw();
                stats.get(index.getCurrent()).draw();
            } else {
                shopBoxDrawer.setBoxColor(SHOP_COLOR);
                shopBoxDrawer.draw();
                descriptionDrawer.setBoxColor(DESCRIPTION_COLOR);
                descriptionDrawer.setText(DESCRIPTION_SYMBOL);
                descriptionDrawer.draw();
                statsDrawer.setBoxColor(STATS_COLOR);
                statsDrawer.setText(STATS_SYMBOL);
                statsDrawer.draw();
                Equippable item = shopEntry.getItem();
                item.drawBigAt(item.getX(), item.getY(), DRAW_BIG_MULTIPLIER);
            }

            priceDrawer.setBoxColor(buyColor);
            priceDrawer.draw();

            // TODO: PRICE DISPLAY

            buyDrawer.setBoxColor(buyColor);
            String text = (sold) ? "SOLD" : "BUY";
            buyDrawer.setText(text);
            buyDrawer.draw();

            if (sold) {
                soldIconDrawer.draw();
            }

        }

    }

    private class CurrencyUI {
        private static final double GEMS_X = 4.5 * TILE_SIDE;
        private static final double COINS_X = 13.5 * TILE_SIDE;
        private static final double CURRENCY_Y = TILE_SIDE;

        private static final Box GEM_BOX = new Box(GEMS_X, CURRENCY_Y, TILE_SIDE, TILE_SIDE);
        private static final Box COIN_BOX = new Box(COINS_X, CURRENCY_Y, TILE_SIDE, TILE_SIDE);

        private final TextDrawer coinTextDrawer = new TextDrawer(COIN_BOX);
        private final TextDrawer gemTextDrawer = new TextDrawer(GEM_BOX);

        private final PictureDrawer coinIconDrawer = new PictureDrawer(COIN_BOX, "ui/", "coin");
        private final PictureDrawer gemIconDrawer = new PictureDrawer(GEM_BOX, "ui/", "gem");

        private void draw() {
            gemIconDrawer.draw();
            coinIconDrawer.draw();
            coinTextDrawer.setText(gameState.getCoinAmount() + "");
            gemTextDrawer.setText(gameState.getGemAmount() + "");
            coinTextDrawer.draw();
            gemTextDrawer.draw();
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
        private static final double SCREEN_HEIGHT = SHOP_BOX_SIDE + BUTTON_HEIGHT * 2 + BUTTON_GAP * 4
                + SCREEN_UP_SPACE;

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
            double y = ((CENTER_Y - SCREEN_HEIGHT / 2) + (QUESTION_BOX.getCenterY() - QUESTION_BOX.getHeight() / 2))
                    / 2;
            BOUGHT_BOX = new Box(CENTER_X, y, SHOP_BOX_SIDE, SHOP_BOX_SIDE);
        }

        private static final Color BUTTON_COLOR = new Color(23, 148, 9);
        private static final Color TEXT_COLOR = Color.BLACK;
        private static final Color OUTLINE_COLOR = Color.WHITE;
        private static final Color BUY_COLOR = new Color(16, 78, 6);

        private static final Font FONT = new Font("Monospaced", Font.BOLD, 25);

        private final OutlinedBoxDrawer screenDrawer;
        private final ClassicButtonDrawer noDrawer;
        private final ClassicButtonDrawer yesDrawer;
        private final ClassicButtonDrawer questionDrawer;

        public BuyScreen() {
            screenDrawer = new OutlinedBoxDrawer(SCREEN_BOX, BUY_COLOR, TEXT_COLOR, THICKNESS.THIN);
            noDrawer = new ClassicButtonDrawer(NO_BOX, BUTTON_COLOR, OUTLINE_COLOR, THICKNESS.THIN, "NO", TEXT_COLOR,
                    FONT);
            yesDrawer = new ClassicButtonDrawer(YES_BOX, BUTTON_COLOR, OUTLINE_COLOR, THICKNESS.THIN, "YES", TEXT_COLOR,
                    FONT);
            questionDrawer = new ClassicButtonDrawer(QUESTION_BOX, BUTTON_COLOR, OUTLINE_COLOR, THICKNESS.THIN,
                    TEXT_COLOR, FONT);
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
            if (buyable == null)
                return;

            String name = buyable.getName().split("/")[0];
            String question = "Buy: " + name + "?";

            screenDrawer.draw();
            noDrawer.draw();
            yesDrawer.draw();

            questionDrawer.setText(question);
            questionDrawer.draw();

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

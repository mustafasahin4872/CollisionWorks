package game.ui;

import game.io.Frame;
import game.core.GameMap;
import game.core.GameState;
import game.core.MapMaker;
import game.ui.components.FrameBox;
import game.io.InputHandler;
import game.io.InputHandler.MouseData;
import game.io.InputHandler.ArrowData;
import game.core.GameState.STATE;
import game.ui.components.Index;
import game.ui.components.TextDisplay;
import game.ui.components.UIButton;
import game.ui.components.UIButton.StateButton;
import mapobjects.components.Box;
import game.io.Drawer.THICKNESS;
import game.io.Drawer.TextDrawer;
import game.io.Drawer.OutlinedBoxDrawer;
import game.io.Drawer.ClassicButtonDrawer;
import game.io.Drawer.OutlineDrawer;
import mapobjects.entities.Gun;
import mapobjects.entities.Player;
import game.ui.components.UIButton.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static helpers.TextMethods.*;
import static mapobjects.traits.GridObject.TILE_SIDE;

// governs skin selection, accessory selection, gun selection and moving to level selection - shop
public class MainSelection {

    private final InputHandler inputHandler;
    private final GameState gameState;

    private final GameMap backgroundMap = new GameMap(new GameState(1, -1), MapMaker.MapType.SELECTION);

    private static final double DRAW_BIG_MULTIPLIER = 2.5;

    private final SkinSelectionUI skinSelectionUI = new SkinSelectionUI();
    private final GunSelectionUI gunSelectionUI = new GunSelectionUI();

    private final Set<UIButton> UIButtons = new HashSet<>();

    public MainSelection(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
        configureNavigationButtons();
    }

    private void configureNavigationButtons() {

        final double SIDE = 2.0 * TILE_SIDE;
        final Box ARROW_BOX = new Box(Frame.X_SCALE - 1 * TILE_SIDE, 8 * TILE_SIDE, SIDE, SIDE);
        final Box SHOP_BOX = new Box(8 * TILE_SIDE, 11 * TILE_SIDE, SIDE, SIDE);
        final Box ACCESSORY_BOX = new Box(TILE_SIDE, 6 * TILE_SIDE, SIDE, SIDE);

        UIButtons.add(new StateButton(ARROW_BOX, gameState, STATE.GAME));
        UIButtons.add(new StateButton(SHOP_BOX, gameState, STATE.SHOP));
        UIButtons.add(new StateButton(ACCESSORY_BOX, gameState, STATE.ACCESSORY));

    }

    public void processInput(MouseData mouseData, ArrowData arrowData) {
        for (UIButton button : UIButtons)
            button.processInput(mouseData, arrowData);
        skinSelectionUI.processInput(mouseData, arrowData);
        gunSelectionUI.processInput(mouseData, arrowData);
    }

    void draw() {
        skinSelectionUI.draw();
        gunSelectionUI.draw();
    }

    public void mainSelectionLoop() {

        skinSelectionUI.configure();
        gunSelectionUI.configure();

        Frame.setDefaultScale();
        while (gameState.getState() == STATE.SELECTION) {
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

        if (gameState.getState() != GameState.STATE.QUIT) {
            Player player = skinSelectionUI.getCurrentSkin();
            player.setAccessories(gameState.getEquipped());
            gameState.setPlayer(player);
            Gun gun = gunSelectionUI.getCurrentGun();
            player.setGun(gun);
        }
    }

    private class SkinSelectionUI {

        private static final double CENTER_X = 8.0 * TILE_SIDE;
        private static final double CENTER_Y = 4.0 * TILE_SIDE;
        private static final double SIDE = 2.5 * TILE_SIDE;
        private static final double GAP = 0.5 * TILE_SIDE;
        private static final double NAME_WIDTH = 3 * TILE_SIDE;
        private static final double BUTTON_SIDE = TILE_SIDE;

        private static final Box BOX = new Box(CENTER_X, CENTER_Y, SIDE, SIDE);
        private static final Box NAME_BOX = new Box(CENTER_X, CENTER_Y - SIDE / 2 - GAP - BUTTON_SIDE / 2, NAME_WIDTH,
                BUTTON_SIDE);
        private static final Box LEFT_BOX = new Box(CENTER_X - GAP / 2 - BUTTON_SIDE / 2,
                CENTER_Y + SIDE / 2 + GAP + BUTTON_SIDE / 2, BUTTON_SIDE, BUTTON_SIDE);
        private static final Box RIGHT_BOX = new Box(CENTER_X + GAP / 2 + BUTTON_SIDE / 2,
                CENTER_Y + SIDE / 2 + GAP + BUTTON_SIDE / 2, BUTTON_SIDE, BUTTON_SIDE);

        private List<Player> skins;
        private final Index skinIndex = new Index(1);
        private final Set<UIButton> UIButtons = new HashSet<>();
        OutlineDrawer leftOutline = new OutlineDrawer(LEFT_BOX, Color.BLACK, THICKNESS.THIN);
        OutlineDrawer rightOutline = new OutlineDrawer(RIGHT_BOX, Color.BLACK, THICKNESS.THIN);
        TextDrawer nameDrawer = new TextDrawer(NAME_BOX, Color.BLACK, new Font("Monospaced", Font.BOLD, 50));
        TextDrawer leftDrawer = new TextDrawer(LEFT_BOX, "<", Color.BLACK, new Font("Monospaced", Font.BOLD, 30));
        TextDrawer rightDrawer = new TextDrawer(RIGHT_BOX, ">", Color.BLACK, new Font("Monospaced", Font.BOLD, 30));

        public SkinSelectionUI() {
            configureNavigationButtons();
        }

        private void configureNavigationButtons() {

            UIButtons.add(new IndexButton(LEFT_BOX, skinIndex, IndexButton.TYPE.DECREMENT));
            UIButtons.add(new IndexButton(RIGHT_BOX, skinIndex, IndexButton.TYPE.INCREMENT));
            UIButtons.add(new GenericIndexKey(GenericIndexKey.GenericKey.LEFT_ARROW, skinIndex, -1));
            UIButtons.add(new GenericIndexKey(GenericIndexKey.GenericKey.RIGHT_ARROW, skinIndex, 1));

        }

        public void configure() {
            skins = gameState.getSkins();
            skinIndex.setN(skins.size());
        }

        private void processInput(MouseData mouseData, ArrowData arrowData) {
            for (UIButton button : UIButtons)
                button.processInput(mouseData, arrowData);
        }

        public Player getCurrentSkin() {
            return skins.get(skinIndex.getCurrent());
        }

        public void draw() {

            Player currentSkin = getCurrentSkin();

            nameDrawer.setText(currentSkin.getPlayerName());
            nameDrawer.draw();

            leftOutline.draw();
            rightOutline.draw();
            leftDrawer.draw();
            rightDrawer.draw();

            currentSkin.drawBigAt(BOX.getCenterX(), BOX.getCenterY(), DRAW_BIG_MULTIPLIER);

        }

    }

    private class GunSelectionUI {

        private static final double CENTER_X = 14.0 * TILE_SIDE;
        private static final double CENTER_Y = 2.0 * TILE_SIDE;
        private static final double SIDE = 2.5 * TILE_SIDE;
        private static final double GAP = 0.5 * TILE_SIDE;
        private static final double BUTTON_SIDE = 0.5 * TILE_SIDE;
        private static final double STATS_SIDE = 0.5 * TILE_SIDE;
        private static final double STATS_GAP = 0.25 * TILE_SIDE;
        private static final Box BOX = new Box(CENTER_X, CENTER_Y, SIDE, SIDE);
        private static final Box LEFT_BOX = new Box(CENTER_X - BUTTON_SIDE - GAP,
                CENTER_Y + SIDE / 2 + GAP + BUTTON_SIDE / 2, BUTTON_SIDE, BUTTON_SIDE);
        private static final Box SELECT_BOX = new Box(CENTER_X, CENTER_Y + SIDE / 2 + GAP + BUTTON_SIDE / 2,
                BUTTON_SIDE, BUTTON_SIDE);
        private static final Box RIGHT_BOX = new Box(CENTER_X + BUTTON_SIDE + GAP,
                CENTER_Y + SIDE / 2 + GAP + BUTTON_SIDE / 2, BUTTON_SIDE, BUTTON_SIDE);
        private static final Box STATS_BOX = new Box(CENTER_X - SIDE / 2 - STATS_GAP - STATS_SIDE / 2,
                CENTER_Y - SIDE / 2 + STATS_SIDE / 2, STATS_SIDE, STATS_SIDE);

        private final BooleanButton statsButton;
        private final Set<IndexButton> indexButtons = new HashSet<>();
        private final IndexButton selectButton;
        private final List<TextDisplay> displays = new ArrayList<>();

        private static final Color BOX_COLOR = new Color(0, 88, 188);
        private static final Color STATS_COLOR = new Color(244, 157, 8);
        private static final String STATS_SYMBOL = "∑";
        private static final String BACK_SYMBOL = "↩";
        private static final String SELECTED_SYMBOL = "✅";
        private static final String UNSELECTED_SYMBOL = "❎";

        private final OutlinedBoxDrawer boxDrawer;
        private final ClassicButtonDrawer leftBoxDrawer;
        private final ClassicButtonDrawer rightBoxDrawer;
        private final ClassicButtonDrawer selectBoxDrawer;
        private final ClassicButtonDrawer statsButtonDrawer;

        private List<Gun> guns;
        private final Index gunIndex = new Index(1);

        public GunSelectionUI() {
            configureUIButtons();
            statsButton = new BooleanButton(STATS_BOX);
            selectButton = new IndexButton(SELECT_BOX, gunIndex, IndexButton.TYPE.SELECT);

            Font buttonFont = new Font("Monospaced", Font.BOLD, 25);
            boxDrawer = new OutlinedBoxDrawer(BOX, BOX_COLOR, Color.BLACK, THICKNESS.THIN);
            leftBoxDrawer = new ClassicButtonDrawer(LEFT_BOX, BOX_COLOR, Color.BLACK, THICKNESS.THIN, "<", Color.BLACK,
                    buttonFont);
            rightBoxDrawer = new ClassicButtonDrawer(RIGHT_BOX, BOX_COLOR, Color.BLACK, THICKNESS.THIN, ">",
                    Color.BLACK, buttonFont);
            selectBoxDrawer = new ClassicButtonDrawer(SELECT_BOX, BOX_COLOR, Color.BLACK, THICKNESS.THIN,
                    UNSELECTED_SYMBOL, Color.BLACK, buttonFont);
            statsButtonDrawer = new ClassicButtonDrawer(STATS_BOX, STATS_COLOR, Color.BLACK, THICKNESS.THIN,
                    STATS_SYMBOL, Color.BLACK, new Font("Monospaced", Font.BOLD, 20));
        }

        public void configure() {

            FrameBox.updateCenter(Frame.X_SCALE / 2, Frame.Y_SCALE / 2);

            final int LINE_HEIGHT = 15;
            final int size = getFontSizeForHeight(LINE_HEIGHT, new Font("Arial", Font.PLAIN, 100));
            final Font infoFont = new Font("Arial", Font.PLAIN, size);

            guns = gameState.getGuns();
            gunIndex.setN(guns.size());
            displays.clear();
            for (Gun gun : guns) {
                displays.add(new TextDisplay(BOX, gun.getStats(), infoFont, true));
            }

        }

        private void configureUIButtons() {

            indexButtons.add(new IndexButton(LEFT_BOX, gunIndex, IndexButton.TYPE.DECREMENT));
            indexButtons.add(new IndexButton(RIGHT_BOX, gunIndex, IndexButton.TYPE.INCREMENT));
        }

        private void processInput(MouseData mouseData, ArrowData arrowData) {
            statsButton.processInput(mouseData, arrowData);
            selectButton.processInput(mouseData, arrowData);
            if (!statsButton.isPressed()) {
                for (UIButton button : indexButtons)
                    button.processInput(mouseData, arrowData);
            }
        }

        private void draw() {

            if (!statsButton.isPressed()) {
                boxDrawer.setBoxColor(BOX_COLOR);
                boxDrawer.draw();
                getCurrentGun().drawBigAt(BOX.getCenterX(), BOX.getCenterY(), DRAW_BIG_MULTIPLIER);

                statsButtonDrawer.setBoxColor(STATS_COLOR);
                statsButtonDrawer.setText(STATS_SYMBOL);
                statsButtonDrawer.draw();
            } else {
                boxDrawer.setBoxColor(STATS_COLOR);
                boxDrawer.draw();
                statsButtonDrawer.setBoxColor(BOX_COLOR);
                statsButtonDrawer.setText(BACK_SYMBOL);
                statsButtonDrawer.draw();
                displays.get(gunIndex.getCurrent()).draw();
            }

            leftBoxDrawer.draw();
            rightBoxDrawer.draw();

            String symbol = (gunIndex.getCurrent() == gunIndex.getSelect()) ? SELECTED_SYMBOL : UNSELECTED_SYMBOL;
            selectBoxDrawer.setText(symbol);
            selectBoxDrawer.draw();

        }

        private Gun getCurrentGun() {
            return guns.get(gunIndex.getCurrent());
        }

    }

}

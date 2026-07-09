package game;

import helpers.InputHandler;
import helpers.InputHandler.MouseData;
import helpers.InputHandler.ArrowData;
import game.GameState.STATE;
import helpers.UIButton;
import helpers.UIButton.StateButton;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.mapobject.Gun;
import mapobjects.mapobject.Player;
import helpers.UIButton.*;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static helpers.DrawMethods.*;
import static mapobjects.category.GridObject.TILE_SIDE;


// governs skin selection, accessory selection, gun selection and moving to level selection - shop
public class MainSelection {

    private final InputHandler inputHandler;
    private final GameState gameState;

    private final GameMap backgroundMap = new GameMap(new GameState(1, -1), helpers.MapType.SELECTION);

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
        for (UIButton button : UIButtons) button.processInput(mouseData, arrowData);
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

        StdDraw.setXscale(0, Frame.X_SCALE);
        StdDraw.setYscale(Frame.Y_SCALE, 0);
        while (gameState.getState() == STATE.SELECTION) {
            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();
            ArrowData arrowData = inputHandler.getArrowData();
            processInput(mouseData, arrowData);

            StdDraw.clear();
            backgroundMap.draw();
            draw();
            StdDraw.show();
            StdDraw.pause(Frame.PAUSE);
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
        private static final Box NAME_BOX = new Box(CENTER_X, CENTER_Y - SIDE/2 - GAP - BUTTON_SIDE /2, NAME_WIDTH, BUTTON_SIDE);
        private static final Box LEFT_BOX = new Box(CENTER_X - GAP/2 - BUTTON_SIDE/2, CENTER_Y + SIDE/2 + GAP + BUTTON_SIDE /2, BUTTON_SIDE, BUTTON_SIDE);
        private static final Box RIGHT_BOX = new Box(CENTER_X + GAP/2 + BUTTON_SIDE/2, CENTER_Y + SIDE/2 + GAP + BUTTON_SIDE /2, BUTTON_SIDE, BUTTON_SIDE);

        private List<Player> skins;
        private final Index skinIndex = new Index(1);
        private final Set<UIButton> UIButtons = new HashSet<>();

        public SkinSelectionUI() {
            configureNavigationButtons();
        }

        private void configureNavigationButtons() {

            UIButtons.add(new IndexButton(LEFT_BOX, skinIndex, IndexButton.TYPE.DECREMENT));
            UIButtons.add(new IndexButton(RIGHT_BOX, skinIndex, IndexButton.TYPE.INCREMENT));
            UIButtons.add(new ArrowKey(skinIndex, ArrowKey.TYPE.INCREMENT));
            UIButtons.add(new ArrowKey(skinIndex, ArrowKey.TYPE.DECREMENT));

        }

        public void configure() {
            skins = gameState.getSkins();
            skinIndex.setN(skins.size());
        }

        private void processInput(MouseData mouseData, ArrowData arrowData) {
            for (UIButton button : UIButtons) button.processInput(mouseData, arrowData);
        }

        public Player getCurrentSkin() {
            return skins.get(skinIndex.getValue());
        }

        public void draw() {

            Player currentSkin = getCurrentSkin();
            Color color = StdDraw.BLACK;

            Font nameFont = new Font("Monospaced", Font.BOLD, 50);
            textInsideBox(NAME_BOX, currentSkin.getPlayerName(), color, nameFont);

            Font font = new Font("Monospaced", Font.BOLD, 30);
            drawRectangleOutline(LEFT_BOX, color, THICKNESS.THIN);
            drawRectangleOutline(RIGHT_BOX, color, THICKNESS.THIN);
            textInsideBox(LEFT_BOX, "<", color, font);
            textInsideBox(RIGHT_BOX, ">", color, font);

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
        private static final Box BOX = new Box(CENTER_X, CENTER_Y, SIDE, SIDE);
        private static final Box LEFT_BOX = new Box(CENTER_X - BUTTON_SIDE - GAP, CENTER_Y + SIDE/2 + GAP + BUTTON_SIDE/2, BUTTON_SIDE, BUTTON_SIDE);
        private static final Box SELECT_BOX = new Box(CENTER_X, CENTER_Y + SIDE/2 + GAP + BUTTON_SIDE/2, BUTTON_SIDE, BUTTON_SIDE);
        private static final Box RIGHT_BOX = new Box(CENTER_X + BUTTON_SIDE + GAP, CENTER_Y + SIDE/2 + GAP + BUTTON_SIDE/2, BUTTON_SIDE, BUTTON_SIDE);
        private static final Box STATS_BOX = new Box(CENTER_X + SIDE/2, CENTER_Y - SIDE/2, STATS_SIDE, STATS_SIDE);

        private List<Gun> guns;
        private final Index gunIndex = new Index(1);
        private final Set<UIButton> UIButtons = new HashSet<>();

        public GunSelectionUI() {
            configureUIButtons();
        }

        public void configure() {
            guns = gameState.getGuns();
            gunIndex.setN(guns.size());
        }

        private void configureUIButtons() {

            UIButtons.add(new IndexButton(LEFT_BOX, gunIndex, IndexButton.TYPE.DECREMENT));
            UIButtons.add(new IndexButton(RIGHT_BOX, gunIndex, IndexButton.TYPE.INCREMENT));
            UIButtons.add(new IndexButton(SELECT_BOX, gunIndex, IndexButton.TYPE.SELECT));
            UIButtons.add(new BooleanButton(STATS_BOX));

        }

        private void processInput(MouseData mouseData, ArrowData arrowData) {
            for (UIButton button : UIButtons) button.processInput(mouseData, arrowData);
        }

        private void draw() {

            Color boxColor = new Color(208, 146, 95);
            Color color = StdDraw.BLACK;
            Font font = new Font("Monospaced", Font.BOLD, 30);

            drawRectWithOutline(BOX, boxColor, color);
            drawRectWithOutline(LEFT_BOX, boxColor, color);
            drawRectWithOutline(RIGHT_BOX, boxColor, color);

            textInsideBox(LEFT_BOX, "<", color, font);
            textInsideBox(RIGHT_BOX, ">", color, font);
            if (gunIndex.getValue() == gunIndex.getSelect()) {
                textInsideBox(SELECT_BOX, "✅", color, font);
            } else {
                textInsideBox(SELECT_BOX, "❎", color, font);
            }
            getCurrentGun().drawBigAt(BOX.getCenterX(), BOX.getCenterY(), DRAW_BIG_MULTIPLIER);

        }

        private Gun getCurrentGun() {
            return guns.get(gunIndex.getValue());
        }

    }

}

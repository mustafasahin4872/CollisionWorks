package game;

import game.GameState.STATE;
import helpers.InputHandler;
import helpers.InputHandler.*;
import helpers.UIButton;
import helpers.UIButton.*;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static helpers.DrawMethods.drawRectWithOutline;
import static helpers.DrawMethods.textInsideBox;
import static mapobjects.category.GridObject.TILE_SIDE;

public class AccessorySelection {

    private final InputHandler inputHandler;
    private final GameState gameState;

    private final GameMap backgroundMap = new GameMap(new GameState(1, -2), helpers.MapType.SELECTION);

    private final Set<UIButton> UIButtons = new HashSet<>();

    private final List<AccessorySelectionUI> uis = new ArrayList<>();
    private final MannequinUI mannequinUI = new MannequinUI();

    public AccessorySelection(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;

        configureNavigationButtons(gameState);
    }

    private void configureNavigationButtons(GameState gameState) {
        final double yShift = 2.5 * TILE_SIDE;
        final double startY = 3.2 * TILE_SIDE;
        uis.add(new AccessorySelectionUI(4 * TILE_SIDE, startY));
        uis.add(new AccessorySelectionUI(4 * TILE_SIDE, startY + yShift));
        uis.add(new AccessorySelectionUI(4 * TILE_SIDE, startY + 2 * yShift));
        Box box = new Box(Frame.X_SCALE - TILE_SIDE ,6 * TILE_SIDE ,2 * TILE_SIDE ,2 * TILE_SIDE);
        UIButtons.add(new StateButton(box, gameState, STATE.SELECTION));
    }

    public void processInput(InputHandler.MouseData mouseData, InputHandler.ArrowData arrowData) {
        for (UIButton button : UIButtons) button.processInput(mouseData, arrowData);
        for (AccessorySelectionUI ui : uis) ui.processInput(mouseData, arrowData);
    }

    public void draw() {
        backgroundMap.draw();
        for (AccessorySelectionUI ui : uis) ui.draw();
        mannequinUI.draw();
    }

    public void accessorySelectionLoop() {

        List<Accessory> accessories = gameState.getAccessories();

        // first elements are null - no accessory selected
        List<Accessory.Headwear> headwears = new ArrayList<>();
        headwears.add(null);
        List<Accessory.Neckwear> neckwears = new ArrayList<>();
        neckwears.add(null);
        List<Accessory.Brooch> brooches = new ArrayList<>();
        brooches.add(null);
        for (Accessory accessory : accessories) {
            if (accessory == null) continue;
            accessory.setAlone(true);
            switch (accessory) {
                case Accessory.Headwear headwear -> headwears.add(headwear);
                case Accessory.Neckwear neckwear -> neckwears.add(neckwear);
                case Accessory.Brooch brooch -> brooches.add(brooch);
                default -> {}
            }
        }
        uis.get(0).setAccessories(headwears);
        uis.get(1).setAccessories(neckwears);
        uis.get(2).setAccessories(brooches);

        StdDraw.setXscale(0, Frame.X_SCALE);
        StdDraw.setYscale(Frame.Y_SCALE, 0);

        while (gameState.getState() == GameState.STATE.ACCESSORY) {
            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();
            ArrowData arrowData = inputHandler.getArrowData();

            processInput(mouseData, arrowData);

            StdDraw.clear();
            draw();
            StdDraw.show();
            StdDraw.pause(Frame.PAUSE);
        }

        ArrayList<Accessory> selected = new ArrayList<>();
        for (AccessorySelectionUI ui : uis) {
            Accessory accessory = ui.getSelectedAccessory();
            if (accessory != null) {
                selected.add(accessory);
            }
        }
        gameState.setEquipped(selected.toArray(new Accessory[0]));

    }

    private static class AccessorySelectionUI {

        private List<? extends Accessory> accessories;

        private static final double BUTTON_SIDE = 0.4 * TILE_SIDE;
        private static final double GAP = 0.2 * TILE_SIDE;
        private static final double SIDE = 3 * BUTTON_SIDE + 2 * GAP;
        private static final double DRAW_BIG_MULTIPLIER = SIDE / TILE_SIDE;

        private final Box BOX;
        private final Box RIGHT_BOX;
        private final Box LEFT_BOX;
        private final Box SELECT_BOX;
        private final IndexButton[] indexButtons = new IndexButton[3];
        private final Index index;

        public AccessorySelectionUI(double centerX, double centerY) {
            this.BOX = new Box(centerX, centerY, SIDE, SIDE);
            this.SELECT_BOX = new Box(centerX, centerY + SIDE/2 + GAP + BUTTON_SIDE/2, BUTTON_SIDE, BUTTON_SIDE);
            this.LEFT_BOX = new Box(centerX - GAP - BUTTON_SIDE, centerY + SIDE/2 + GAP + BUTTON_SIDE/2, BUTTON_SIDE, BUTTON_SIDE);
            this.RIGHT_BOX = new Box(centerX + GAP + BUTTON_SIDE, centerY + SIDE/2 + GAP + BUTTON_SIDE/2, BUTTON_SIDE, BUTTON_SIDE);
            this.index = new Index(1);
            configureNavigationButtons();
        }

        private void setAccessories(List<? extends Accessory> accessories) {
            this.accessories = accessories;
            index.setN(accessories.size());
        }

        private void configureNavigationButtons() {
            indexButtons[0] = new IndexButton(LEFT_BOX, index, IndexButton.TYPE.DECREMENT);
            indexButtons[1] = new IndexButton(RIGHT_BOX, index, IndexButton.TYPE.INCREMENT);
            indexButtons[2] = new IndexButton(SELECT_BOX, index, IndexButton.TYPE.SELECT);
        }

        private void processInput(MouseData mouseData, ArrowData arrowData) {
            for (IndexButton button : indexButtons) button.processInput(mouseData, arrowData);
        }

        private Accessory getSelectedAccessory() {
            if (accessories.isEmpty()) return null;
            return accessories.get(index.getSelect());
        }

        private void draw() {

            Color boxColor;
            Accessory accessory = accessories.get(index.getCurrent());
            if (accessory == null) {
                boxColor = new Color(208, 146, 95);
            }
            else {
                boxColor = accessory.getRarity().getColor();
            }

            Color color = StdDraw.BLACK;
            Font font = new Font("Monospaced", Font.BOLD, 20);

            drawRectWithOutline(BOX, boxColor, color);
            drawRectWithOutline(LEFT_BOX, boxColor, color);
            drawRectWithOutline(RIGHT_BOX, boxColor, color);

            textInsideBox(LEFT_BOX, "<", color, font);
            textInsideBox(RIGHT_BOX, ">", color, font);
            if (index.getCurrent() == index.getSelect()) {
                textInsideBox(SELECT_BOX, "✅", color, font);
            } else {
                textInsideBox(SELECT_BOX, "❎", color, font);
            }
            if (accessory == null) return;
            accessory.drawBigAt(BOX.getCenterX(), BOX.getCenterY(), DRAW_BIG_MULTIPLIER);

        }

    }

    private class MannequinUI {

        private static final double CENTER_X = 9 * TILE_SIDE;
        private static final double CENTER_Y = 6 * TILE_SIDE;
        private static final double DRAW_BIG_MULTIPLIER = 3;

        public void draw() {
            Player player = gameState.getPlayer();
            player.drawBigAt(CENTER_X, CENTER_Y, DRAW_BIG_MULTIPLIER);
            for (AccessorySelectionUI ui : uis) {
                Accessory accessory = ui.getSelectedAccessory();
                if (accessory == null) continue;
                accessory.setAlone(false);
                accessory.setPlayer(player);
                accessory.drawBigAt(CENTER_X, CENTER_Y, DRAW_BIG_MULTIPLIER);
                accessory.setAlone(true);
            }
        }

    }

}

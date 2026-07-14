package mapobjects.entities;

import game.io.Drawer.PictureDrawer;
import mapobjects.components.Box;
import mapobjects.traits.Drawable;
import mapobjects.traits.OnEffector;
import mapobjects.traits.GridObject;

import static game.core.GameState.gameState;

public class Currency extends GridObject implements OnEffector, Drawable {

    public enum CurrencyType {
        singleCoin(1, 0.6), tripleCoin(3, 0.8), coinBag(10, 1.2),
        singleGem(1, 0.6), tripleGem(3, 0.8), gemBag(10, 1.2);

        private final int value;
        private final double side;

        CurrencyType(int value, double side) {
            this.value = value;
            this.side = side;
        }
    }

    private final Box effectBox;
    protected final int value;
    private final CurrencyType type;
    private final PictureDrawer drawer;

    public Currency(int worldIndex, int xNum, int yNum, CurrencyType type) {
        super(worldIndex, xNum, yNum, type.side, type.side);
        effectBox = positionBox.clone();
        this.value = type.value;
        this.type = type;
        drawer = new PictureDrawer(positionBox, getDirectory1(), type.name());
    }

    @Override
    public Box getTriggerBox() {
        return effectBox;
    }

    @Override
    public void playerIsOn(Player player) {
        collect();
    }

    public void collect() {
        switch (type) {
            case CurrencyType.singleCoin, CurrencyType.tripleCoin, CurrencyType.coinBag-> gameState.collectCoin(value);
            case CurrencyType.singleGem, CurrencyType.tripleGem, CurrencyType.gemBag -> gameState.collectGem(value);
        }
        expire();
    }

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }

}

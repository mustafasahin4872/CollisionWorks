package mapobjects.entities;

import mapobjects.components.Box;
import mapobjects.traits.OnEffector;
import mapobjects.traits.GridObject;

import static game.core.Main.gameState;

public class Currency extends GridObject implements OnEffector {

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

    public Currency(int worldIndex, int xNum, int yNum, CurrencyType type) {
        super(worldIndex, xNum, yNum, type.side, type.side);
        effectBox = positionBox.clone();
        this.value = type.value;
        this.type = type;
        setName(type.name());
    }

    @Override
    public Box getEffectBox() {
        return effectBox;
    }

    @Override
    public void checkPlayerIsOn(Player player) {
        checkPlayerCornerIsOn(player);
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

}

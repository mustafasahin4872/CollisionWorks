package mapobjects.mapobject;

import mapobjects.component.Box;
import mapobjects.category.OnEffector;
import mapobjects.category.GridObject;

public abstract class Coin extends GridObject implements OnEffector {

    private final Box effectBox;
    private final int value;

    public Coin(int worldIndex, int xNum, int yNum, double size, int value) {
        super(worldIndex, xNum, yNum, size, size);
        effectBox = positionBox.clone();
        this.value = value;
    }


    public int getValue() {
        return value;
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
        collect(player);
    }

    private void collect(Player player) {
        player.collectCoin(value);
        expire();
    }


    public static class SingleCoin extends Coin {
        private static final double SIZE = 0.6;
        private static final int VALUE = 1;

        public SingleCoin(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, SIZE, VALUE);
        }
    }

    public static class TripleCoin extends Coin {
        private static final double SIZE = 0.8;
        private static final int VALUE = 3;

        public TripleCoin(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, SIZE, VALUE);
        }
    }

    public static class CoinBag extends Coin {
        private static final double SIZE = 1.2;
        private static final int VALUE = 10;

        public CoinBag(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, SIZE, VALUE);
        }
    }
}

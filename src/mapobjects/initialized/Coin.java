package mapobjects.initialized;

import game.Player;
import mapobjects.framework.Box;
import mapobjects.framework.OnEffector;
import mapobjects.framework.GridObject;

public abstract class Coin extends GridObject implements OnEffector {

    private final Box effectBox;
    private final int value;

    public Coin(int worldIndex, int xNum, int yNum, double size, int value, String fileName) {
        super(worldIndex, xNum, yNum, size, size, fileName);
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
        private static final String FILE = "misc/coinImages/coin.png";

        public SingleCoin(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, SIZE, VALUE, FILE);
        }
    }

    public static class TripleCoin extends Coin {
        private static final double SIZE = 0.8;
        private static final int VALUE = 3;
        private static final String FILE = "misc/coinImages/tripleCoin.png";

        public TripleCoin(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, SIZE, VALUE, FILE);
        }
    }

    public static class CoinBag extends Coin {
        private static final double SIZE = 1.2;
        private static final int VALUE = 10;
        private static final String FILE = "misc/coinImages/coinBag.png";

        public CoinBag(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, SIZE, VALUE, FILE);
        }
    }
}

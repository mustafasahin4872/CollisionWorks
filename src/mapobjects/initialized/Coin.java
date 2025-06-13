package mapobjects.initialized;

import game.Player;
import mapobjects.framework.EffectBox;
import mapobjects.framework.Effector;
import mapobjects.framework.MapObject;

public abstract class Coin extends MapObject implements Effector {

    private final EffectBox effectBox;
    private final int value;

    public Coin(int worldIndex, int xNum, int yNum, double size, int value, String fileName) {
        super(worldIndex, xNum, yNum, size, size, fileName);
        effectBox = new EffectBox(this);
        this.value = value;
    }


    public int getValue() {
        return value;
    }

    @Override
    public double[] getEffectBox() {
        return effectBox.getEffectBox();
    }


    @Override
    public void call(Player player) {
        checkPlayerIsOn(player);
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

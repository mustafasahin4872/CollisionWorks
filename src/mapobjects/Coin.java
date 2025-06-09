package mapobjects;

import game.Player;

public class Coin extends MapObject {

    private boolean collected = false;
    private final int coinAmount;

    public Coin(int xNum, int yNum, double size, int coinAmount, String fileName) {
        super(xNum, yNum, size, size, fileName);
        this.coinAmount = coinAmount;
    }

    public int getCoinAmount() {
        return coinAmount;
    }

    @Override
    public void playerIsOn(Player player) {
        if (collected) return;
        player.collectCoin(coinAmount);
        collected = true;
    }

    @Override
    public void draw() {
        if (collected) return;
        super.draw();
    }

    public static class SingleCoin extends Coin {

        public SingleCoin(int xNum, int yNum) {
            super(xNum, yNum, 0.6, 1, "misc/coinImages/coin.png");
        }
    }

    public static class TripleCoin extends Coin {
        public TripleCoin(int xNum, int yNum) {
            super(xNum, yNum, 0.8, 3, "misc/coinImages/tripleCoin.png");
        }
    }

    public static class CoinBag extends Coin {
        public CoinBag(int xNum, int yNum) {
            super(xNum, yNum, 1.2,10, "misc/coinImages/coinBag.png");
        }
    }
}

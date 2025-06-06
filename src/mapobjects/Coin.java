package mapobjects;

import game.Player;
import lib.StdDraw;

public class Coin extends MapObject {

    private boolean collected = false;
    private final String fileName;
    private final int coinAmount;
    private final double size;

    public Coin(int xNum, int yNum, double size, int coinAmount, String fileName) {
        super(xNum, yNum);
        this.size = size;
        this.coinAmount = coinAmount;
        this.fileName = fileName;

        double spaceOnSide = (TILE_SIDE - size) / 2;
        coordinates[0] += spaceOnSide;
        coordinates[1] += spaceOnSide;
        coordinates[2] -= spaceOnSide;
        coordinates[3] -= spaceOnSide;
        collisionBox = coordinates;
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
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], fileName, size, size);
    }

    public static class SingleCoin extends Coin {

        public SingleCoin(int xNum, int yNum) {
            super(xNum, yNum, 30, 1, "misc/coinImages/coin.png");
        }
    }

    public static class TripleCoin extends Coin {
        public TripleCoin(int xNum, int yNum) {
            super(xNum, yNum, 40, 3, "misc/coinImages/tripleCoin.png");
        }
    }

    public static class CoinBag extends Coin {
        public CoinBag(int xNum, int yNum) {
            super(xNum, yNum, 60,10, "misc/coinImages/coinBag.png");
        }
    }
}

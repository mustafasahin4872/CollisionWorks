public class Coin implements Passable, Drawable {

    private boolean collected = false;
    private final String fileName;
    private final int xNum;
    private final int yNum;
    private final int coinAmount;
    private final double[] coordinates;

    private double size;
    private double spaceOnSide;

    public Coin(int xNum, int yNum, double size, int coinAmount, String fileName) {
        this.xNum = xNum;
        this.yNum = yNum;
        this.size = size;
        spaceOnSide = (Tile.HALF_SIDE*2- size)/2;
        this.coinAmount = coinAmount;
        this.fileName = fileName;
        coordinates = new double[]{
                (xNum - 1) * Tile.HALF_SIDE * 2 + spaceOnSide, (Tile.Y_TILE - yNum) * Tile.HALF_SIDE * 2 + spaceOnSide,
                xNum * Tile.HALF_SIDE * 2 - spaceOnSide, (Tile.Y_TILE - yNum + 1) * Tile.HALF_SIDE * 2 - spaceOnSide
        };
    }

    public int getCoinAmount() {
        return coinAmount;
    }

    @Override
    public void playerIsOn(Player player) {
        if (!collected) {
            player.collectCoin(coinAmount);
        }
        collected = true;
    }

    @Override
    public double[] getCoordinates() {
        return coordinates;
    }

    @Override
    public void draw() {
        if (!collected) {
            StdDraw.picture((coordinates[0]+coordinates[2])/2, (coordinates[1]+coordinates[3])/2,
                    fileName, size, size);
        }
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

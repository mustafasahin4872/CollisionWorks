public class Chest implements Passable, Drawable {

    private final int xNum;
    private final int yNum;
    private final int coinNum;
    private final double[] coordinates;
    private final double[] collisionBox;
    private final String type;
    private final char[] buffs;
    private final double buffTime;
    private boolean isOpen = false;


    public Chest(int xNum, int yNum, String type, char[] buffs, double buffTime, int coinNum) {
        this.xNum = xNum;
        this.yNum = yNum;
        {
            double tileSide = Tile.HALF_SIDE*2;
            double x0 = (xNum - 1) * tileSide;
            double x1 = (xNum + 1) * tileSide;
            double y0 = (yNum - 1) * tileSide;
            double y1 = (yNum + 1) * tileSide;
            coordinates = new double[]{x0, y0, x1, y1};
            collisionBox = new double[]{x0, y0, x1, (y0 + y1) / 2};
        }
        this.type = type;
        this.buffs = buffs;
        this.buffTime = buffTime;
        this.coinNum = coinNum;
    }

    @Override
    public double[] getCoordinates() {
        return coordinates;
    }

    public int getCoinNum() {
        return coinNum;
    }

    @Override
    public void playerIsOn(Player player) {
        if (isOpen) return;
        isOpen = true;
        player.buff(buffs, buffTime);
        player.collectCoin(coinNum);
    }

    @Override
    public void draw() {

        String fileName;
        if (isOpen) {
            fileName = "misc/chestImages/opened" + type + ".png";
        } else {
            fileName = "misc/chestImages/closed" + type + ".png";
        }
        System.out.println(1);
        StdDraw.picture(
                (coordinates[0]+coordinates[2])/2, (coordinates[1]+coordinates[3])/2,
                fileName, Tile.HALF_SIDE*4, Tile.HALF_SIDE*4
        );
    }


    public static class WoodenChest extends Chest {
        public WoodenChest(int xNum, int yNum, char[] buff) {
            super(xNum, yNum, "WoodenChest", buff, 10, 5);
        }
    }

    public static class SilverChest extends Chest {
        public SilverChest(int xNum, int yNum, char[] buff) {
            super(xNum, yNum, "SilverChest", buff, 20, 10);
        }
    }

    public static class GoldenChest extends Chest {
        public GoldenChest(int xNum, int yNum, char[] buff) {
            super(xNum, yNum, "GoldenChest", buff, 30, 20);
        }
    }

}

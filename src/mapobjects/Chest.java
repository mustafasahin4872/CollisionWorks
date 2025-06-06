package mapobjects;

import game.Player;
import lib.StdDraw;

public abstract class Chest extends MapObject {

    private boolean isOpen;
    private final String type;
    private final double buffTime;
    private final char[] buffs;
    private final int coinNum;
    private String fileName;

    public Chest(int xNum, int yNum, String type, char[] buffs, double buffTime, int coinNum) {
        super(xNum, yNum);
        this.type = type;
        this.buffs = buffs;
        this.buffTime = buffTime;
        this.coinNum = coinNum;
        fileName = "misc/chestImages/closed" + type + ".png";
        set2x2CenterCoordinates();
        set2x2Coordinates();
        collisionBox = new double[]{coordinates[0], (coordinates[1] + coordinates[3])/2, coordinates[2], coordinates[3]};
    }

    public int getCoinNum() {
        return coinNum;
    }

    @Override
    public void playerIsOn(Player player) {
        if (isOpen) return;
        isOpen = true;
        fileName = "misc/chestImages/opened" + type + ".png";
        player.buff(buffs, buffTime);
        player.collectCoin(coinNum);
    }

    @Override
    public void draw() {
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], fileName, TILE_SIDE*2, TILE_SIDE*2);
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

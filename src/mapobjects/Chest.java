package mapobjects;

import game.Player;

public abstract class Chest extends MapObject {

    private boolean isOpen;
    private final String type;
    private final double buffTime;
    private final char[] buffs;
    private final int coinNum;

    public Chest(int xNum, int yNum, String type, char[] buffs, double buffTime, int coinNum) {
        super(xNum, yNum, 2, 2, "misc/chestImages/closed" + type + ".png", true);
        this.type = type;
        this.buffs = buffs;
        this.buffTime = buffTime;
        this.coinNum = coinNum;
        setCollisionBox(new double[]{coordinates[0], (coordinates[1] + coordinates[3])/2, coordinates[2], coordinates[3]});
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

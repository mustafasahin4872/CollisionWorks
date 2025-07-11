package mapobjects.initialized;

import game.Player;
import mapobjects.framework.Box;
import mapobjects.framework.GridObject;
import mapobjects.framework.OnEffector;

public abstract class Chest extends GridObject implements OnEffector {

    private final Box effectBox;
    private boolean isOpen;
    private final String type;
    private final double buffTime;
    private final char[] buffs;
    private final int coinNum;

    public Chest(int worldIndex, int xNum, int yNum, String type, char[] buffs, double buffTime, int coinNum) {
        super(worldIndex, xNum, yNum, 2, 2, "misc/chestImages/closed" + type + ".png", true);
        this.type = type;
        this.buffs = buffs;
        this.buffTime = buffTime;
        this.coinNum = coinNum;
        effectBox = positionBox.clone();
        effectBox.setCorners(new double[]{
                positionBox.getCorner(0), (positionBox.getCorner(1) + positionBox.getCorner(3)) / 2,
                positionBox.getCorner(2), positionBox.getCorner(3)
        });
    }


    public int getCoinNum() {
        return coinNum;
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
        openChest(player);
    }


    public void openChest(Player player) {
        if (isOpen) return;
        isOpen = true;
        setFileName("misc/chestImages/opened" + type + ".png");
        player.buff(buffs, buffTime);
        player.collectCoin(coinNum);
    }


    public static class WoodenChest extends Chest {
        private static final String TYPE = "WoodenChest";
        private static final double BUFF_TIME = 10;
        private static final int COIN_NUM = 5;

        public WoodenChest(int worldIndex, int xNum, int yNum, char[] buff) {
            super(worldIndex, xNum, yNum, TYPE, buff, BUFF_TIME, COIN_NUM);
        }
    }

    public static class SilverChest extends Chest {
        private static final String TYPE = "SilverChest";
        private static final double BUFF_TIME = 20;
        private static final int COIN_NUM = 10;

        public SilverChest(int worldIndex, int xNum, int yNum, char[] buff) {
            super(worldIndex, xNum, yNum, TYPE, buff, BUFF_TIME, COIN_NUM);
        }
    }

    public static class GoldenChest extends Chest {
        private static final String TYPE = "GoldenChest";
        private static final double BUFF_TIME = 30;
        private static final int COIN_NUM = 20;

        public GoldenChest(int worldIndex, int xNum, int yNum, char[] buff) {
            super(worldIndex, xNum, yNum, TYPE, buff, BUFF_TIME, COIN_NUM);
        }
    }

}

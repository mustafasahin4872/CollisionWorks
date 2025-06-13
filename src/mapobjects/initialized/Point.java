package mapobjects.initialized;

import game.Player;
import lib.StdDraw;
import mapobjects.framework.EffectBox;
import mapobjects.framework.Effector;
import mapobjects.framework.MapObject;

import static helperobjects.CollisionMethods.playerCenterIsIn;

public abstract class Point extends MapObject implements Effector {

    protected final int index;
    protected boolean isBig;

    public Point(int worldIndex, int xNum, int yNum, int index, String type, boolean isBig) {
        super(worldIndex, xNum, yNum, getSize(isBig), getSize(isBig), "misc/" + type + "Images/" + index + ".png", isBig);
        this.index = index;
        this.isBig = isBig;
    }

    private static int getSize(boolean isBig) {
        return isBig ? 2 : 1;
    }

    @Override
    public abstract void playerIsOn(Player player);

    @Override
    public void draw() {
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], fileName, TILE_SIDE, TILE_SIDE);
    }

    public static class WinPoint extends Point implements Effector {

        private final EffectBox effectBox;
        private final Player.PASSCODE passcode;

        public WinPoint(int worldIndex, int xNum, int yNum, int index) {
            this(worldIndex, xNum, yNum, index, false);
        }

        public WinPoint(int worldIndex, int xNum, int yNum, int index, boolean isBig) {
            super(worldIndex, xNum, yNum, index, "winPoint", isBig);
            effectBox = new EffectBox(this);
            passcode = switch (index) {
                case 0 -> Player.PASSCODE.NEXT;
                case 1 -> Player.PASSCODE.ALTERNATE1;
                case 2 -> Player.PASSCODE.ALTERNATE2;
                case 3 -> Player.PASSCODE.ALTERNATE3;
                case 4 -> Player.PASSCODE.SHOP;
                default -> Player.PASSCODE.ZERO;
            };
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
            player.setPassCode(passcode);
        }

    }

    public static class CheckPoint extends Point implements Effector {

        private final EffectBox effectBox;
        private CheckPoint prev;
        protected boolean visited;
        private static final Sign ERROR_SIGN = new Sign(0, 0, 0, new String[]{"first unlock the previous checkpoint"}, false);

        public CheckPoint(int worldIndex, int xNum, int yNum, int index) {
            this(worldIndex, xNum, yNum, index, false);
        }

        public CheckPoint(int worldIndex, int xNum, int yNum, int index, boolean isBig) {
            super(worldIndex, xNum, yNum, index, "checkPoint", isBig);
            effectBox = new EffectBox(this);
        }


        @Override
        public double[] getEffectBox() {
            return effectBox.getEffectBox();
        }

        @Override
        public void call(Player player) {
            if (visited) return;
            checkPlayerCenterIsOn(player);
            ERROR_SIGN.call(player);
        }

        @Override
        public void playerIsOn(Player player) {
            int lastCheckPointIndex = player.getLastCheckPointIndex();

            if (index == lastCheckPointIndex + 1) {
                markVisited(player);
            } else if (index > lastCheckPointIndex) {
                ERROR_SIGN.setDisplay();
            }
        }

        private void markVisited(Player player) {
            visited = true;
            setFileName("misc/checkPointImages/0.png");
            if (prev != null) prev.setFileName("misc/checkPointImages/-1.png");
            player.updateLastCheckPointIndex();
            player.setSpawnPoint(centerCoordinates);
        }

        @Override
        public void draw() {
            super.draw();
            ERROR_SIGN.draw();
        }

        public void setPrev(CheckPoint prev) {
            this.prev = prev;
        }

    }

    public static class SpawnPoint extends CheckPoint {

        public SpawnPoint(int worldIndex, int xNum, int yNum) {
            this(worldIndex, xNum, yNum, false);
        }

        public SpawnPoint(int worldIndex, int xNum, int yNum, boolean isBig) {
            super(worldIndex, xNum, yNum, 0, isBig);
            visited = true;
        }


        @Override
        public void call(Player player) {} //no need to call

        @Override
        public void playerIsOn(Player player) {} //no action needed

    }

}
package mapobjects.mapobject;

import game.Player;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.category.OnEffector;
import mapobjects.category.GridObject;

public abstract class Point extends GridObject implements OnEffector {

    protected final int index;
    protected boolean isBig;

    public Point(int worldIndex, int xNum, int yNum, int index, String type, boolean isBig) {
        super(worldIndex, xNum, yNum, getSize(isBig), getSize(isBig), "src/main/resources/" + type + "Images/" + index + ".png", isBig);
        this.index = index;
        this.isBig = isBig;
    }

    private static int getSize(boolean isBig) {
        return isBig ? 2 : 1;
    }

    @Override
    public void checkPlayerIsOn(Player player) {
        checkPlayerCornerIsOn(player);
    }

    //special override for the selection screen
    @Override
    public void draw() {
        StdDraw.picture(getX(), getY(), fileName, TILE_SIDE, TILE_SIDE);
    }

    public static class WinPoint extends Point implements OnEffector {

        private final Box effectBox;
        private final Player.PASSCODE passcode;

        public WinPoint(int worldIndex, int xNum, int yNum, int index) {
            this(worldIndex, xNum, yNum, index, false);
        }

        public WinPoint(int worldIndex, int xNum, int yNum, int index, boolean isBig) {
            super(worldIndex, xNum, yNum, index, "winPoint", isBig);
            effectBox = positionBox.clone();
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
        public Box getEffectBox() {
            return effectBox;
        }

        @Override
        public void playerIsOn(Player player) {
            player.setPassCode(passcode);
        }

    }

    public static class CheckPoint extends Point implements OnEffector {

        private final Box effectBox;
        private CheckPoint prev;
        protected boolean visited;
        private static final Sign ERROR_SIGN = new Sign(0, 0, 0, new String[]{"first unlock the previous checkpoint"}, false);

        public CheckPoint(int worldIndex, int xNum, int yNum, int index) {
            this(worldIndex, xNum, yNum, index, false);
        }

        public CheckPoint(int worldIndex, int xNum, int yNum, int index, boolean isBig) {
            super(worldIndex, xNum, yNum, index, "checkPoint", isBig);
            effectBox = positionBox.clone();
        }


        @Override
        public Box getEffectBox() {
            return effectBox;
        }

        @Override
        public void call(Player player) {
            if (visited) return;
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
            setFileName("src/main/resources/checkPointImages/0.png");
            if (prev != null) prev.setFileName("src/main/resources/checkPointImages/-1.png");
            player.updateLastCheckPointIndex();
            player.setSpawnPoint(getCenterCoordinates());
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
        public void playerIsOn(Player player) {} //no action needed

    }

}
package mapobjects;

import game.Player;
import lib.StdDraw;

public abstract class Point extends MapObject{

    protected final int index;
    protected String fileName;
    protected boolean isBig;

    public Point(int xNum, int yNum, int index, String type) {
        this(xNum, yNum, index, type, false);
    }

    public Point(int xNum, int yNum, int index, String type, boolean isBig) {
        super(xNum, yNum);
        this.index = index;
        this.isBig = isBig;
        fileName = "misc/"+type+"Images/"+index+".png";
        if (isBig) {
            set2x2CenterCoordinates();
            set2x2Coordinates();
        }
    }

    @Override
    public abstract void playerIsOn(Player player);

    @Override
    public void draw() {
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], fileName, TILE_SIDE, TILE_SIDE);
    }

    public static class WinPoint extends Point {
        private final Player.PASSCODE passcode;
        public WinPoint(int xNum, int yNum, int index) {
            this(xNum, yNum, index, false);
        }

        public WinPoint(int xNum, int yNum, int index, boolean isBig) {
            super(xNum, yNum, index, "winPoint", isBig);
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
        public void playerIsOn(Player player) {
            player.setPassCode(passcode);
        }
    }

    public static class CheckPoint extends Point {

        private CheckPoint prev;
        protected boolean visited;
        private final Sign errorSign = new Sign(0, xNum, yNum, new String[]{"first unlock the previous checkpoint"}, false);

        public CheckPoint(int xNum, int yNum, int index) {
            this(xNum, yNum, index, false);
        }

        public CheckPoint(int xNum, int yNum, int index, boolean isBig) {
            super(xNum, yNum, index, "checkPoint", isBig);
        }

        @Override
        public void playerIsOn(Player player) {
            if (visited) return;
            int lastCheckPointIndex = player.getLastCheckPoint();
            if (index==lastCheckPointIndex+1) {
                player.updateLastCheckPoint();
                visited = true;
                fileName = "misc/checkPointImages/0.png";
                prev.updateFileName();
                player.setSpawnPoint(centerCoordinates);
            } else if (index>lastCheckPointIndex) {
                errorSign.playerIsOn(player);
            }
        }

        public Sign getErrorSign() {
            return errorSign;
        }

        public void setPrev(CheckPoint prev) {
            this.prev = prev;
        }

        public void updateFileName() {
            fileName = "misc/checkPointImages/-1.png";
        }
    }

    public static class SpawnPoint extends CheckPoint {
        public SpawnPoint(int xNum, int yNum) {
            this(xNum, yNum, false);
        }

        public SpawnPoint(int xNum, int yNum, boolean isBig) {
            super(xNum, yNum, 0, isBig);
            visited = true;
        }

        @Override
        public void playerIsOn(Player player) {} //no action needed

    }
}

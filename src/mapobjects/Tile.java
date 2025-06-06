package mapobjects;

import game.Player;
import lib.StdDraw;

public abstract class Tile extends MapObject {
    public static final double HALF_SIDE = TILE_SIDE/2;

    private final boolean isSolid;
    private final String fileName;
    private static final String ROOT = "misc/tileImages/";
    private final boolean isApproachable; //false for tiles locked inside solid tiles

    // Main constructor
    public Tile(int xNum, int yNum, boolean isApproachable, boolean isSolid, int worldIndex, String fileName) {
        super(worldIndex, xNum, yNum);
        coordinates = new double[]{
                (xNum - 1) * HALF_SIDE * 2, (yNum-1) * HALF_SIDE * 2,
                xNum * HALF_SIDE * 2, yNum * HALF_SIDE * 2
        };
        this.isApproachable = isApproachable;
        this.isSolid = isSolid;
        this.fileName = fileName;
    }


    public boolean isSolid() {
        return isSolid;
    }

    public boolean isApproachable() {
        return isApproachable;
    }

    @Override
    public double[] getCollisionBox() {
        return coordinates;
    }

    @Override
    public void draw() {
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], fileName, HALF_SIDE*2, HALF_SIDE*2);
    }

    public abstract void playerIsOn(Player player);

    public static class SpaceTile extends Tile {
        public SpaceTile(int xNum, int yNum, int worldIndex) {
            super(xNum, yNum, true, false, worldIndex, ROOT + "SpaceTile" + worldIndex + ".jpg");
        }

        public void playerIsOn(Player player) {
            player.resetDeceleration();
            player.resetAcceleration();
            player.resetMaxSpeed();
        }

    }

    public static class WallTile extends Tile {
        public WallTile(int xNum, int yNum, boolean isApproachable, int worldIndex) {
            super(xNum, yNum, isApproachable,true, worldIndex, ROOT + "WallTile" + worldIndex + ".jpg");
        }

        public void playerIsOn(Player player) {} //impossible

    }

    public static class RiverTile extends Tile {
        public RiverTile(int xNum, int yNum, boolean isApproachable, int worldIndex) {
            super(xNum, yNum, isApproachable,true, worldIndex, ROOT + "RiverTile" + worldIndex + ".jpg");
        }

        public void playerIsOn(Player player) {} //impossible

    }

    public static class SlowTile extends Tile {
        public SlowTile(int xNum, int yNum) {
            super(xNum, yNum, true,false, 1, ROOT + "SlowTile" + ".jpg"); // earthy brown
        }

        public void playerIsOn(Player player) {
            player.slow();
        }

    }

    public static class SpecialTile extends Tile {
        public SpecialTile(int xNum, int yNum) {
            super(xNum, yNum, true, false,2, ROOT + "SpecialTile" + ".jpg"); // icy blue
        }

        public void playerIsOn(Player player) {
            player.slip();
        }

    }

    public static class DamageTile extends Tile {
        public DamageTile(int xNum, int yNum, int worldIndex) {
            super(xNum, yNum, true, false, worldIndex, ROOT + "DamageTile" + worldIndex + ".jpg");
        }

        public void playerIsOn(Player player) {
            player.resetDeceleration();
            player.resetAcceleration();
            player.resetMaxSpeed();
            player.damage(worldIndex);
        }

    }

    public static class HealTile extends Tile {
        public HealTile(int xNum, int yNum, int worldIndex) { //golden yellow tile, draw a + in it
            super(xNum, yNum, true, false, worldIndex, ROOT + "HealTile" + ".jpg");
        }

        public void playerIsOn(Player player) {
            player.resetDeceleration();
            player.resetAcceleration();
            player.resetMaxSpeed();
            player.heal(3.0/worldIndex);
        }

    }
}
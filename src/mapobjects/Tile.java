package mapobjects;

import game.Player;

public abstract class Tile extends MapObject {

    private static final String ROOT = "misc/tileImages/";
    private final boolean isSolid, isApproachable;

    public Tile(int worldIndex, int xNum, int yNum, boolean isApproachable, boolean isSolid, String type) {
        super(worldIndex, xNum, yNum, ROOT + type + worldIndex + ".jpg");
        this.isApproachable = isApproachable;
        this.isSolid = isSolid;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public boolean isApproachable() {
        return isApproachable;
    }

    protected void resetPlayerStats(Player player) {
        player.resetDeceleration();
        player.resetAcceleration();
        player.resetMaxSpeed();
    }

    public abstract void playerIsOn(Player player);

    public static class SpaceTile extends Tile {
        public SpaceTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, true, false, "SpaceTile");
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
        }
    }

    public static class WallTile extends Tile {
        public WallTile(int worldIndex, int xNum, int yNum, boolean isApproachable) {
            super(worldIndex, xNum, yNum, isApproachable, true, "WallTile");
        }

        @Override
        public void playerIsOn(Player player) {} // Impossible
    }

    public static class RiverTile extends Tile {
        public RiverTile(int worldIndex, int xNum, int yNum, boolean isApproachable) {
            super(worldIndex, xNum, yNum, isApproachable, true, "RiverTile");
        }

        @Override
        public void playerIsOn(Player player) {} // Impossible
    }

    public static class SlowTile extends Tile {

        public SlowTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, true, false, "SlowTile"); // Earthy brown
        }

        @Override
        public void playerIsOn(Player player) {
            player.slow();
        }
    }

    public static class SpecialTile extends Tile {

        public SpecialTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, true, false, "SpecialTile"); // Icy blue
        }

        @Override
        public void playerIsOn(Player player) {
            player.slip();
        }
    }

    public static class DamageTile extends Tile {
        public DamageTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, true, false, "DamageTile");
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
            player.damage(worldIndex);
        }

    }

    public static class HealTile extends Tile {
        public HealTile(int worldIndex, int xNum, int yNum) { // Golden yellow tile, draw a + in it
            super(worldIndex, xNum, yNum, true, false, "HealTile");
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
            player.heal(3.0 / worldIndex);
        }
    }
}

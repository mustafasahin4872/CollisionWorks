public abstract class Tile implements Passable, Drawable {
    public static final int HALF_SIDE = 25;

    public final int worldIndex;
    private final int xNum, yNum;
    private final double[] coordinates;
    private final double centerX, centerY;
    private final char symbol;
    private final boolean isSolid;
    private final String fileName;
    private static final String ROOT = "misc/tileImages/";
    private final boolean isApproachable; //false for tiles locked inside solid tiles

    // Main constructor
    public Tile(int xNum, int yNum, char symbol, boolean isApproachable, boolean isSolid, int worldIndex, String fileName) {
        this.xNum = xNum;
        this.yNum = yNum;
        coordinates = new double[]{
                (xNum - 1) * HALF_SIDE * 2, (Y_TILE - yNum) * HALF_SIDE * 2,
                xNum * HALF_SIDE * 2, (Y_TILE - yNum + 1) * HALF_SIDE * 2
        };
        centerX = (coordinates[0] + coordinates[2]) / 2;
        centerY = (coordinates[1] + coordinates[3]) / 2;
        this.symbol = symbol;
        this.isApproachable = isApproachable;
        this.isSolid = isSolid;
        this.worldIndex = worldIndex;
        this.fileName = fileName;
    }

    public char getSymbol() {
        return symbol;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public boolean isApproachable() {
        return isApproachable;
    }

    @Override
    public double[] getCoordinates() {
        return coordinates;
    }

    @Override
    public void draw() {
        StdDraw.picture(centerX, centerY, fileName, HALF_SIDE*2, HALF_SIDE*2);
    }

    public abstract void playerIsOn(Player player);

    public static class SpaceTile extends Tile {
        public SpaceTile(int xNum, int yNum, int worldIndex) {
            super(xNum, yNum, ' ', true, false, worldIndex, ROOT + "SpaceTile" + worldIndex + ".jpg");
        }

        public void playerIsOn(Player player) {
            player.resetDeceleration();
            player.resetAcceleration();
            player.resetMaxSpeed();
        }

    }

    public static class WallTile extends Tile {
        public WallTile(int xNum, int yNum, boolean isApproachable, int worldIndex) {
            super(xNum, yNum, 'W', isApproachable,true, worldIndex, ROOT + "WallTile" + worldIndex + ".jpg");
        }

        public void playerIsOn(Player player) {} //impossible

    }

    public static class RiverTile extends Tile {
        public RiverTile(int xNum, int yNum, boolean isApproachable, int worldIndex) {
            super(xNum, yNum, 'R', isApproachable,true, worldIndex, ROOT + "RiverTile" + worldIndex + ".jpg");
        }

        public void playerIsOn(Player player) {} //impossible

    }

    public static class MudTile extends Tile {
        public MudTile(int xNum, int yNum) {
            super(xNum, yNum, 'M', true,false, 1, ROOT + "MudTile" + ".jpg"); // earthy brown
        }

        public void playerIsOn(Player player) {
            player.slow();
        }

    }

    public static class IceTile extends Tile {
        public IceTile(int xNum, int yNum) {
            super(xNum, yNum, 'I', true, false,2, ROOT + "IceTile" + ".jpg"); // icy blue
        }

        public void playerIsOn(Player player) {
            player.slip();
        }

    }

    public static class DamageTile extends Tile {
        public DamageTile(int xNum, int yNum, int worldIndex) {
            super(xNum, yNum, 'D', true, false, worldIndex, ROOT + "DamageTile" + worldIndex + ".jpg");
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
            super(xNum, yNum, 'H', true, false, worldIndex, ROOT + "HealTile" + ".jpg");
        }

        public void playerIsOn(Player player) {
            player.resetDeceleration();
            player.resetAcceleration();
            player.resetMaxSpeed();
            player.heal(3.0/worldIndex);
        }

    }
}
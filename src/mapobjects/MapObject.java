package mapobjects;
import game.Player;
import helperobjects.Drawable;

public abstract class MapObject implements Drawable {

    protected final int worldIndex, xNum, yNum;
    protected double[] centerCoordinates, coordinates, collisionBox;
    public static final double TILE_SIDE = 50;

    public MapObject(int xNum, int yNum) {
        this(0, xNum, yNum);
    }

    public MapObject(int worldIndex, int xNum, int yNum) {
        this.worldIndex = worldIndex;
        this.xNum = xNum;
        this.yNum = yNum;
        centerCoordinates = new double[]{
                (xNum-0.5)*TILE_SIDE, (yNum-0.5)*TILE_SIDE
        };
        coordinates = new double[]{
                (xNum-1)*TILE_SIDE, (yNum-1)*TILE_SIDE,
                (xNum)*TILE_SIDE, (yNum)*TILE_SIDE
        };
        collisionBox = coordinates;
    }

    public abstract void playerIsOn(Player player);
    public abstract void draw();

    public double[] getCoordinates() {
        return coordinates;
    }

    public double[] getCollisionBox() {
        return collisionBox;
    }

    protected void set2x2CenterCoordinates() {
        centerCoordinates[0] = xNum*TILE_SIDE;
        centerCoordinates[1] = yNum*TILE_SIDE;
    }

    protected void set2x2Coordinates() {
        coordinates[0] = (xNum - 1) * TILE_SIDE;
        coordinates[2] = (xNum + 1) * TILE_SIDE;
        coordinates[1] = (yNum - 1) * TILE_SIDE;
        coordinates[3] = (yNum + 1) * TILE_SIDE;
    }

}

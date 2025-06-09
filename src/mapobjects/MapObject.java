package mapobjects;
import game.Player;
import lib.StdDraw;

import static helperobjects.CollisionMethods.playerIsIn;

public abstract class MapObject {

    protected final boolean cornerAligned;
    protected String fileName;
    protected final int worldIndex, xNum, yNum;
    protected final double width, height, halfWidth, halfHeight; //in tiles
    protected final double[] centerCoordinates, coordinates, collisionBox;
    public static final double HALF_SIDE = 25, TILE_SIDE = HALF_SIDE*2;

    public MapObject(int xNum, int yNum) {
        this(0, xNum, yNum, 1, 1, null, false);
    }

    public MapObject(int worldIndex, int xNum, int yNum) {
        this(worldIndex, xNum, yNum, 1, 1, null, false);
    }

    public MapObject(int xNum, int yNum, String fileName) {
        this(0, xNum, yNum, 1, 1, fileName, false);
    }

    public MapObject(int xNum, int yNum, double width, double height) {
        this(0, xNum, yNum, width, height, null, false);
    }

    public MapObject(int worldIndex, int xNum, int yNum, double width, double height) {
        this(worldIndex, xNum, yNum, width, height, null, false);
    }

    public MapObject(int xNum, int yNum, double width, double height, String fileName) {
        this(0, xNum, yNum, width, height, fileName, false);
    }

    public MapObject(int worldIndex, int xNum, int yNum, String fileName) {
        this(worldIndex, xNum, yNum, 1, 1, fileName, false);
    }



    public MapObject(int xNum, int yNum, boolean cornerAligned) {
        this(0, xNum, yNum, 1, 1, null, cornerAligned);
    }

    public MapObject(int worldIndex, int xNum, int yNum, boolean cornerAligned) {
        this(worldIndex, xNum, yNum, 1, 1, null, cornerAligned);
    }

    public MapObject(int xNum, int yNum, String fileName, boolean cornerAligned) {
        this(0, xNum, yNum, 1, 1, fileName, cornerAligned);
    }

    public MapObject(int xNum, int yNum, double width, double height, boolean cornerAligned) {
        this(0, xNum, yNum, width, height, null, cornerAligned);
    }

    public MapObject(int worldIndex, int xNum, int yNum, double width, double height, boolean cornerAligned) {
        this(worldIndex, xNum, yNum, width, height, null, cornerAligned);
    }

    public MapObject(int xNum, int yNum, double width, double height, String fileName, boolean cornerAligned) {
        this(0, xNum, yNum, width, height, fileName, cornerAligned);
    }

    public MapObject(int worldIndex, int xNum, int yNum, double width, double height, String fileName, boolean cornerAligned) {
        this.worldIndex = worldIndex;
        this.xNum = xNum;
        this.yNum = yNum;
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height/2;
        this.fileName = fileName;
        this.cornerAligned = cornerAligned;

        if (cornerAligned) {
            centerCoordinates = new double[]{(xNum-0.5+halfWidth)*TILE_SIDE, (yNum-0.5+halfHeight)*TILE_SIDE};
        } else {
            centerCoordinates = new double[]{(xNum-0.5)*TILE_SIDE, (yNum-0.5)*TILE_SIDE};
        }
        coordinates = new double[]{
                centerCoordinates[0]-halfWidth*TILE_SIDE, centerCoordinates[1]-halfHeight*TILE_SIDE,
                centerCoordinates[0]+halfWidth*TILE_SIDE,centerCoordinates[0]+halfHeight*TILE_SIDE,
                };

        collisionBox = new double[4];
        setCollisionBoxToCoordinates();
    }

    public void isPlayerOn(Player player) {
        if (playerIsIn(player, collisionBox)) {
            playerIsOn(player);
        }
    }

    public abstract void playerIsOn(Player player);

    public void draw() {
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], fileName, coordinates[2]-coordinates[0], coordinates[3]-coordinates[1]);
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public double[] getCollisionBox() {
        return collisionBox;
    }

    public double[] getCenterCoordinates() {
        return centerCoordinates;
    }

    protected void setCollisionBox(double[] collisionBox) {
        this.collisionBox[0] = collisionBox[0];
        this.collisionBox[1] = collisionBox[1];
        this.collisionBox[2] = collisionBox[2];
        this.collisionBox[3] = collisionBox[3];
    }

    protected void setCollisionBoxToCoordinates() {
        System.arraycopy(coordinates, 0, collisionBox, 0, 4);
    }

    //shifts coordinates, collisionBox, center
    protected void shiftPosition(double xShift, double yShift) {
        xShift(xShift);
        yShift(yShift);
    }

    protected void xShift(double xShift) {
        centerCoordinates[0] += xShift;
        coordinates[0] += xShift;
        coordinates[2] += xShift;
        collisionBox[0] += xShift;
        collisionBox[2] += xShift;
    }

    protected void yShift(double yShift) {
        centerCoordinates[1] += yShift;
        coordinates[1] += yShift;
        coordinates[3] += yShift;
        collisionBox[1] += yShift;
        collisionBox[3] += yShift;
    }
}

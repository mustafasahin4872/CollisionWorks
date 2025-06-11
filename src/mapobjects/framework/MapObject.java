package mapobjects.framework;
import game.Player;
import lib.StdDraw;

import static helperobjects.CollisionMethods.playerIsIn;

public abstract class MapObject extends Blueprint {

    protected static final char VERTICAL = '|', HORIZONTAL = '—',
            RIGHT = '>', LEFT = '<', UP = '^', DOWN = 'v';

    protected final boolean cornerAligned;
    //if cornerAligned, the object's coordinates box's upper left corner is fixed in the upper left corner of the initializing box
    protected String fileName; //null if object is not drawn with picture
    protected final double width, height, halfWidth, halfHeight; //in tiles
    protected final double[] centerCoordinates, coordinates, collisionBox; //in pixels
    public static final double HALF_SIDE = 25, TILE_SIDE = HALF_SIDE*2; //one tile's dimensions


    public MapObject(int worldIndex, int xNum, int yNum) {
        this(worldIndex, xNum, yNum, 1, 1, null, false);
    }

    public MapObject(int worldIndex, int xNum, int yNum, double width, double height) {
        this(worldIndex, xNum, yNum, width, height, null, false);
    }

    public MapObject(int worldIndex, int xNum, int yNum, String fileName) {
        this(worldIndex, xNum, yNum, 1, 1, fileName, false);
    }

    public MapObject(int worldIndex, int xNum, int yNum, boolean cornerAligned) {
        this(worldIndex, xNum, yNum, 1, 1, null, cornerAligned);
    }

    public MapObject(int worldIndex, int xNum, int yNum, double width, double height, boolean cornerAligned) {
        this(worldIndex, xNum, yNum, width, height, null, cornerAligned);
    }

    public MapObject(int worldIndex, int xNum, int yNum, double width, double height, String fileName) {
        this(worldIndex, xNum, yNum, width, height, fileName, false);
    }

    public MapObject(int worldIndex, int xNum, int yNum, double width, double height, String fileName, boolean cornerAligned) {
        super(worldIndex, xNum, yNum);
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height/2;
        this.fileName = fileName;
        this.cornerAligned = cornerAligned;

        if (cornerAligned) {
            centerCoordinates = new double[]{(xNum-1+halfWidth)*TILE_SIDE, (yNum-1+halfHeight)*TILE_SIDE};
        } else {
            centerCoordinates = new double[]{(xNum-0.5)*TILE_SIDE, (yNum-0.5)*TILE_SIDE};
        }
        coordinates = new double[]{
                centerCoordinates[0]-halfWidth*TILE_SIDE, centerCoordinates[1]-halfHeight*TILE_SIDE,
                centerCoordinates[0]+halfWidth*TILE_SIDE,centerCoordinates[1]+halfHeight*TILE_SIDE,
                };
        collisionBox = new double[4];
        setCollisionBoxToCoordinates();
    }

    public void checkPlayerIsOn(Player player) {
        if (playerIsIn(player, collisionBox)) {
            playerIsOn(player);
        }
    }

    public abstract void playerIsOn(Player player);

    public void draw() {
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], fileName, coordinates[2]-coordinates[0], coordinates[3]-coordinates[1]);
    }

    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double[] getCenterCoordinates() {
        return centerCoordinates;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public double[] getCollisionBox() {
        return collisionBox;
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

    //shift methods
    protected void shiftPosition(double deltaX, double deltaY) {
        xShiftPosition(deltaX);
        yShiftPosition(deltaY);
    }

    protected void xShiftPosition(double deltaX) {
        centerCoordinates[0] += deltaX;
        coordinates[0] += deltaX;
        coordinates[2] += deltaX;
        collisionBox[0] += deltaX;
        collisionBox[2] += deltaX;
    }

    protected void yShiftPosition(double deltaY) {
        centerCoordinates[1] += deltaY;
        coordinates[1] += deltaY;
        coordinates[3] += deltaY;
        collisionBox[1] += deltaY;
        collisionBox[3] += deltaY;
    }

}

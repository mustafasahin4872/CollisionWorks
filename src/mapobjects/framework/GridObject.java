package mapobjects.framework;
import game.Player;
import lib.StdDraw;

public abstract class GridObject extends MapObject {

    protected static final char ZERO = '0', VERTICAL = '|', HORIZONTAL = '—',
            RIGHT = '>', LEFT = '<', UP = '^', DOWN = 'v';


    protected final int worldIndex, xNum, yNum;
    protected String fileName; //null if object is not drawn with picture
    protected final boolean cornerAligned; //if cornerAligned,
    //the object's coordinates box's upper left corner is fixed in the upper left corner of the initializing box
    protected boolean expired;
    public static final double HALF_SIDE = 25, TILE_SIDE = HALF_SIDE*2; //one tile's dimensions


    public GridObject(int worldIndex, int xNum, int yNum) {
        this(worldIndex, xNum, yNum, 1, 1, null, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height) {
        this(worldIndex, xNum, yNum, width, height, null, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, String fileName) {
        this(worldIndex, xNum, yNum, 1, 1, fileName, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, boolean cornerAligned) {
        this(worldIndex, xNum, yNum, 1, 1, null, cornerAligned);
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height, boolean cornerAligned) {
        this(worldIndex, xNum, yNum, width, height, null, cornerAligned);
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height, String fileName) {
        this(worldIndex, xNum, yNum, width, height, fileName, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height, String fileName, boolean cornerAligned) {
        super((xNum-0.5)*TILE_SIDE, (yNum-0.5)*TILE_SIDE, width*TILE_SIDE, height*TILE_SIDE);
        this.worldIndex = worldIndex;
        this.xNum = xNum;
        this.yNum = yNum;
        this.fileName = fileName;
        this.cornerAligned = cornerAligned;

        if (cornerAligned) {
            setCenterCoordinates((xNum-1+width/2)*TILE_SIDE, (yNum-1+height/2)*TILE_SIDE);
        }
    }


    public int getWorldIndex() {
        return worldIndex;
    }

    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    protected void expire() {
        expired = true;
    }

    public boolean isExpired() {
        return expired;
    }


    public void draw() {
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], fileName, width, height);
    }

    public abstract void call(Player player);


}

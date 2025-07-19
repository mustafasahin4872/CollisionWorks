package mapobjects.category;

//the subtype of mapObject. its corners are always on the sides of tiles, so it fits the grid.
//height and width are now multiples of one tile side, and integers are used for width and height below this mapObject type.
//coordinates are indicated with integers as well, they are the locations on the grid, starting from (1, 1) in the top left corner

public abstract class GridObject extends MapObject {

    public static final double HALF_SIDE = 25, TILE_SIDE = HALF_SIDE*2; //one tile's dimensions

    protected final int xNum, yNum;
    protected final boolean cornerAligned;
    // if cornerAligned, the object's coordinates' upper left corner is fixed in the upper left corner of the initializing box

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
        super(worldIndex, (xNum-0.5)*TILE_SIDE, (yNum-0.5)*TILE_SIDE, width*TILE_SIDE, height*TILE_SIDE, fileName);
        this.xNum = xNum;
        this.yNum = yNum;
        this.cornerAligned = cornerAligned;

        if (cornerAligned) {
            setCenterCoordinates((xNum-1+width/2)*TILE_SIDE, (yNum-1+height/2)*TILE_SIDE);
        }
    }

    public int getXNum() {
        return xNum;
    }

    public int getYNum() {
        return yNum;
    }

    public boolean isCornerAligned() {
        return cornerAligned;
    }

    public boolean isSolid() {
        return this instanceof Collidable;
    }

}

package mapobjects.traits.schemas;

//the subtype of mapObject. its corners are always on the sides of tiles, so it fits the grid.
//height and width are now multiples of one tile side, and integers are used for width and height below this mapObject type.
//coordinates are indicated with integers as well, they are the locations on the grid, starting from (1, 1) in the top left corner

import mapobjects.traits.Collidable;

public abstract class GridObject extends MapObject {

    public static final double HALF_SIDE = 25, TILE_SIDE = HALF_SIDE*2; //one tile's dimensions

    protected int xNum, yNum;
    protected final boolean cornerAligned;
    // if cornerAligned, the object's coordinates' upper left corner is fixed in the upper left corner of the initializing box

    public GridObject(int worldIndex, int xNum, int yNum) {
        this(worldIndex, xNum, yNum, 1, 1, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height) {
        this(worldIndex, xNum, yNum, width, height, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, String name) {
        this(worldIndex, xNum, yNum, 1, 1, name, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, String name, String imageType) {
        this(worldIndex, xNum, yNum, 1, 1, name, imageType, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, boolean cornerAligned) {
        this(worldIndex, xNum, yNum, 1, 1, cornerAligned);
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height, String name) {
        this(worldIndex, xNum, yNum, width, height, name, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height, String name, String imageType) {
        this(worldIndex, xNum, yNum, width, height, name, imageType, false);
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height, boolean cornerAligned) {
        super(worldIndex, (xNum-0.5)*TILE_SIDE, (yNum-0.5)*TILE_SIDE, width*TILE_SIDE, height*TILE_SIDE);
        this.xNum = xNum;
        this.yNum = yNum;
        this.cornerAligned = cornerAligned;

        if (cornerAligned) {
            setCenterCoordinates((xNum-1+width/2)*TILE_SIDE, (yNum-1+height/2)*TILE_SIDE);
        }
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height, String name, boolean cornerAligned) {
        super(worldIndex, (xNum-0.5)*TILE_SIDE, (yNum-0.5)*TILE_SIDE, width*TILE_SIDE, height*TILE_SIDE);
        this.xNum = xNum;
        this.yNum = yNum;
        this.cornerAligned = cornerAligned;

        if (cornerAligned) {
            setCenterCoordinates((xNum-1+width/2)*TILE_SIDE, (yNum-1+height/2)*TILE_SIDE);
        }
    }

    public GridObject(int worldIndex, int xNum, int yNum, double width, double height, String name, String imageType, boolean cornerAligned) {
        super(worldIndex, (xNum-0.5)*TILE_SIDE, (yNum-0.5)*TILE_SIDE, width*TILE_SIDE, height*TILE_SIDE);
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

    public void setXNum(int xNum) {
        this.xNum = xNum;
    }

    public void setYNum(int yNum) {
        this.yNum = yNum;
    }

    public void setGridNumbers(int[] gridNumbers) {
        setXNum(gridNumbers[0]);
        setYNum(gridNumbers[1]);
    }

    public boolean isCornerAligned() {
        return cornerAligned;
    }

    public boolean isSolid() {
        return this instanceof Collidable;
    }

}

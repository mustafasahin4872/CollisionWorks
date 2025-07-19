package mapobjects.category;

//the most general map object class, holds attributes of rectangular map object

import game.Player;
import lib.StdDraw;
import mapobjects.component.Box;

public abstract class MapObject {

    //the character codes for map objects' attributes
    protected static final char ZERO = '0', VERTICAL = '|', HORIZONTAL = '—',
            RIGHT = '>', LEFT = '<', UP = '^', DOWN = 'v';

    protected final int worldIndex; //the world the object is in
    protected String fileName; //the image file's name, is null if no image is needed
    protected boolean expired; //if object is no longer needed, it is marked
    protected final Box positionBox; //object's position and dimensions are stored in the box


    //empty map object, the attributes are set up manually for this.
    public MapObject() {
        this(0, 0, 0, 0, 0);
    }

    public MapObject(int worldIndex, double x, double y, double width, double height) {
        this(worldIndex, x, y, width, height, null);
    }

    public MapObject(int worldIndex, double x, double y, double width, double height, String fileName) {
        this.worldIndex = worldIndex;
        this.fileName = fileName;
        positionBox = new Box(x, y, width, height);
    }


    //all map objects are called every iteration if they are not expired
    //each map object has their unique actions, when called, they perform them
    public void call(Player player) {}


    public int getWorldIndex() {
        return worldIndex;
    }


    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void draw() {
        StdDraw.picture(getX(), getY(), fileName, getWidth(), getHeight());
    }


    public void expire() {
        expired = true;
    }

    public boolean isExpired() {
        return expired;
    }


    public double getWidth() {
        return positionBox.getWidth();
    }

    public double getHeight() {
        return positionBox.getHeight();
    }

    public void setWidth(double width) {
        positionBox.setWidth(width);
    }

    public void setHeight(double height) {
        positionBox.setHeight(height);
    }


    public double[] getCenterCoordinates() {
        return positionBox.getCenterCoordinates();
    }

    public double getX() {
        return positionBox.getCenterX();
    }

    public double getY() {
        return positionBox.getCenterY();
    }

    public void setCenterCoordinates(double x, double y) {
        positionBox.setCenterCoordinates(x, y);
    }

    public void setX(double x) {
        positionBox.setCenterX(x);
    }

    public void setY(double y) {
        positionBox.setCenterY(y);
    }

    //returns the xNum and yNum of a map object's center in the grid.
    public int[] getGridNumbers() {
        int[] gridNumbers = new int[2];
        gridNumbers[0] = (int) (getX() / GridObject.TILE_SIDE) + 1;
        gridNumbers[1] = (int) (getY() / GridObject.TILE_SIDE) + 1;
        return gridNumbers;
    }


    //shift methods
    protected void shiftPosition(double deltaX, double deltaY) {
        xShiftPosition(deltaX);
        yShiftPosition(deltaY);
    }

    protected void xShiftPosition(double deltaX) {
        positionBox.xShift(deltaX);
    }

    protected void yShiftPosition(double deltaY) {
        positionBox.yShift(deltaY);
    }

}

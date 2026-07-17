package mapobjects.traits.schemas;

import mapobjects.components.Box;
import mapobjects.traits.collisions.HasBody;

//the most general map object class, holds attributes of a rectangular map object
public abstract class MapObject implements HasBody {

    protected int worldIndex; //the world the object is in

    protected boolean expired; //if object is no longer needed, it is marked
    protected final Box positionBox; //object's position and dimensions are stored in the box

    //empty map object, the attributes are set up manually for this.
    public MapObject() {
        this(0, 0, 0, 0, 0);
    }

    public MapObject(int worldIndex, double x, double y, double width, double height) {
        this.worldIndex = worldIndex;
        positionBox = new Box(x, y, width, height);
    }

    //all map objects are called every iteration if they are not expired
    //each map object has their unique actions, when called, they perform them
    public void call() {}


    public int getWorldIndex() {
        return worldIndex;
    }

    public void setWorldIndex(int worldIndex) {
        this.worldIndex = worldIndex;
    }


    public void expire() {
        expired = true;
    }

    public boolean isExpired() {
        return expired;
    }

    public void resize(double multiplier) {
        setWidth(getWidth()*multiplier);
        setHeight(getHeight()*multiplier);
    }


    public double[] getCenterCoordinates() {
        return positionBox.getCenterCoordinates();
    }

    public Box getPositionBox() {
        return positionBox;
    }

    public void setCenterCoordinates(double x, double y) {
        positionBox.setCenterCoordinates(x, y);
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

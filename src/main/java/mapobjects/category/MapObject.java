package mapobjects.category;

//the most general map object class, holds attributes of a rectangular map object
//the format of each rectangular region coordinate is {x0, y0, x1, y1} unless stated otherwise.
//(x0, y0) and (x1, y1) are the rectangle's bottom left and top right corners.

import game.Main;
import mapobjects.mapobject.Player;
import lib.StdDraw;
import mapobjects.component.Box;

public abstract class MapObject {

    //the char codes for map objects' attributes
    protected static final char ZERO = '0', VERTICAL = '|', HORIZONTAL = '—',
            RIGHT = '>', LEFT = '<', UP = '^', DOWN = 'v';

    protected int worldIndex; //the world the object is in

    protected String imageFileName, name, directory, imageType;
    protected boolean expired; //if object is no longer needed, it is marked
    protected final Box positionBox; //object's position and dimensions are stored in the box


    //empty map object, the attributes are set up manually for this.
    public MapObject() {
        this(0, 0, 0, 0, 0);
    }

    public MapObject(int worldIndex, double x, double y, double width, double height) {
        this(worldIndex, x, y, width, height, "0");
    }

    public MapObject(int worldIndex, double x, double y, double width, double height, String name) {
        this(worldIndex, x, y, width, height, name, "png");
    }

    public MapObject(int worldIndex, double x, double y, double width, double height, String name, String imageType) {
        this.worldIndex = worldIndex;
        this.name = name;
        this.imageType = imageType;
        directory = getDirectory();
        setName(name);

        positionBox = new Box(x, y, width, height);
    }

    private String getDirectory() {
        String fullName = this.getClass().getName();  // e.g. mapobjects.mapobject.Chest.WoodenChest
        // Remove the package prefix first
        String prefix = "mapobjects.mapobject.";
        if (!fullName.startsWith(prefix)) {
            throw new IllegalStateException("Unexpected package: " + fullName);
        }
        String remainder = fullName.substring(prefix.length()).toLowerCase();  // e.g. "Chest.WoodenChest"
        String directory;
        if (remainder.contains("$")) {
            String[] parts = remainder.split("\\$"); // split on dot
            directory = parts[0] + "/" + parts[1] + "/";
        } else {
            // No subclass, just classname only
            directory = remainder + "/";
        }
        return directory;
    }


    //all map objects are called every iteration if they are not expired
    //each map object has their unique actions, when called, they perform them
    public void call(Player player) {}


    public int getWorldIndex() {
        return worldIndex;
    }

    public void setWorldIndex(int worldIndex) {
        this.worldIndex = worldIndex;
    }

    protected void setName(String name) {
        this.name = name;
        imageFileName = Main.IMAGES_ROOT + directory + name + "." + imageType;
    }

    public void draw() {
        StdDraw.picture(getX(), getY(), imageFileName, getWidth(), getHeight());
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

    public void resize(double multiplier) {
        setWidth(getWidth()*multiplier);
        setHeight(getHeight()*multiplier);
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

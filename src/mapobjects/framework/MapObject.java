package mapobjects.framework;

public abstract class MapObject {

    protected final double[] centerCoordinates = new double[2], coordinates = new double[4];
    protected double width, height, halfWidth, halfHeight;

    //for setting everything up later
    public MapObject() {
        this(0, 0, 0, 0);
    }

    public MapObject(double x, double y, double width, double height) {
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height/2;
        setCenterCoordinates(x, y);
        updateCoordinates();
    }

    public double getX() {
        return centerCoordinates[0];
    }

    public double getY() {
        return centerCoordinates[1];
    }

    public void setX(double x) {
        centerCoordinates[0] = x;
        updateCoordinates();
    }

    public void setY(double y) {
        centerCoordinates[1] = y;
        updateCoordinates();
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        halfWidth = width/2;
        updateCoordinates();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        halfHeight = height/2;
        updateCoordinates();
    }

    public double[] getCenterCoordinates() {
        return centerCoordinates;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCenterCoordinates(double x, double y) {
        centerCoordinates[0] = x;
        centerCoordinates[1] = y;
        updateCoordinates();
    }

    public void updateCoordinates() {
        coordinates[0] = centerCoordinates[0] - halfWidth;
        coordinates[1] = centerCoordinates[1] - halfHeight;
        coordinates[2] = centerCoordinates[0] + halfWidth;
        coordinates[3] = centerCoordinates[1] + halfHeight;
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
    }

    protected void yShiftPosition(double deltaY) {
        centerCoordinates[1] += deltaY;
        coordinates[1] += deltaY;
        coordinates[3] += deltaY;
    }

}

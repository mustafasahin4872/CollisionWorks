package mapobjects.framework;

//represents a rectangle object, stores coordinates of center and corners along with values of width, height
public class Box implements Cloneable {

    private final double[] centerCoordinates, corners = new double[4];
    private double width, height, halfWidth, halfHeight;


    public Box(double[] centerCoordinates, double width, double height) {
        this.centerCoordinates = centerCoordinates;
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height/2;
        updateCorners();
    }

    public Box(double x, double y, double width, double height) {
        this.centerCoordinates = new double[]{x, y};
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height/2;
        updateCorners();
    }


    private void updateCorners() {
        corners[0] = centerCoordinates[0] - halfWidth;
        corners[1] = centerCoordinates[1] - halfHeight;
        corners[2] = centerCoordinates[0] + halfWidth;
        corners[3] = centerCoordinates[1] + halfHeight;
    }


    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setWidth(double width) {
        this.width = width;
        halfWidth = width/2;
        updateCorners();
    }

    public void setHeight(double height) {
        this.height = height;
        halfHeight = height/2;
        updateCorners();
    }


    public double[] getCenterCoordinates() {
        return centerCoordinates;
    }

    public void setCenterCoordinates(double[] centerCoordinates) {
        this.centerCoordinates[0] = centerCoordinates[0];
        this.centerCoordinates[1] = centerCoordinates[1];
        updateCorners();
    }

    public void setCenterCoordinates(double x, double y) {
        centerCoordinates[0] = x;
        centerCoordinates[1] = y;
        updateCorners();
    }

    public double getCenterX() {
        return centerCoordinates[0];
    }

    public double getCenterY() {
        return centerCoordinates[1];
    }

    public void setCenterX(double x) {
        centerCoordinates[0] = x;
        updateCorners();
    }

    public void setCenterY(double y) {
        centerCoordinates[1] = y;
        updateCorners();
    }


    public double[] getCorners() {
        return corners;
    }

    public double getCorner(int index) {
        return corners[index];
    }

    public void setCorners(double[] corners) {
        this.corners[0] = corners[0];
        this.corners[1] = corners[1];
        this.corners[2] = corners[2];
        this.corners[3] = corners[3];
    }

    public void setCorner(int index, double value) {
        corners[index] = value;
    }


    public void xShift(double deltaX) {
        centerCoordinates[0] += deltaX;
        updateCorners();
    }

    public void yShift(double deltaY) {
        centerCoordinates[1] += deltaY;
        updateCorners();
    }


    @Override
    public Box clone() {
        return new Box(centerCoordinates[0], centerCoordinates[1], width, height);
    }
}

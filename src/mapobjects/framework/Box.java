package mapobjects.framework;

import static mapobjects.framework.MapObject.TILE_SIDE;

public class Box {

    protected final double[] centerCoordinates, box = new double[4];
    private final double width, height, halfWidth, halfHeight;

    public Box(MapObject mapObject) {
        this(mapObject.centerCoordinates, mapObject.width*TILE_SIDE, mapObject.height*TILE_SIDE);
    }

    public Box(double[] centerCoordinates, double width, double height) {
        this.centerCoordinates = centerCoordinates;
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height/2;
        updateBox();
    }

    public double[] getBox() {
        return box;
    }

    public double getBoxIndex(int index) {
        return box[index];
    }

    public void setBox(double[] box) {
        this.box[0] = box[0];
        this.box[1] = box[1];
        this.box[2] = box[2];
        this.box[3] = box[3];
    }

    public void updateBox() {
        box[0] = centerCoordinates[0] - halfWidth;
        box[1] = centerCoordinates[1] - halfHeight;
        box[2] = centerCoordinates[0] + halfWidth;
        box[3] = centerCoordinates[1] + halfHeight;
    }

    public void setBoxIndex(int index, double value) {
        box[index] = value;
    }

    public void xShiftBox(double deltaX) {
        box[0] += deltaX;
        box[2] += deltaX;
    }

    public void yShiftBox(double deltaY) {
        box[1] += deltaY;
        box[3] += deltaY;
    }

}

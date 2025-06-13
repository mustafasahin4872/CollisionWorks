package mapobjects.framework;

import static mapobjects.framework.MapObject.TILE_SIDE;

//contains a rangeBox
public class RangeBox {

    protected final double range;
    protected final double[] rangeBox = new double[4];

    public RangeBox(MapObject mapObject, double range) {
        this.range = range;
        double[] coordinates = mapObject.getCoordinates();
        rangeBox[0] = coordinates[0] - range*TILE_SIDE;
        rangeBox[2] = coordinates[2] + range*TILE_SIDE;
        rangeBox[1] = coordinates[1] - range*TILE_SIDE;
        rangeBox[3] = coordinates[3] + range*TILE_SIDE;
    }


    public double[] getRangeBox() {
        return rangeBox;
    }


    public void xShiftRangeBox(double deltaX) {
        rangeBox[0] += deltaX;
        rangeBox[2] += deltaX;
    }

    public void yShiftRangeBox(double deltaY) {
        rangeBox[1] += deltaY;
        rangeBox[3] += deltaY;
    }

}

package mapobjects.framework;

import static mapobjects.framework.MapObject.TILE_SIDE;

public class EffectBox {

    final MapObject mapObject;
    final double[] effectBox = new double[4];
    double width, height, halfWidth, halfHeight;

    public EffectBox(MapObject mapObject) {
        this(mapObject, TILE_SIDE, TILE_SIDE);
    }

    public EffectBox(MapObject mapObject, double width, double height) {
        this.mapObject = mapObject;
        halfWidth = width/2;
        halfHeight = height/2;
        updateEffectBox();
    }

    public double[] getEffectBox() {
        return effectBox;
    }

    public void setEffectBox(double[] effectBox) {
        this.effectBox[0] = effectBox[0];
        this.effectBox[1] = effectBox[1];
        this.effectBox[2] = effectBox[2];
        this.effectBox[3] = effectBox[3];
    }

    public void updateEffectBox() {
        effectBox[0] = mapObject.centerCoordinates[0] - halfWidth;
        effectBox[1] = mapObject.centerCoordinates[1] - halfHeight;
        effectBox[2] = mapObject.centerCoordinates[0] + halfWidth;
        effectBox[3] = mapObject.centerCoordinates[1] + halfHeight;
    }

    protected void setEffectBoxToCoordinates() {
        System.arraycopy(mapObject.coordinates, 0, effectBox, 0, 4);
    }

}

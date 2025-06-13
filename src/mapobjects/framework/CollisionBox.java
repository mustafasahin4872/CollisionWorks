package mapobjects.framework;

import static mapobjects.framework.MapObject.TILE_SIDE;

public class CollisionBox {

    private final MapObject mapObject;
    private final double width, height, halfWidth, halfHeight;
    private final double[] collisionBox = new double[4];

    public CollisionBox(MapObject mapObject) {
        this(mapObject, mapObject.width*TILE_SIDE, mapObject.height*TILE_SIDE);
    }

    public CollisionBox(MapObject mapObject, double width, double height) {
        this.mapObject = mapObject;
        this.width = width*TILE_SIDE;
        this.height = height*TILE_SIDE;
        halfWidth = width/2;
        halfHeight = height/2;
        updateCollisionBox();
    }

    public double[] getCollisionBox() {
        return collisionBox;
    }

    public void setCollisionBox(double[] collisionBox) {
        this.collisionBox[0] = collisionBox[0];
        this.collisionBox[1] = collisionBox[1];
        this.collisionBox[2] = collisionBox[2];
        this.collisionBox[3] = collisionBox[3];
    }

    public double getCollisionIndex(int index) {
        return collisionBox[index];
    }

    public void setCollisionIndex(int index, double value) {
        collisionBox[index] = value;
    }

    public void updateCollisionBox() {
        collisionBox[0] = mapObject.centerCoordinates[0] - halfWidth;
        collisionBox[1] = mapObject.centerCoordinates[1] - halfHeight;
        collisionBox[2] = mapObject.centerCoordinates[0] + halfWidth;
        collisionBox[3] = mapObject.centerCoordinates[1] + halfHeight;
    }

    protected void setCollisionBoxToCoordinates() {
        System.arraycopy(mapObject.coordinates, 0, collisionBox, 0, 4);
    }


}

package mapobjects;

import game.Player;

public interface MapEntity {

    void playerIsOn(Player player);

    double[] getCenterCoordinates();
    double[] getCoordinates();
    double[] getCollisionBox();

    void setFileName(String fileName);

    void setCollisionBox(double[] collisionBox);

    default void setCollisionBoxToCoordinates() {
        System.arraycopy(getCoordinates(), 0, getCollisionBox(), 0, 4);
    }


    //shifts coordinates, collisionBox, center
    default void shiftPosition(double xShift, double yShift) {
        xShift(xShift);
        yShift(yShift);
    }

    void xShift(double xShift);
    void yShift(double yShift);

}



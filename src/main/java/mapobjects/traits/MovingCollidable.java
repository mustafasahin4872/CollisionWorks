package mapobjects.traits;

import mapobjects.components.Box;

public interface MovingCollidable extends Moving, Collidable{

    boolean isXCollided();
    boolean isYCollided();
    void xCollide();
    void yCollide();

    default Box getNextXBox() {
        Box collisionBox = getCollisionBox();
        return new Box(getNextXCenterCoordinates(), collisionBox.getWidth(), collisionBox.getHeight());
    }

    default Box getNextYBox() {
        Box collisionBox = getCollisionBox();
        return new Box(getNextYCenterCoordinates(), collisionBox.getWidth(), collisionBox.getHeight());
    }

    //need to update collision box as well
    @Override
    void setX(double x);
    @Override
    void setY(double y);

}

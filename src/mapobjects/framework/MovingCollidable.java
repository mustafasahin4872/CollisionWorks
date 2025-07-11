package mapobjects.framework;

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

}

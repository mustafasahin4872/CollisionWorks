package mapobjects.framework;

public interface MovingCollidable extends Moving, Collidable {

    boolean isXCollided();
    boolean isYCollided();
    void xCollide();
    void yCollide();

    default Box getNextXBox(MovingCollidable movingCollidable) {
        return new Box(movingCollidable.getNextXCenterCoordinates(), movingCollidable.getWidth(), movingCollidable.getHeight());
    }

    default Box getNextYBox(MovingCollidable movingCollidable) {
        return new Box(movingCollidable.getNextYCenterCoordinates(), movingCollidable.getWidth(), movingCollidable.getHeight());
    }

}

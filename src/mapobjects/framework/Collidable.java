package mapobjects.framework;

import static helperobjects.CollisionMethods.intersects;

public interface Collidable {

    Box getCollisionBox();


    //checks collisions with moving collidables and sets their movement values
    default void checkCollision(MovingCollidable movingCollidable) {
        boolean xCollided = movingCollidable.isXCollided(), yCollided = movingCollidable.isYCollided();

        if (xCollided && yCollided) return;

        Box collidedBox = getCollisionBox(), movingBox = movingCollidable.getCollisionBox(),
                nextXBox = movingCollidable.getNextXBox(), nextYBox = movingCollidable.getNextYBox();

        if (!xCollided && intersects(nextXBox, collidedBox)) {
            double xVelocity = movingCollidable.getXVelocity();
            movingCollidable.xCollide();
            if (xVelocity>0) {
                movingCollidable.setX(collidedBox.getCorner(0) - movingBox.getWidth() / 2);
            } else if (xVelocity<0) {
                movingCollidable.setX(collidedBox.getCorner(2) + movingBox.getWidth() / 2);
            }
        }

        if (!yCollided && intersects(nextYBox, collidedBox)) {
            double yVelocity = movingCollidable.getYVelocity();
            movingCollidable.yCollide();
            if (yVelocity>0) {
                movingCollidable.setY(collidedBox.getCorner(1) - movingBox.getHeight() / 2);
            } else if (yVelocity < 0) {
                movingCollidable.setY(collidedBox.getCorner(3) + movingBox.getHeight() / 2);
            }
        }
    }


}
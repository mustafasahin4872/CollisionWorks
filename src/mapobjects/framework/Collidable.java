package mapobjects.framework;

import game.Player;

import static helperobjects.CollisionMethods.checkMovingCollidableLineCollision;

public interface Collidable {

    default double getWidth() {
        return getCollisionCoordinates()[2]- getCollisionCoordinates()[0];
    }
    default double getHeight() {
        return getCollisionCoordinates()[3] - getCollisionCoordinates()[1];
    }

    Box getCollisionBox();

    default double[] getCollisionCoordinates() {
        return getCollisionBox().getBox();
    }

    default void checkCollision(Player player) {
        checkMovingCollidableLineCollision(player, getCollisionCoordinates());
    }

}
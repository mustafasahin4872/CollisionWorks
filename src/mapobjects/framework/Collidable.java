package mapobjects.framework;

import game.Player;

import static helperobjects.CollisionMethods.checkPlayerLineCollision;

public interface Collidable {

    double[] getCollisionBox();

    default void checkCollision(Player player) {
        checkPlayerLineCollision(player, getCollisionBox());
    }

}
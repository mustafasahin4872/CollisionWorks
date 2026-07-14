package mapobjects.traits;

import mapobjects.entities.Player;
import mapobjects.components.Box;

import static helpers.CollisionMethods.intersects;
import static helpers.CollisionMethods.isIn;

//casts custom effects to Player objects
public interface OnEffector {

    Box getTriggerBox();

    void playerIsOn(Player player);

    default void checkPlayerIsOn(Player player) {
        if (isCornerOn(player)) playerIsOn(player);
    }

    default boolean isCornerOn(Moving moving) {
        return intersects(moving.getPositionBox(), getTriggerBox());
    }

    default boolean isCenterOn(Moving moving) {
        return isIn(moving.getX(), moving.getY(), getTriggerBox());
    }

}

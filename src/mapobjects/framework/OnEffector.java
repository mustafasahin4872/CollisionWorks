package mapobjects.framework;

import game.Player;

import static helperobjects.CollisionMethods.*;

//casts custom effects to Player objects
public interface OnEffector {

    Box getEffectBox();

    void checkPlayerIsOn(Player player);

    default void checkPlayerCornerIsOn(Player player) {
        if (intersects(player.getCollisionBox(), getEffectBox())) {
            playerIsOn(player);
        }
    }

    default void checkPlayerCenterIsOn(Player player) {
        if (isIn(player.getX(), player.getY(), getEffectBox())) {
            playerIsOn(player);
        }
    }

    void playerIsOn(Player player);

}

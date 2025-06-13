package mapobjects.framework;

import game.Player;

import static helperobjects.CollisionMethods.playerCenterIsIn;
import static helperobjects.CollisionMethods.playerIsIn;

public interface Effector {

    double[] getEffectBox();

    default void checkPlayerIsOn(Player player) {
        if (playerIsIn(player, getEffectBox())) {
            playerIsOn(player);
        }
    }

    default void checkPlayerCenterIsOn(Player player) {
        if (playerCenterIsIn(player, getEffectBox())) {
            playerIsOn(player);
        }
    }

    void playerIsOn(Player player);

}

package mapobjects.framework;

import game.Player;

import static helperobjects.CollisionMethods.playerIsIn;

public interface Ranged {

    double[] getRangeBox();

    void playerInRange(Player player);

    default void checkPlayerInRange(Player player) {
        if (playerIsIn(player, getRangeBox())) {
            playerInRange(player);
        }
    }
}

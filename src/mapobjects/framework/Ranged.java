package mapobjects.framework;

import game.Player;

import static helperobjects.CollisionMethods.intersects;

public interface Ranged {

    Box getRangeBox();

    default void checkPlayerInRange(Player player) {
        if (intersects(player.getCollisionBox(), getRangeBox())) {
            playerInRange(player);
        }
    }

    void playerInRange(Player player);

}

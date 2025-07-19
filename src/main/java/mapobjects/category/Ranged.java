package mapobjects.category;

import mapobjects.mapobject.Player;
import mapobjects.component.Box;

import static helpers.CollisionMethods.intersects;

public interface Ranged {

    Box getRangeBox();

    default void checkPlayerInRange(Player player) {
        if (intersects(player.getCollisionBox(), getRangeBox())) {
            playerInRange(player);
        }
    }

    void playerInRange(Player player);

}

package mapobjects.traits.triggerables;

import mapobjects.components.Trigger;
import mapobjects.entities.Player;

import java.util.Set;

// gets triggered only if a player passes on
public interface PlayerOnTriggerable extends Triggerable {

    Trigger<Player> getPlayerOnTrigger();

    default void setPlayerOnTriggerers(Set<Player> triggerers) {
        getPlayerOnTrigger().setTriggerers(triggerers);
    }
}

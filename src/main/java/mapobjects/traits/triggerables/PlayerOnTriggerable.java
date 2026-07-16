package mapobjects.traits.triggerables;

import mapobjects.components.Trigger;
import mapobjects.entities.Player;

import java.util.Set;

public interface PlayerOnTriggerable extends Triggerable {
    Trigger<Player> getPlayerOnTrigger();

    default void setPlayerOnTriggerers(Set<Player> triggerers) {
        getPlayerOnTrigger().setTriggerers(triggerers);
    }
}

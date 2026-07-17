package mapobjects.traits.triggerables;

import mapobjects.components.Trigger;
import mapobjects.entities.Player;

public interface PlayerOnTriggerable extends Triggerable {

    Trigger<Player> getPlayerOnTrigger();

}

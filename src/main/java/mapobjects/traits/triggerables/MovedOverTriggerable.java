package mapobjects.traits.triggerables;

import mapobjects.components.Trigger;
import mapobjects.traits.collisions.Movable;

public interface MovedOverTriggerable extends Triggerable {

    Trigger<Movable> getMovedOverTrigger();

}

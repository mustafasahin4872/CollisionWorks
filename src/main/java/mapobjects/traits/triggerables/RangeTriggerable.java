package mapobjects.traits.triggerables;

import mapobjects.components.Trigger;
import mapobjects.traits.collisions.Movable;

public interface RangeTriggerable extends Triggerable {

    Trigger<Movable> getRangeTrigger();

}

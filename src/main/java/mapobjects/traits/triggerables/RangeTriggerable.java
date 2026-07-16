package mapobjects.traits.triggerables;

import mapobjects.components.Trigger;
import mapobjects.traits.collisions.Movable;

import java.util.Set;

public interface RangeTriggerable extends Triggerable {

    Trigger<Movable> getRangeTrigger();

    default void setRangeTriggerers(Set<Movable> triggerers) {
        getRangeTrigger().setTriggerers(triggerers);
    }

}

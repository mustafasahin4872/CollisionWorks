package mapobjects.traits.triggerables;

import mapobjects.components.Trigger;
import mapobjects.traits.collisions.Movable;

import java.util.Set;

public interface MovedOverTriggerable extends Triggerable {

    Trigger<Movable> getMovedOverTrigger();

    default void setMovedOverTriggerers(Set<Movable> triggerers) {
        getMovedOverTrigger().setTriggerers(triggerers);
    }

}

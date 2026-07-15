package mapobjects.traits.triggerables;

import mapobjects.components.Box;
import mapobjects.traits.Moving;

import static helpers.CollisionMethods.intersects;

public interface Ranged extends Triggerable<Moving> {

    Box getRangeBox();

    @Override
    default boolean triggered(Moving moving) {
        return intersects(moving.getPositionBox(), getRangeBox());
    }

}

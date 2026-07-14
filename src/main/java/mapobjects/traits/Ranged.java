package mapobjects.traits;

import mapobjects.components.Box;

import static helpers.CollisionMethods.intersects;

public interface Ranged extends Triggerable<Moving> {

    Box getRangeBox();

    @Override
    default boolean triggered(Moving moving) {
        return intersects(moving.getPositionBox(), getRangeBox());
    }

    void action(Moving moving);

}

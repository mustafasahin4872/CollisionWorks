package mapobjects.traits;

import mapobjects.components.Box;

import static helpers.CollisionMethods.intersects;

// gets triggered when Moving entities pass over
public interface OnTriggerable extends Triggerable<Moving> {

    @Override
    default boolean triggered(Moving moving) {
        return intersects(moving.getPositionBox(), getPositionBox());
    }

    Box getPositionBox();

}

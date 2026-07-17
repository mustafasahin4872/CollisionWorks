package mapobjects.traits.collisions;

/// simulates an object that has a body and its position can be changed
public interface Movable extends HasBody {

    default void setX(double x) {
        getPositionBox().setCenterX(x);
    }
    default void setY(double y) {
        getPositionBox().setCenterY(y);
    }

    // if an external object is applying force, weight is considered.
    default double getWeight() {return 100;}

}

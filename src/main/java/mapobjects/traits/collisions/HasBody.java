package mapobjects.traits.collisions;

import mapobjects.components.Box;

public interface HasBody {

    Box getPositionBox();
    default double getX() {return getPositionBox().getCenterX();}
    default double getY() {return getPositionBox().getCenterY();}

}

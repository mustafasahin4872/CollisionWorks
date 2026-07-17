package mapobjects.traits.collisions;

import mapobjects.components.Box;

public interface HasBody {

    Box getPositionBox();
    default double getX() {return getPositionBox().getCenterX();}
    default double getY() {return getPositionBox().getCenterY();}

    default double getWidth() {return getPositionBox().getWidth();}
    default double getHeight() {return getPositionBox().getHeight();}

    default void setWidth(double width) {
        getPositionBox().setWidth(width);
    }
    default void setHeight(double height) {
        getPositionBox().setHeight(height);
    }

    default int[] getCoveredTileIndexes() {
        return getPositionBox().getCoveredTileIndexes();
    }

}

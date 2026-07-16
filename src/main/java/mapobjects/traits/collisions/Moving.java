package mapobjects.traits.collisions;

import game.io.Frame;

public interface Moving extends HasBody, Movable {

    double getXVelocity();
    double getYVelocity();

    default double getNextX() {
        return getX() + getXVelocity() * Frame.DT;
    }
    default double getNextY() {
        return getY() + getYVelocity() * Frame.DT;
    }

    default double[] getNextXCenterCoordinates() {
        return new double[]{getNextX(), getY()};
    }

    default double[] getNextYCenterCoordinates() {
        return new double[]{getX(), getNextY()};
    }

    default double[] getNextCenterCoordinates() {
        return new double[]{getNextX(), getNextY()};
    }

}

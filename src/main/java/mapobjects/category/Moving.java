package mapobjects.category;

import game.Frame;

public interface Moving {

    double getX();
    double getY();
    void setX(double x);
    void setY(double y);
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

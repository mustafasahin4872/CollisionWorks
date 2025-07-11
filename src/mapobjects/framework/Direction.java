package mapobjects.framework;

public class Direction {

    private double direction;
    public static double PI = Math.PI;

    public Direction() {
        direction = 0;
    }

    public Direction(int direction) {
        this.direction = Math.toRadians(direction);
    }


    public double getDirection() {
        return direction;
    }

    public int getDegreeDirection() {
        return (int)(Math.toDegrees(direction));
    }

    public void setDirection(int degrees) {
        direction = Math.toRadians(degrees);
    }

    public double getXComponent() {
        return Math.cos(direction);
    }

    public double getYComponent() {
        return Math.sin(direction);
    }

    public void rotate(int degrees) {
        direction = (direction + Math.toRadians(degrees)) % (2 * Math.PI);
        if (direction < 0) direction += 2 * Math.PI;
    }

    //I is the angular inertia, takes a value between 0 and 1
    public void rotateTowards(double[] from, double[] towards, double I) {
        double dx = towards[0] - from[0];
        double dy = towards[1] - from[1];
        double aimedDirection = Math.atan2(dy, dx); // always safe, handles all quadrants

        double angleDiff = aimedDirection - direction;
        angleDiff = Math.atan2(Math.sin(angleDiff), Math.cos(angleDiff)); // keeps it between -π and π

        direction += (1 - I) * angleDiff;
    }
}
package helperobjects;

import mapobjects.framework.Box;

//static methods for collisions
public class CollisionMethods {

    //IS IN METHODS

    public static boolean intersects(Box a, Box b) {
        double[] A = a.getCorners(), B = b.getCorners();
        double ax0 = A[0], ay0 = A[1], ax1 = A[2], ay1 = A[3];
        double bx0 = B[0], by0 = B[1], bx1 = B[2], by1 = B[3];

        return ax0 < bx1 && ax1 > bx0 &&
                ay0 < by1 && ay1 > by0;
    }

    public static boolean isIn(double x, double y, Box box) {
        return isIn(x, y, box.getCorners());
    }

    public static boolean isIn(double x, double y, double[] obstacle) {
        return ((x > obstacle[0]) && (x < obstacle[2]) && (y > obstacle[1]) && (y < obstacle[3]));
    }


    //SHIFT METHODS

    public static void xShiftBox(double delta, double[] box) {
        shiftBoxCoordinate(delta, box, 0);
        shiftBoxCoordinate(delta, box, 2);
    }

    public static void yShiftBox(double delta, double[] box) {
        shiftBoxCoordinate(delta, box, 1);
        shiftBoxCoordinate(delta, box, 3);
    }

    public static void shiftBoxCoordinate(double delta, double[] box, int index) {
        box[index] += delta;
    }


/*

    public static boolean playerIsIn(Player player, double[] obstacle) {
        double halfSide = player.getSide()/2;
        double x = player.getX(), y = player.getY();
        return isIn(x + halfSide, y + halfSide, obstacle) ||
                isIn(x + halfSide, y - halfSide, obstacle) ||
                isIn(x - halfSide, y + halfSide, obstacle) ||
                isIn(x - halfSide, y - halfSide, obstacle) ||
                isIn(x + halfSide, y, obstacle) ||
                isIn(x - halfSide, y, obstacle) ||
                isIn(x, y + halfSide, obstacle) ||
                isIn(x, y - halfSide, obstacle);
    }

    public static boolean playerCenterIsIn(Player player, double[] obstacle) {
        double x = player.getX(), y = player.getY();
        return ((x > obstacle[0]) && (x < obstacle[2]) && (y > obstacle[1]) && (y < obstacle[3]));
    }

    //OLD LINE COLLISION METHODS

    //updates position and velocity if movingCollidable collides to a wall
    public static void checkMovingCollidableLineCollision(MovingCollidable movingCollidable, double[] coordinates) {
        // X-axis collision
        if (!movingCollidable.isXCollided()) {
            if (movingCollidable.getXVelocity() > 0) {
                if (movingCollidableLineCollision(movingCollidable, coordinates, Side.LEFT)) {
                    movingCollidable.xCollide();
                    movingCollidable.setX(coordinates[0] - movingCollidable.getWidth() / 2);
                    movingCollidable.setXVelocity(0);
                }
            } else if (movingCollidable.getXVelocity() < 0) {
                if (movingCollidableLineCollision(movingCollidable, coordinates, Side.RIGHT)) {
                    movingCollidable.xCollide();
                    movingCollidable.setX(coordinates[2] + movingCollidable.getWidth() / 2);
                    movingCollidable.setXVelocity(0);
                }
            }
        }
        // Y-axis collision
        if (!movingCollidable.isYCollided()) {
            if (movingCollidable.getYVelocity() > 0) {
                if (movingCollidableLineCollision(movingCollidable, coordinates, Side.BOTTOM)) {
                    movingCollidable.yCollide();
                    movingCollidable.setY(coordinates[1] - movingCollidable.getHeight() / 2);
                    movingCollidable.setYVelocity(0);
                }
            } else if (movingCollidable.getYVelocity() < 0) {
                if (movingCollidableLineCollision(movingCollidable, coordinates, Side.TOP)) {
                    movingCollidable.yCollide();
                    movingCollidable.setY(coordinates[3] + movingCollidable.getHeight() / 2);
                    movingCollidable.setYVelocity(0);
                }
            }
        }
    }

    private enum Side {TOP, BOTTOM, RIGHT, LEFT}
    private static boolean movingCollidableLineCollision(MovingCollidable player, double[] obstacle, Side side) {
        double x0 = obstacle[0], y0 = obstacle[1], x1 = obstacle[2], y1 = obstacle[3];

        double x = player.getX();
        double y = player.getY();
        double nextX = x + player.getXVelocity() * Frame.DT;
        double nextY = y + player.getYVelocity() * Frame.DT;
        double halfSide = player.getWidth() / 2;

        if (side == Side.TOP) {
            return xLineCollision(x - halfSide, y - halfSide, nextY - halfSide, x0, x1, y1) ||
                    xLineCollision(x + halfSide, y - halfSide, nextY - halfSide, x0, x1, y1) ||
                    xLineCollision(x, y - halfSide, nextY - halfSide, x0, x1, y1);
        } else if (side == Side.BOTTOM) {
            return xLineCollision(x - halfSide, y + halfSide, nextY + halfSide, x0, x1, y0) ||
                    xLineCollision(x + halfSide, y + halfSide, nextY + halfSide, x0, x1, y0) ||
                    xLineCollision(x, y + halfSide, nextY + halfSide, x0, x1, y0);
        } else if (side == Side.LEFT) {
            return yLineCollision(x + halfSide, y - halfSide, nextX - halfSide, y0, y1, x0) ||
                    yLineCollision(x + halfSide, y + halfSide, nextX - halfSide, y0, y1, x0) ||
                    yLineCollision(x + halfSide, y, nextX - halfSide, y0, y1, x0);
        } else { // RIGHT
            return yLineCollision(x - halfSide, y - halfSide, nextX + halfSide, y0, y1, x1) ||
                    yLineCollision(x - halfSide, y + halfSide, nextX + halfSide, y0, y1, x1) ||
                    yLineCollision(x - halfSide, y, nextX + halfSide, y0, y1, x1);
        }
    }

    private static boolean xLineCollision(double x, double y, double nextY, double x0, double x1, double y0) {
        // Check if y0 is between y and nextY, or if both y and nextY are equal to y0
        if (y <= y0 && y0 <= nextY || y >= y0 && y0 >= nextY) {
            // Handle the case where the line is exactly horizontal (y == nextY == y0)
            if (y == nextY && y != y0) {
                return false;
            }
            return x0 < x && x < x1;
        }
        return false;
    }

    private static boolean yLineCollision(double x, double y, double nextX, double y0, double y1, double x0) {
        // Check if x0 is between x and nextX, or if both x and nextX are equal to x0
        if (x <= x0 && x0 <= nextX || x >= x0 && x0 >= nextX) {
            // Handle the case where the line is exactly vertical (x == nextX == x0)
            if (x == nextX && x != x0) {
                return false;
            }
            return y0<y && y<y1;
        }
        return false;
    }


 */

}

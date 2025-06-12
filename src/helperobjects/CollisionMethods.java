package helperobjects;

import game.Frame;
import game.Player;

//static methods for collisions
public class CollisionMethods {

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


    public static boolean isIn(double x, double y, double[] obstacle) {
        return ((x > obstacle[0]) && (x < obstacle[2]) && (y > obstacle[1]) && (y < obstacle[3]));
    }

    public static boolean isIn(Player player, double[] obstacle) {
        double x = player.getX(), y = player.getY();
        return ((x > obstacle[0]) && (x < obstacle[2]) && (y > obstacle[1]) && (y < obstacle[3]));
    }


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



    //updates position and velocity if player collides to a wall
    public static void checkCollision(Player player, double[] coordinates) {
        // X-axis collision
        if (!player.isXCollided()) {
            if (player.getXVelocity() > 0) {
                if (checkLineCollision(player, coordinates, Side.LEFT)) {
                    player.xCollide();
                    player.setX(coordinates[0] - player.getSide() / 2);
                    player.setXVelocity(0);
                }
            } else if (player.getXVelocity() < 0) {
                if (checkLineCollision(player, coordinates, Side.RIGHT)) {
                    player.xCollide();
                    player.setX(coordinates[2] + player.getSide() / 2);
                    player.setXVelocity(0);
                }
            }
        }
        // Y-axis collision
        if (!player.isYCollided()) {
            if (player.getYVelocity() > 0) {
                if (checkLineCollision(player, coordinates, Side.BOTTOM)) {
                    player.yCollide();
                    player.setY(coordinates[1] - player.getSide() / 2);
                    player.setYVelocity(0);
                }
            } else if (player.getYVelocity() < 0) {
                if (checkLineCollision(player, coordinates, Side.TOP)) {
                    player.yCollide();
                    player.setY(coordinates[3] + player.getSide() / 2);
                    player.setYVelocity(0);
                }
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    //COLLISIONS

    public enum Side {TOP, BOTTOM, RIGHT, LEFT}
    private static boolean checkLineCollision(Player player, double[] obstacle, Side side) {
        double x0 = obstacle[0], y0 = obstacle[1], x1 = obstacle[2], y1 = obstacle[3];

        double x = player.getX();
        double y = player.getY();
        double nextX = x + player.getXVelocity() * Frame.DT;
        double nextY = y + player.getYVelocity() * Frame.DT;
        double halfSide = player.getSide() / 2;

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

}

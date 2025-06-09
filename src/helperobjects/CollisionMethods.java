package helperobjects;

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

}

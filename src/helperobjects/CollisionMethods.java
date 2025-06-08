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

}

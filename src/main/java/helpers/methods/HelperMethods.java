package helpers.methods;

import mapobjects.traits.GridObject;


public class HelperMethods {

    public static boolean outOfMapBounds(GridObject[][] layer, int i, int j) {
        return i < 0 || j < 0 || i >= layer.length || j >= layer[0].length;
    }

    public static String getDirectionString(int xDirection, int yDirection) {
        String direction;
        if (xDirection == 0 && yDirection == 0) {
            direction = "0";
        } else if (xDirection == 0 && yDirection == 1) {
            direction = "D";
        } else if (xDirection == 0 && yDirection == -1) {
            direction = "U";
        } else if (xDirection == 1 && yDirection == 0) {
            direction = "R";
        } else if (xDirection == -1 && yDirection == 0) {
            direction = "L";
        } else if (xDirection == 1 && yDirection == 1) {
            direction = "DR";
        } else if (xDirection == -1 && yDirection == 1) {
            direction = "DL";
        } else if (xDirection == 1 && yDirection == -1) {
            direction = "UR";
        } else if (xDirection == -1 && yDirection == -1) {
            direction = "UL";
        } else {
            direction = "invalid";
        }
        return direction;
    }

}

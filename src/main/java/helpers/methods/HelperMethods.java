package helpers.methods;

import mapobjects.traits.GridObject;

import java.util.function.Consumer;

public class HelperMethods {

    public static void iterateThroughLayers(GridObject[][][] layers, int startX, int startY, int endX, int endY, Consumer<GridObject> action) {
        for (GridObject[][] layer : layers) {
            for (int i = startY; i<=endY; i++) {
                for (int j = startX; j<=endX; j++) {
                    GridObject gridObject = layer[i-1][j-1];
                    if (gridObject!=null) {
                        action.accept(gridObject);
                    }
                }
            }
        }
    }

    public static char[] toCharArray(String[] stringArray) {

        int l = stringArray.length;
        char[] returnArray = new char[l];

        for (int i = 0; i<l; i++) {
            returnArray[i] = stringArray[i].charAt(0);
        }
        return returnArray;

    }

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

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String center(String s, int width) {
        if (s.length() >= width) {
            return s;
        }

        int leftPadding = (width - s.length()) / 2;
        int rightPadding = width - s.length() - leftPadding;

        return " ".repeat(leftPadding) + s + " ".repeat(rightPadding);
    }
}

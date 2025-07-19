package helperobjects;

import mapobjects.category.GridObject;

import java.util.function.Consumer;

public class Helpers {

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

}

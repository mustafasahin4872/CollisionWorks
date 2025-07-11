package helperobjects;

import mapobjects.framework.GridObject;

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

}

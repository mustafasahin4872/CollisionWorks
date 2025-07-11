package game;

import helperobjects.Helpers;
import helperobjects.MapMaker;
import mapobjects.framework.GridObject;
import mapobjects.initialized.*;

import java.util.function.Consumer;

public class GameMap {

    private final Player player;
    private final int worldIndex, levelIndex;

    private final double width, height; //default 3200x1800
    private final int xTile, yTile; //default 64x34

    private int[] tileRange;

    //all the elements in map is rectangular. The format of each rectangular region coordinates is
    //{x0, y0, x1, y1} unless stated otherwise.
    //(x0, y0) and (x1, y1) are the rectangle's bottom left and top right corners.

    private final GridObject[][][] layers;

    //private final int totalCoinOnMap;

    //------------------------------------------------------------------------------------------------------------

    public GameMap(int worldIndex, int levelIndex, Player player) {
        this(worldIndex, levelIndex, 64, 36, player);
    }

    public GameMap(int worldIndex, int levelIndex, int xTile, int yTile, Player player) {
        this(worldIndex, levelIndex, xTile, yTile, player, false);
    }

    public GameMap(int worldIndex, int levelIndex, int xTile, int yTile, Player player, boolean isSelectionMap) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.xTile = xTile;
        this.yTile = yTile;
        this.player = player;

        width = xTile*Tile.HALF_SIDE*2;
        height = yTile*Tile.HALF_SIDE*2;

        MapMaker mapMaker = new MapMaker(worldIndex, levelIndex, xTile, yTile, player, isSelectionMap);
        mapMaker.mapMaker();
        layers = mapMaker.getLayers();

        setFrameTileRange();
    }

    //------------------------------------------------------------------------------------------------------------

    public int getWorldIndex() {
        return worldIndex;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public Player getPlayer() {
        return player;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    //------------------------------------------------------------------------------------------------------------

    public void callMapObjects() {
        player.call(layers);
        for (GridObject[][] layer : layers) {
            for (int y = tileRange[1]; y <= tileRange[3]; y++) {
                for (int x = tileRange[0]; x <= tileRange[2]; x++) {
                    GridObject gridObject = layer[y-1][x-1];
                    if (gridObject == null) continue;
                    gridObject.call(player);
                    if (gridObject.isExpired()) {
                        layer[y-1][x-1] = null;
                    }
                }
            }
        }

    }

    public void draw() {
        iterateCurrentFrameObjects(GridObject::draw);
    }

    private void iterateCurrentFrameObjects(Consumer<GridObject> action) {
        Helpers.iterateThroughLayers(layers, tileRange[0], tileRange[1], tileRange[2], tileRange[3], action);
    }

    //--------------------------------------------------------------------------------------------------

    public void setFrameTileRange() {
        //normally ranges should be 9 and 6, but for guarantee, set them higher
        final int X_RANGE = 12, Y_RANGE = 10;

        int xNum = (int) (player.getX() / GridObject.TILE_SIDE);
        int yNum = (int) (player.getY() / GridObject.TILE_SIDE);

        int minX = xNum - X_RANGE;
        int maxX = xNum + X_RANGE + 1;

        if (minX<1) {
            minX = 1;
            maxX = Math.min(minX + X_RANGE * 2, xTile);

        } else if (maxX > xTile) {
            maxX = xTile;
            minX = Math.max(maxX - X_RANGE * 2, 1);
        }

        int minY = yNum - Y_RANGE;
        int maxY = yNum + Y_RANGE + 1;

        if (minY<1) {
            minY = 1;
            maxY = Math.min(minY + Y_RANGE * 2, yTile);
        }
        else if (maxY > yTile) {
            maxY = yTile;
            minY = Math.max(maxY - Y_RANGE *2, 1);
        }

        tileRange = new int[]{minX, minY, maxX, maxY};
    }

}

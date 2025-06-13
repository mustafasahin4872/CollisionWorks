package game;

import helperobjects.MapMaker;
import mapobjects.framework.MapObject;
import mapobjects.initialized.*;

import java.util.function.Consumer;

public class GameMap {

    private final Player player;
    private final int worldIndex, levelIndex;

    private final double width, height; //default 3200x1800
    private final int xTile, yTile; //default 64x34

    private static final int RANGE = 15; //the tiles to be included in calculations
    private int[] tileRange;

    //all the elements in map is rectangular. The format of each rectangular region coordinates is
    //{x0, y0, x1, y1} unless stated otherwise.
    //(x0, y0) and (x1, y1) are the rectangle's bottom left and top right corners.

    private final MapObject[][][] layers;

    //private final int totalCoinOnMap;

    //------------------------------------------------------------------------------------------------------------
    //CONSTRUCTOR

    public GameMap(int worldIndex, int levelIndex, Player player) {
        this(worldIndex, levelIndex, 64, 36, player);
    }

    public GameMap(int worldIndex, int levelIndex, int xTile, int yTile, Player player) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.xTile = xTile;
        this.yTile = yTile;
        this.player = player;

        width = xTile*Tile.HALF_SIDE*2;
        height = yTile*Tile.HALF_SIDE*2;

        MapMaker mapMaker = new MapMaker(worldIndex, levelIndex, xTile, yTile, player);
        mapMaker.mapMaker();
        layers = mapMaker.getLayers();

        setFrameTileRange();
    }

    //------------------------------------------------------------------------------------------------------------
    //GETTERS AND SETTERS

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

    public void mapObjectCalls() {

        iterateCurrentFrameObjects(mapObject -> mapObject.call(player));

        if (!player.isXCollided()) {
            player.setX(player.getX()+ player.getXVelocity() * Frame.DT);
        }
        if (!player.isYCollided()) {
            player.setY(player.getY() + player.getYVelocity() * Frame.DT);
        }

        player.resetXCollided();
        player.resetYCollided();

    }

    private void iterateCurrentFrameObjects(Consumer<MapObject> action) {
        for (MapObject[][] layer : layers) {
            for (int y = tileRange[1]; y <= tileRange[3]; y++) {
                for (int x = tileRange[0]; x <= tileRange[2]; x++) {
                    MapObject mapObject = layer[y - 1][x - 1];
                    if (mapObject != null) {
                        action.accept(mapObject); // 💅 do the thing
                    }
                }
            }
        }
    }


    //---------------------------------------------------------------------------------------------
    //DRAW

    public void draw() {
        iterateCurrentFrameObjects(MapObject::draw);
    }

    //--------------------------------------------------------------------------------------------------

    public void setFrameTileRange() {
        int xNum = (int) (player.getX() / MapObject.TILE_SIDE);
        int yNum = (int) (player.getY() / MapObject.TILE_SIDE);

        int minX = xNum - RANGE;
        int maxX = xNum + RANGE;

        if (minX<1) {
            minX = 1;
            maxX = Math.min(minX + RANGE * 2, xTile);

        } else if (maxX> xTile) {
            maxX = xTile;
            minX = Math.max(maxX - RANGE * 2, 1);

        }

        int minY = yNum - RANGE;
        int maxY = yNum + RANGE;

        if (minY<1) {
            minY = 1;
            maxY = Math.min(minY + RANGE * 2, yTile);
        }
        else if (maxY > yTile) {
            maxY = yTile;
            minY = Math.max(maxY - RANGE *2, 1);
        }

        tileRange = new int[]{minX, minY, maxX, maxY};
    }

}

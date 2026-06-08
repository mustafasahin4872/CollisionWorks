package game;

import helpers.HelperMethods;
import helpers.MapMaker;
import helpers.MapType;
import mapobjects.category.GridObject;
import mapobjects.category.MapObject;
import mapobjects.category.Moving;
import mapobjects.category.MovingCollidable;
import mapobjects.mapobject.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static helpers.CollisionMethods.isIn;

public class GameMap {

    private final Player player;

    private final double width, height;
    private final int xTile, yTile;
    private final double[] spawnPoint;

    private int[] tileRange;

    private final GridObject[][][] layers;
    private final Set<Moving> movingObjects = new HashSet<>();
    private final Set<MovingCollidable> movingCollidableObjects = new HashSet<>();
    private final Set<MapObject> alwaysCalledObjects = new HashSet<>();

    //private final int totalCoinOnMap;

    //------------------------------------------------------------------------------------------------------------

    public GameMap(GameState gameState, MapType mapType) {
        xTile = mapType.xTile;
        yTile = mapType.yTile;
        this.player = gameState.player;

        width = xTile*Tile.HALF_SIDE*2;
        height = yTile*Tile.HALF_SIDE*2;

        MapMaker mapMaker = new MapMaker(gameState.worldIndex, gameState.levelIndex, xTile, yTile, player, mapType);
        mapMaker.mapMaker();
        layers = mapMaker.getLayers();
        spawnPoint = mapMaker.getSpawnPoint();

        for (GridObject[][] layer : layers) {
            for (int i = 0; i<yTile; i++) {
                for (int j = 0; j<xTile; j++) {
                    GridObject gridObject = layer[i][j];
                    if (gridObject instanceof MovingCollidable movingCollidable) {
                        movingCollidableObjects.add(movingCollidable);
                    } else if (gridObject instanceof Moving moving) {
                        movingObjects.add(moving);
                    }
                    if (gridObject instanceof Door || gridObject instanceof Shooter || gridObject instanceof Moving) {
                        alwaysCalledObjects.add(gridObject);
                    }
                }
            }
        }

        setFrameTileRange();
    }

    //------------------------------------------------------------------------------------------------------------

    public Player getPlayer() {
        return player;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double[] getSpawnPoint() {return spawnPoint;}

    //------------------------------------------------------------------------------------------------------------

    public void callMapObjects() {
        player.call(layers);

        for (MapObject mapObject : alwaysCalledObjects) {
            mapObject.call(player);
        }
        alwaysCalledObjects.removeIf(MapObject::isExpired);

        for (GridObject[][] layer : layers) {
            for (int y = tileRange[1]; y <= tileRange[3]; y++) {
                for (int x = tileRange[0]; x <= tileRange[2]; x++) {
                    GridObject gridObject = layer[y-1][x-1];
                    if (gridObject == null) continue;

                    if (!alwaysCalledObjects.contains(gridObject)) {
                        if (gridObject instanceof EmptyGridObject e) {
                            if (!alwaysCalledObjects.contains(e.getLinkedObject())) {
                                gridObject.call(player);
                            }
                        } else {
                            gridObject.call(player);
                        }

                    }
                    if (gridObject.isExpired()) {
                        layer[y-1][x-1] = null;
                    }
                }
            }
        }

    }

    public void draw() {
        iterateCurrentFrameObjects(GridObject::draw);
        iterateAlwaysCalledObjects(MapObject::draw);
    }

    private void iterateCurrentFrameObjects(Consumer<GridObject> action) {
        HelperMethods.iterateThroughLayers(layers, tileRange[0], tileRange[1], tileRange[2], tileRange[3], action);
    }

    private void iterateAlwaysCalledObjects(Consumer<MapObject> action) {
        for (MapObject mapObject : alwaysCalledObjects) {
            action.accept(mapObject);
        }

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

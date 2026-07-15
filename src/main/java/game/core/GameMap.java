package game.core;

import mapobjects.traits.*;
import mapobjects.entities.*;
import mapobjects.traits.effectors.Effector;
import mapobjects.traits.receivers.HealthBearer;
import mapobjects.traits.receivers.Receiver;
import mapobjects.traits.receivers.TileReceiver;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.schemas.MapObject;
import mapobjects.traits.triggerables.OnTriggerable;
import mapobjects.traits.triggerables.Ranged;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static helpers.CollisionMethods.isIn;
import static mapobjects.traits.schemas.GridObject.TILE_SIDE;

public class GameMap {

    private final Player player;

    private final double width, height;
    private final int xTile, yTile;
    private final double[] spawnPoint;

    private int[] tileRange;

    private final GridObject[][][] layers;
    private final Tile[][] tiles;
    private final GridObject[][] gridObjects;
    private final EmptyGridObject[][] emptyGridObjects;

    private final Set<Moving> movingObjects = new HashSet<>();
    private final Set<MovingCollidable> movingCollidableObjects = new HashSet<>();
    private final Set<HealthBearer> healthBearers = new HashSet<>();
    private final Set<Ranged> rangeds = new HashSet<>();
    private final Set<OnTriggerable> onTriggerables = new HashSet<>();
    private final Set<MapObject> alwaysCalledObjects = new HashSet<>();
    private final Set<MapObject> spawnedObjects;
    private final Set<TileReceiver> tileReceivers = new HashSet<>();
    private final Set<Drawable> drawables = new HashSet<>();

    // private final int totalCoinOnMap;

    // ------------------------------------------------------------------------------------------------------------

    public GameMap(GameState gameState, MapMaker.MapType mapType) {
        xTile = mapType.xTile;
        yTile = mapType.yTile;
        this.player = gameState.getPlayer();

        width = xTile * Tile.HALF_SIDE * 2;
        height = yTile * Tile.HALF_SIDE * 2;

        MapMaker mapMaker = new MapMaker(gameState.getWorldIndex(), gameState.getLevelIndex(), xTile, yTile, player,
                mapType);
        mapMaker.mapMaker();
        layers = mapMaker.getLayers();
        tiles = mapMaker.getTiles();
        gridObjects = mapMaker.getGridObjects();
        emptyGridObjects = mapMaker.getEmptyGridObjects();

        spawnPoint = mapMaker.getSpawnPoint();
        spawnedObjects = mapMaker.getSpawnedObjects();

        ArrayList<Shooter> shooters = new ArrayList<>();
        ArrayList<Sign> signs = new ArrayList<>();

        for (int i = 0; i < yTile; i++) {
            for (int j = 0; j < xTile; j++) {
                GridObject gridObject = gridObjects[i][j];
                if (gridObject instanceof Moving moving) {
                    movingObjects.add(moving);
                    if (gridObject instanceof MovingCollidable movingCollidable) {
                        movingCollidableObjects.add(movingCollidable);
                    }
                }
                if (gridObject instanceof OnTriggerable o) {
                    onTriggerables.add(o);
                }
                if (gridObject instanceof TileReceiver tileReceiver) {
                    tileReceivers.add(tileReceiver);
                }
                if (gridObject instanceof HealthBearer healthBearer) {
                    healthBearers.add(healthBearer);
                }
                if (gridObject instanceof Ranged ranged) {
                    rangeds.add(ranged);
                }
                if (gridObject instanceof Shooter s)
                    shooters.add(s);
                if (gridObject instanceof Sign s) signs.add(s);
                if (gridObject instanceof Door || gridObject instanceof Shooter || gridObject instanceof Moving) {
                    alwaysCalledObjects.add(gridObject);
                }
                if (gridObject instanceof Drawable d)
                    drawables.add(d);
            }
        }

        healthBearers.add(player);
        tileReceivers.add(player);
        movingObjects.add(player);

        for (Ranged ranged : rangeds) {
            ranged.setTriggerers(movingObjects);
        }
        for (OnTriggerable o : onTriggerables) {
            if (!(o instanceof Sign)) o.setTriggerers(movingObjects);
        }

        for (Shooter s : shooters) {
            s.setTargets(healthBearers);
        }
        for (Sign sign : signs) {
            sign.setTriggerers(new HashSet<>(Set.of(player)));
        }
        player.setTargets(healthBearers);
        setFrameTileRange();
    }

    public static boolean outOfMapBounds(GridObject[][] layer, int x, int y) {
        return y < 0 || x < 0 || y >= layer.length || x >= layer[0].length;
    }

    // ------------------------------------------------------------------------------------------------------------

    public Player getPlayer() {
        return player;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double[] getSpawnPoint() {
        return spawnPoint;
    }

    // ------------------------------------------------------------------------------------------------------------

    public void callMapObjects() {
        player.call(layers);
        player.call();

        // dump all spawned objects into respective sets and clear
        for (MapObject mapObject : spawnedObjects) {
            alwaysCalledObjects.add(mapObject);
            if (mapObject instanceof TileReceiver t) tileReceivers.add(t);
        }
        spawnedObjects.clear();

        for (MapObject mapObject : alwaysCalledObjects) {
            if (mapObject instanceof Projectile p) p.call(player, layers);
            else mapObject.call(player);
            if (mapObject instanceof Receiver r) r.call();
        }

        for (TileReceiver tileReceiver : tileReceivers) {
            int[] indexes = tileReceiver.getCoveredTileIndexes();
            for (int x = indexes[0]; x<=indexes[2]; x++) {
                for (int j = indexes[1]; j<= indexes[3]; j++) {
                    if (outOfMapBounds(tiles, x, j)) continue;
                    Tile tile = tiles[j][x];
                    if (tile instanceof Effector e) {
                        e.sendEffect(tileReceiver);
                    }
                }
            }
        }

        alwaysCalledObjects.removeIf(MapObject::isExpired);


        for (GridObject[][] layer : layers) {
            for (int y = tileRange[1]; y <= tileRange[3]; y++) {
                for (int x = tileRange[0]; x <= tileRange[2]; x++) {
                    GridObject gridObject = layer[y][x];
                    if (gridObject == null)
                        continue;

                    if (!alwaysCalledObjects.contains(gridObject)) {
                        if (gridObject instanceof EmptyGridObject e) {
                            if (!alwaysCalledObjects.contains(e.getLinkedObject())) {
                                gridObject.call(player);
                                if (gridObject instanceof Receiver r) r.call();
                            }
                        } else {
                            gridObject.call(player);
                            if (gridObject instanceof Receiver r) r.call();
                        }
                    }
                    if (gridObject.isExpired()) {
                        layer[y][x] = null;
                    }
                }
            }
        }

    }

    public void draw() {
        drawCurrentFrameObjects();
        drawAlwaysCalledObjects();
    }

    private void drawCurrentFrameObjects() {
        int startX = tileRange[0];
        int startY = tileRange[1];
        int endX = tileRange[2];
        int endY = tileRange[3];
        for (GridObject[][] layer : layers) {
            for (int y = startY; y <= endY; y++) {
                for (int x = startX; x <= endX; x++) {
                    GridObject gridObject = layer[y][x];
                    if (gridObject != null) {
                        if (gridObject instanceof Drawable d)
                            d.draw();
                    }
                }
            }
        }
    }

    private void drawAlwaysCalledObjects() {
        for (MapObject mapObject : alwaysCalledObjects) {
            if (mapObject instanceof Drawable d)
                d.draw();
        }
    }

    // --------------------------------------------------------------------------------------------------

    public void setFrameTileRange() {
        // normally ranges should be 9 and 6, but for guarantee, set them higher
        final int X_RANGE = 12, Y_RANGE = 10;

        int xNum = (int) (player.getX() / GridObject.TILE_SIDE);
        int yNum = (int) (player.getY() / GridObject.TILE_SIDE);

        int minX = xNum - X_RANGE;
        int maxX = xNum + X_RANGE + 1;

        if (minX < 0) {
            minX = 0;
            maxX = Math.min(minX + X_RANGE * 2, xTile-1);
        } else if (maxX >= xTile) {
            maxX = xTile-1;
            minX = Math.max(maxX - X_RANGE * 2, 0);
        }

        int minY = yNum - Y_RANGE;
        int maxY = yNum + Y_RANGE + 1;

        if (minY < 0) {
            minY = 0;
            maxY = Math.min(minY + Y_RANGE * 2, yTile-1);
        } else if (maxY >= yTile) {
            maxY = yTile-1;
            minY = Math.max(maxY - Y_RANGE * 2, 0);
        }

        tileRange = new int[] { minX, minY, maxX, maxY };
    }

}

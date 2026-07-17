package game.core;

import mapobjects.entities.*;
import mapobjects.traits.collisions.Movable;
import mapobjects.traits.collisions.Moving;
import mapobjects.traits.collisions.MovingCollidable;
import mapobjects.traits.senders.Sender;
import mapobjects.traits.receivers.HealthEffectReceiver;
import mapobjects.traits.receivers.Receiver;
import mapobjects.traits.receivers.TileReceiver;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.schemas.MapObject;
import mapobjects.traits.triggerables.MovedOverTriggerable;
import mapobjects.traits.triggerables.PlayerOnTriggerable;
import mapobjects.traits.triggerables.RangeTriggerable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameMap {

    private final Player player;

    private final double width, height;
    private final int xTile, yTile;
    private final double[] spawnPoint;

    private int[] tileRange;

    private final Set<MapObject> allMapObjects;
    private final Set<MapObject>[][] map;
    private final Tile[][] tiles;

    private final Set<Movable> movables = new HashSet<>();
    private final Set<MovingCollidable> movingCollidableObjects = new HashSet<>();
    private final Set<HealthEffectReceiver> healthEffectReceivers = new HashSet<>();
    private final Set<RangeTriggerable> rangeTriggerables = new HashSet<>();
    private final Set<MovedOverTriggerable> movedOverTriggerables = new HashSet<>();
    private final Set<PlayerOnTriggerable> playerOnTriggerables = new HashSet<>();
    private final Set<MapObject> alwaysCalledObjects = new HashSet<>();
    private final Set<MapObject> spawnedObjects;
    private final Set<TileReceiver> tileReceivers = new HashSet<>();
    private final Set<Shooter.MovingShooter> movingShooters = new HashSet<>();
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
        allMapObjects = mapMaker.getAllMapObjects();
        map = mapMaker.getMap();
        tiles = mapMaker.getTiles();

        spawnPoint = mapMaker.getSpawnPoint();
        spawnedObjects = mapMaker.getSpawnedObjects();

        ArrayList<Shooter> shooters = new ArrayList<>();
        ArrayList<Sign> signs = new ArrayList<>();

        for (int y = 0; y < yTile; y++) {
            for (int x = 0; x < xTile; x++) {
                Set<MapObject> objects = map[y][x];
                for (MapObject mapObject : objects) {

                    if (mapObject instanceof Movable movable) {
                        movables.add(movable);
                        if (mapObject instanceof Moving moving) {
                            if (mapObject instanceof MovingCollidable movingCollidable) {
                                movingCollidableObjects.add(movingCollidable);
                            }
                        }
                    }
                    if (mapObject instanceof MovedOverTriggerable o) {
                        movedOverTriggerables.add(o);
                    }
                    if (mapObject instanceof PlayerOnTriggerable p) {
                        playerOnTriggerables.add(p);
                    }
                    if (mapObject instanceof TileReceiver tileReceiver) {
                        tileReceivers.add(tileReceiver);
                    }
                    if (mapObject instanceof HealthEffectReceiver healthEffectReceiver) {
                        healthEffectReceivers.add(healthEffectReceiver);
                    }
                    if (mapObject instanceof RangeTriggerable rangeTriggerable) {
                        rangeTriggerables.add(rangeTriggerable);
                    }
                    if (mapObject instanceof Shooter s) {
                        shooters.add(s);
                        if (mapObject instanceof Shooter.MovingShooter m) {
                            movingShooters.add(m);
                        }
                    }
                    if (mapObject instanceof Sign s) {
                        signs.add(s);
                    }
                    if (mapObject instanceof Door || mapObject instanceof Shooter || mapObject instanceof Moving) {
                        alwaysCalledObjects.add(mapObject);
                    }
                    if (mapObject instanceof Drawable d) {
                        drawables.add(d);
                    }

                }

            }
        }


        healthEffectReceivers.add(player);
        tileReceivers.add(player);
        movables.add(player);
        movingCollidableObjects.add(player);

        for (RangeTriggerable rangeTriggerable : rangeTriggerables) {
            rangeTriggerable.setRangeTriggerers(movables);
        }
        for (MovedOverTriggerable o : movedOverTriggerables) {
            o.setMovedOverTriggerers(movables);
        }
        for (PlayerOnTriggerable p : playerOnTriggerables) {
            p.setPlayerOnTriggerers(Set.of(player));
        }

        for (Shooter s : shooters) {
            s.setTargets(healthEffectReceivers);
        }
        player.setTargets(healthEffectReceivers);
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

    public Set<MovingCollidable> getMovingCollidableObjects() {
        return movingCollidableObjects;
    }

    // ------------------------------------------------------------------------------------------------------------

    public void rebuildGrid() {

        for (int y = 0; y < yTile; y++) {
            for (int x = 0; x < xTile; x++) {
                map[y][x].clear();
            }
        }

        for (MapObject mapObject : allMapObjects) {

            if (mapObject.isExpired()) continue;

            int[] coveredTileIndexes = mapObject.getCoveredTileIndexes();
            int startX = coveredTileIndexes[0];
            int endX = coveredTileIndexes[2];
            int startY = coveredTileIndexes[1];
            int endY = coveredTileIndexes[3];

            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    map[y][x].add(mapObject);
                }
            }

        }

    }

    public Set<MapObject> getObjectsInTileRange(int startX, int startY, int endX, int endY) {

        Set<MapObject> objects = new HashSet<>();

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                objects.addAll(map[y][x]);
                objects.add(tiles[y][x]);
            }
        }


        return objects;

    }

    // ------------------------------------------------------------------------------------------------------------

    public void callMapObjects() {
        player.call();
        player.callReceiver();

        // dump all spawned objects into respective sets and clear
        for (MapObject mapObject : spawnedObjects) {
            alwaysCalledObjects.add(mapObject);
            if (mapObject instanceof TileReceiver t) tileReceivers.add(t);
            if (mapObject instanceof MovingCollidable mc) movingCollidableObjects.add(mc);
            if (mapObject instanceof RangeTriggerable rt) {
                rt.setRangeTriggerers(movables);
            }
            if (mapObject instanceof MovedOverTriggerable mt) {
                mt.setMovedOverTriggerers(movables);
            }
            if (mapObject instanceof PlayerOnTriggerable pt) {
                pt.setPlayerOnTriggerers(Set.of(player));
            }
        }
        spawnedObjects.clear();

        for (MapObject mapObject : alwaysCalledObjects) {
            mapObject.call();
            if (mapObject instanceof Receiver r) r.callReceiver();
        }

        for (TileReceiver tileReceiver : tileReceivers) {
            int[] indexes = tileReceiver.getCoveredTileIndexes();
            for (int x = indexes[0]; x<=indexes[2]; x++) {
                for (int j = indexes[1]; j<= indexes[3]; j++) {
                    if (outOfMapBounds(tiles, x, j)) continue;
                    Tile tile = tiles[j][x];
                    if (tile instanceof Sender e) {
                        e.sendEffect(tileReceiver);
                    }
                }
            }
        }

        alwaysCalledObjects.removeIf(MapObject::isExpired);
        movingCollidableObjects.removeIf(mc -> mc instanceof MapObject mo && mo.isExpired());

        for (int y = tileRange[1]; y <= tileRange[3]; y++) {
            for (int x = tileRange[0]; x <= tileRange[2]; x++) {
                Set<MapObject> objects = map[y][x];

                for (MapObject mapObject : objects) {
                    if (!alwaysCalledObjects.contains(mapObject)) {

                        mapObject.call();
                        if (mapObject instanceof Receiver r) r.callReceiver();

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

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                tiles[y][x].draw();
            }
        }

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Set<MapObject> objects = map[y][x];
                for (MapObject mapObject : objects) {
                    if (mapObject instanceof Drawable d) d.draw();
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

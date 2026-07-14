package game.core;

import mapobjects.traits.*;
import mapobjects.entities.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    private final Set<HealthBearer> healthBearers = new HashSet<>();
    private final Set<Ranged> rangeds = new HashSet<>();
    private final Set<OnTriggerable> onTriggerables = new HashSet<>();
    private final Set<MapObject> alwaysCalledObjects = new HashSet<>();
    private final Set<MapObject> spawnedObjects;
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
        spawnPoint = mapMaker.getSpawnPoint();
        spawnedObjects = mapMaker.getSpawnedObjects();

        ArrayList<Shooter> shooters = new ArrayList<>();
        ArrayList<Sign> signs = new ArrayList<>();

        for (GridObject[][] layer : layers) {
            for (int i = 0; i < yTile; i++) {
                for (int j = 0; j < xTile; j++) {
                    GridObject gridObject = layer[i][j];
                    if (gridObject instanceof Moving moving) {
                        movingObjects.add(moving);
                        if (gridObject instanceof MovingCollidable movingCollidable) {
                            movingCollidableObjects.add(movingCollidable);
                        }
                    }
                    if (gridObject instanceof OnTriggerable o) {
                        onTriggerables.add(o);
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
        }
        healthBearers.add(player);
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

    public static boolean outOfMapBounds(GridObject[][] layer, int i, int j) {
        return i < 0 || j < 0 || i >= layer.length || j >= layer[0].length;
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

        for (MapObject mapObject : alwaysCalledObjects) {
            mapObject.call(player);
            if (mapObject instanceof Receiver r) r.call();
        }
        for (MapObject mapObject : spawnedObjects) {
            if (mapObject instanceof Projectile p) p.call(player, layers);
            else mapObject.call(player);
            if (mapObject instanceof Receiver r) r.call();
        }
        alwaysCalledObjects.removeIf(MapObject::isExpired);
        spawnedObjects.removeIf(MapObject::isExpired);

        for (GridObject[][] layer : layers) {
            for (int y = tileRange[1]; y <= tileRange[3]; y++) {
                for (int x = tileRange[0]; x <= tileRange[2]; x++) {
                    GridObject gridObject = layer[y - 1][x - 1];
                    if (gridObject == null)
                        continue;

                    if (!alwaysCalledObjects.contains(gridObject) && !spawnedObjects.contains(gridObject)) {
                        if (gridObject instanceof EmptyGridObject e) {
                            if (!alwaysCalledObjects.contains(e.getLinkedObject()) && !spawnedObjects.contains(e.getLinkedObject())) {
                                gridObject.call(player);
                                if (gridObject instanceof Receiver r) r.call();
                            }
                        } else {
                            gridObject.call(player);
                            if (gridObject instanceof Receiver r) r.call();
                        }
                    }
                    if (gridObject.isExpired()) {
                        layer[y - 1][x - 1] = null;
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
            for (int i = startY; i <= endY; i++) {
                for (int j = startX; j <= endX; j++) {
                    GridObject gridObject = layer[i - 1][j - 1];
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
        for (MapObject mapObject : spawnedObjects) {
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

        if (minX < 1) {
            minX = 1;
            maxX = Math.min(minX + X_RANGE * 2, xTile);

        } else if (maxX > xTile) {
            maxX = xTile;
            minX = Math.max(maxX - X_RANGE * 2, 1);
        }

        int minY = yNum - Y_RANGE;
        int maxY = yNum + Y_RANGE + 1;

        if (minY < 1) {
            minY = 1;
            maxY = Math.min(minY + Y_RANGE * 2, yTile);
        } else if (maxY > yTile) {
            maxY = yTile;
            minY = Math.max(maxY - Y_RANGE * 2, 1);
        }

        tileRange = new int[] { minX, minY, maxX, maxY };
    }

}

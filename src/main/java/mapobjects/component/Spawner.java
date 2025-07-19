package mapobjects.component;

import mapobjects.mapobject.Player;
import helpers.MapObjectGenerator;
import mapobjects.category.GridObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static mapobjects.category.GridObject.TILE_SIDE;

//creates and stores blueprints for the linked object. has different spawn styles
public class Spawner {

    private final Set<MapObjectGenerator> spawnObjects;
    private double centerX;
    private double centerY;
    private final int worldIndex, xNum, yNum;

    public Spawner(int worldIndex, double x, double y) {
        this.worldIndex = worldIndex;
        centerX = x;
        centerY = y;
        xNum = 0;
        yNum = 0;
        spawnObjects = new HashSet<>();
    }

    public Spawner(GridObject gridObject) {
        this.worldIndex = gridObject.getWorldIndex();
        this.xNum = gridObject.getXNum();
        this.yNum = gridObject.getYNum();
        double[] centerCoordinates = gridObject.getCenterCoordinates();
        centerX = centerCoordinates[0];
        centerY = centerCoordinates[1];
        spawnObjects = new HashSet<>();
    }


    public MapObjectGenerator[] getSpawnObjects() {
        return spawnObjects.toArray(new MapObjectGenerator[0]);
    }


    public void replaceAll(MapObjectGenerator[] mapObjectGenerators) {
        spawnObjects.clear();
        Collections.addAll(spawnObjects, mapObjectGenerators);
    }


    //spawns in the adjacent tile to the object depending on direction
    //spawns on top if direction is 0
    public MapObjectGenerator summonSpawn(char direction) {
        return switch (direction) {
            case '<' -> new MapObjectGenerator(worldIndex, centerX-TILE_SIDE, centerY);
            case '>' -> new MapObjectGenerator(worldIndex, centerX+TILE_SIDE, centerY);
            case '^' -> new MapObjectGenerator(worldIndex, centerX, centerY-TILE_SIDE);
            case 'v' -> new MapObjectGenerator(worldIndex, centerX, centerY+TILE_SIDE);
            case '0' -> new MapObjectGenerator(worldIndex, centerX, centerY);
            default -> {
                System.out.println("error in summonSpawn");
                yield null;
            }
        };
    }

    public MapObjectGenerator onTopPlayerSpawn(Player player) {
        int[] gridNumbers = player.getGridNumbers();
        return new MapObjectGenerator(worldIndex, gridNumbers[0], gridNumbers[1]);
    }

    //spawns in the direction of player's current position, spawns on the circle with given radius
        public MapObjectGenerator directionSpawn(double[] towards, double radius) {
        double dx = towards[0] - centerX;
        double dy = towards[1] - centerY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        double spawnX;
        double spawnY;
        if (distance == 0) {
            spawnX = centerX + radius;
            spawnY = centerY;
        } else {
            spawnX = centerX + dx * radius / distance;
            spawnY = centerY + dy * radius / distance;
        }


        return new MapObjectGenerator(worldIndex, spawnX, spawnY);
    }

    //spawns in the direction of player's next position
    public void aimDirectionSpawn(Player player) {

    }

    //spawns towards next position of the player
    public MapObjectGenerator aimSpawn(Player player) {
        int nextXNum = (int) (player.getNextX() / TILE_SIDE) + 1;
        int nextYNum = (int) (player.getNextY() / TILE_SIDE) + 1;
        return new MapObjectGenerator(worldIndex, nextXNum, nextYNum);
    }

    //spawns the object at the given location with respect to the spawner object
    public MapObjectGenerator targetSpawn(int xNum, int yNum) {
        return new MapObjectGenerator(worldIndex, xNum, yNum);
    }


    //spawns in random places(notices the wall tiles)
    public MapObjectGenerator[] randomSpawn(int range, int spawnNum, GridObject[][][] layers) {
        MapObjectGenerator[] spawned = new MapObjectGenerator[spawnNum];
        int[][] coordinates = getRandomCoordinates(range, spawnNum, layers);
        for (int i = 0; i<spawnNum; i++) {
            int[] coordinate = coordinates[i];
            spawned[i] = new MapObjectGenerator(worldIndex, coordinate[0], coordinate[1]);
        }
        return spawned;
    }

    //returns spawnNum unique coordinate pair, checks for not spawning on solid objects
    private int[][] getRandomCoordinates(int range, int spawnNum, GridObject[][][] layers) {
        ArrayList<int[]> holder = new ArrayList<>();
        int xBound = xNum - range, yBound = yNum - range;
        int SIDE = range*2+2;
        while (holder.size() < spawnNum) {
            int x = xBound + (int) (Math.random()*SIDE);
            int y = yBound + (int) (Math.random()*SIDE);
            boolean valid = true;
            for (int[] held : holder) {
                if (x == held[0] && y == held[1]) {
                    valid = false;
                    break;
                }
            }
            for (GridObject[][] layer : layers) {
                GridObject gridObject = layer[y-1][x-1];
                if (gridObject != null && gridObject.isSolid()) {valid = false;}
            }

            if (valid) {
                holder.add(new int[]{x, y});
            }
        }

        return holder.toArray(new int[0][]);
    }


    public void setCenterCoordinates(double x, double y) {
        centerX = x;
        centerY = y;
    }

    public void setCenterX (double x) {
        centerX = x;
    }

    public void setCenterY (double y) {
        centerY = y;
    }

}

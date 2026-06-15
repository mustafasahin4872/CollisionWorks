package mapobjects.component;

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
    private final int worldIndex, xNum, yNum;

    public Spawner(int worldIndex) {
        this.worldIndex = worldIndex;
        xNum = 0;
        yNum = 0;
        spawnObjects = new HashSet<>();
    }

    public Spawner(GridObject gridObject) {
        this.worldIndex = gridObject.getWorldIndex();
        this.xNum = gridObject.getXNum();
        this.yNum = gridObject.getYNum();
        spawnObjects = new HashSet<>();
    }

    public MapObjectGenerator[] getSpawnObjects() {
        return spawnObjects.toArray(new MapObjectGenerator[0]);
    }


    public void replaceAll(MapObjectGenerator[] mapObjectGenerators) {
        spawnObjects.clear();
        Collections.addAll(spawnObjects, mapObjectGenerators);
    }


    //spawns one tile shifted to the object depending on direction
    //spawns on top if direction is 0
    public MapObjectGenerator summonSpawn(double x, double y, char direction) {
        return switch (direction) {
            case '<' -> new MapObjectGenerator(worldIndex, x-TILE_SIDE, y);
            case '>' -> new MapObjectGenerator(worldIndex, x+TILE_SIDE, y);
            case '^' -> new MapObjectGenerator(worldIndex, x, y-TILE_SIDE);
            case 'v' -> new MapObjectGenerator(worldIndex, x, y+TILE_SIDE);
            case '0' -> new MapObjectGenerator(worldIndex, x, y);
            default -> {
                System.out.println("error in summonSpawn");
                yield null;
            }
        };
    }

    public MapObjectGenerator locationSpawn(double[] location) {
        return new MapObjectGenerator(worldIndex, location[0], location[1]);
    }

    //spawns in the direction of given position, spawns on the circle with given radius
    public MapObjectGenerator directionSpawn(double[] from, double[] towards, double radius) {
        double x = from[0];
        double y = from[1];

        double dx = towards[0] - x;
        double dy = towards[1] - y;

        double distance = Math.sqrt(dx * dx + dy * dy);

        double spawnX;
        double spawnY;
        if (distance == 0) {
            spawnX = x + radius;
            spawnY = y;
        } else {
            spawnX = x + dx * radius / distance;
            spawnY = y + dy * radius / distance;
        }


        return new MapObjectGenerator(worldIndex, spawnX, spawnY);
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


}

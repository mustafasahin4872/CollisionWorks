package mapobjects.framework;

import game.Player;
import helperobjects.Blueprint;

import java.util.ArrayList;

import static mapobjects.framework.GridObject.TILE_SIDE;

//creates and stores blueprints for the linked object. has different spawn styles
public class Spawner {

    private final int spawnNum;
    private final Blueprint[] spawnObjects;
    private final double centerX, centerY;
    private final int worldIndex, xNum, yNum;

    public Spawner(int worldIndex, double x, double y, int spawnNum) {
        this.spawnNum = spawnNum;
        this.worldIndex = worldIndex;
        centerX = x;
        centerY = y;
        xNum = 0;
        yNum = 0;
        spawnObjects = new Blueprint[spawnNum];
    }

    public Spawner(GridObject gridObject) {
        this(gridObject, 1);
    }

    public Spawner(GridObject gridObject, int spawnNum) {
        this.spawnNum = spawnNum;
        this.worldIndex = gridObject.worldIndex;
        this.xNum = gridObject.xNum;
        this.yNum = gridObject.yNum;
        double[] centerCoordinates = gridObject.getCenterCoordinates();
        centerX = centerCoordinates[0];
        centerY = centerCoordinates[1];
        spawnObjects = new Blueprint[spawnNum];
    }


    public Blueprint[] getSpawnObjects() {
        return spawnObjects;
    }

    public void replace(int index, Blueprint blueprint) {
        spawnObjects[index] = blueprint;
    }

    public void replaceAll(Blueprint[] blueprints) {
        System.arraycopy(blueprints, 0, spawnObjects, 0, spawnNum);
    }


    //spawns in the adjacent tile to the object depending on direction
    //spawns on top if direction is 0
    public Blueprint summonSpawn(char direction) {
        return switch (direction) {
            case '<' -> new Blueprint(worldIndex, xNum-1, yNum);
            case '>' -> new Blueprint(worldIndex, xNum+1, yNum);
            case '^' -> new Blueprint(worldIndex, xNum, yNum-1);
            case 'v' -> new Blueprint(worldIndex, xNum, yNum+1);
            case '0' -> new Blueprint(worldIndex, xNum, yNum);
            default -> {
                System.out.println("error in summonSpawn");
                yield null;
            }
        };
    }

    public Blueprint onTopPlayerSpawn(Player player) {
        int[] gridNumbers = player.getGridNumbers();
        return new Blueprint(worldIndex, gridNumbers[0], gridNumbers[1]);
    }

    //spawns in the direction of player's current position
    public void directionSpawn(Player player) {

    }

    //spawns in the direction of player's next position
    public void aimDirectionSpawn(Player player) {

    }

    //spawns towards next position of the player
    public Blueprint aimSpawn(Player player) {
        int nextXNum = (int) (player.getNextX() / TILE_SIDE) + 1;
        int nextYNum = (int) (player.getNextY() / TILE_SIDE) + 1;
        return new Blueprint(worldIndex, nextXNum, nextYNum);
    }

    //spawns the object at the given location with respect to the spawner object
    public Blueprint targetSpawn(int xNum, int yNum) {
        return new Blueprint(worldIndex, xNum, yNum);
    }


    //spawns in random places(notices the wall tiles)
    public Blueprint[] randomSpawn(int range, GridObject[][][] layers) {
        Blueprint[] spawned = new Blueprint[spawnNum];
        int[][] coordinates = getRandomCoordinates(range, layers);
        for (int i = 0; i<spawnNum; i++) {
            int[] coordinate = coordinates[i];
            spawned[i] = new Blueprint(worldIndex, coordinate[0], coordinate[1]);
        }
        return spawned;
    }

    //returns spawnNum unique coordinate pair, checks for not spawning on solid objects
    private int[][] getRandomCoordinates(int range, GridObject[][][] layers) {
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

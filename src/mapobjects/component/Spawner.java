package mapobjects.component;

import game.Player;
import helperobjects.Blueprint;
import mapobjects.category.GridObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static mapobjects.category.GridObject.TILE_SIDE;

//creates and stores blueprints for the linked object. has different spawn styles
public class Spawner {

    private final Set<Blueprint> spawnObjects;
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


    public Blueprint[] getSpawnObjects() {
        return spawnObjects.toArray(new Blueprint[0]);
    }


    public void replaceAll(Blueprint[] blueprints) {
        spawnObjects.clear();
        Collections.addAll(spawnObjects, blueprints);
    }


    //spawns in the adjacent tile to the object depending on direction
    //spawns on top if direction is 0
    public Blueprint summonSpawn(char direction) {
        return switch (direction) {
            case '<' -> new Blueprint(worldIndex, centerX-TILE_SIDE, centerY);
            case '>' -> new Blueprint(worldIndex, centerX+TILE_SIDE, centerY);
            case '^' -> new Blueprint(worldIndex, centerX, centerY-TILE_SIDE);
            case 'v' -> new Blueprint(worldIndex, centerX, centerY+TILE_SIDE);
            case '0' -> new Blueprint(worldIndex, centerX, centerY);
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

    //spawns in the direction of player's current position, spawns on the circle with given radius
        public Blueprint directionSpawn(double[] towards, double radius) {
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


        return new Blueprint(worldIndex, spawnX, spawnY);
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
    public Blueprint[] randomSpawn(int range, int spawnNum, GridObject[][][] layers) {
        Blueprint[] spawned = new Blueprint[spawnNum];
        int[][] coordinates = getRandomCoordinates(range, spawnNum, layers);
        for (int i = 0; i<spawnNum; i++) {
            int[] coordinate = coordinates[i];
            spawned[i] = new Blueprint(worldIndex, coordinate[0], coordinate[1]);
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

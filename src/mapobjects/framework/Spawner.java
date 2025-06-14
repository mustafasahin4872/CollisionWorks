package mapobjects.framework;

import game.Player;
import helperobjects.Blueprint;
import mapobjects.initialized.Tile;

import java.util.ArrayList;

import static mapobjects.framework.GridObject.TILE_SIDE;

//creates and stores blueprints for the linked object. has different spawn styles
public class Spawner {

    private final int spawnNum;
    private final Blueprint[] spawnObjects;
    private final int worldIndex, xNum, yNum;

    public Spawner(GridObject gridObject) {
        this(gridObject, 1);
    }

    public Spawner(GridObject gridObject, int spawnNum) {
        this.spawnNum = spawnNum;
        this.worldIndex = gridObject.worldIndex;
        this.xNum = gridObject.xNum;
        this.yNum = gridObject.yNum;
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
        int xNum = (int)(player.getX()/TILE_SIDE) + 1;
        int yNum = (int)(player.getY()/TILE_SIDE) + 1;
        return new Blueprint(worldIndex, xNum, yNum);
    }

    //spawns in the direction of player's current position
    public void directionSpawn() {

    }

    //spawns towards next position of the player
    public void aimSpawn(Player player) {

    }

    //spawns the object at the given location with respect to the spawner object
    public Blueprint targetSpawn(int xNum, int yNum) {
        return new Blueprint(worldIndex, xNum, yNum);
    }


    //spawns in random places(notices the wall tiles)
    public Blueprint[] randomSpawn(int range, Tile[][] tiles) {
        Blueprint[] spawned = new Blueprint[spawnNum];
        int[][] coordinates = getValidCoordinates(range, tiles);
        for (int i = 0; i<spawnNum; i++) {
            int[] coordinate = coordinates[i];
            spawned[i] = new Blueprint(worldIndex, coordinate[0], coordinate[1]);
        }
        return spawned;
    }


    //returns spawnNum unique coordinate pair, checks for not being impassable as well
    private int[][] getValidCoordinates(int range, Tile[][] tiles) {
        ArrayList<int[]> holder = new ArrayList<>();
        holder.add(new int[]{xNum, yNum});
        holder.add(new int[]{xNum+1, yNum});
        holder.add(new int[]{xNum, yNum+1});
        holder.add(new int[]{xNum+1, yNum+1});
        int xBound = xNum - range, yBound = yNum - range;
        int SIDE = range*2+2;
        while (holder.size()<4+spawnNum) {
            int x = xBound + (int) (Math.random()*SIDE);
            int y = yBound + (int) (Math.random()*SIDE);
            boolean valid = true;
            for (int[] held : holder) {
                if (x == held[0] && y == held[1]) {
                    valid = false;
                    break;
                }
            }
            if (tiles[y-1][x-1].isSolid()) {valid = false;}
            if (valid) {
                holder.add(new int[]{x, y});
            }
        }
        holder.subList(0, 4).clear();

        return holder.toArray(new int[0][]);
    }
}

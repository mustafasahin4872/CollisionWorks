package mapobjects.framework;

import mapobjects.initialized.Tile;

import java.util.ArrayList;

public class SpawnComponent {

    private final int spawnNum;
    private final Blueprint[] spawnObjects;
    private final int worldIndex, xNum, yNum;

    public SpawnComponent(MapObject mapObject) {
        this(mapObject, 1);
    }

    public SpawnComponent(MapObject mapObject, int spawnNum) {
        this.spawnNum = spawnNum;
        this.worldIndex = mapObject.worldIndex;
        this.xNum = mapObject.xNum;
        this.yNum = mapObject.yNum;
        spawnObjects = new Blueprint[spawnNum];
    }

    //spawns object towards the player or on the playerdepending on the object type
    public void aimSpawn() {

    }

    //spawns the object at the given location with respect to the spawner object
    public void targetSpawn(int xNum, int yNum) {

    }

    //spawns in a random place(notices the wall tiles)
    public void randomSpawn(Tile[] tiles, int xTile, int range) {
        int[][] coordinates = getValidCoordinates(tiles, xTile, range);
        for (int i = 0; i<spawnNum; i++) {
            int[] coordinate = coordinates[i];
            spawnObjects[i] = new Blueprint(worldIndex, coordinate[0], coordinate[1]);
        }
    }


    //returns spawnNum unique coordinate pair, checks for not being impassable as well
    private int[][] getValidCoordinates(Tile[] tiles, int xTile, int range) {
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
            int tileIndex = (y-1)*xTile + x - 1;
            if (tiles[tileIndex].isSolid()) {valid = false;}
            if (valid) {
                holder.add(new int[]{x, y});
            }
        }

        holder.subList(0, 4).clear();
        return holder.toArray(new int[0][]);

    }
}

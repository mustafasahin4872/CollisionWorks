package mapobjects.components;

import mapobjects.factories.Blueprint;
import mapobjects.traits.GridObject;
import java.util.ArrayList;
import static mapobjects.traits.GridObject.TILE_SIDE;

//creates and stores blueprints for the linked object. has different spawn styles
public class Spawner {

    private final int worldIndex, xNum, yNum;

    public Spawner(int worldIndex) {
        this.worldIndex = worldIndex;
        xNum = 0;
        yNum = 0;
    }

    public Spawner(GridObject gridObject) {
        this.worldIndex = gridObject.getWorldIndex();
        this.xNum = gridObject.getXNum();
        this.yNum = gridObject.getYNum();
    }


    /// spawns one tile shifted to the object depending on direction
    /// spawns on top if the input direction is 0
    public Blueprint summonSpawn(double x, double y, char direction) {
        return switch (direction) {
            case '<' -> new Blueprint(worldIndex, x-TILE_SIDE, y);
            case '>' -> new Blueprint(worldIndex, x+TILE_SIDE, y);
            case '^' -> new Blueprint(worldIndex, x, y-TILE_SIDE);
            case 'v' -> new Blueprint(worldIndex, x, y+TILE_SIDE);
            case '0' -> new Blueprint(worldIndex, x, y);
            default -> {
                System.out.println("error in summonSpawn");
                yield null;
            }
        };
    }


    /// spawns towards "towards" from "from"
    /// spawns on the circle with given radius
    /// can spawn multiple objects in given cone width
    public Blueprint[] directionSpawn(double[] from, double[] towards, double radius, int spawnNum, double coneWidth) {

        double spreadAngle = (spawnNum > 1) ? coneWidth / (spawnNum-1) : 0;
        Blueprint[] generators = new Blueprint[spawnNum];

        double centerX = from[0];
        double centerY = from[1];
        double dx = towards[0] - centerX;
        double dy = towards[1] - centerY;
        double startAngle = Math.atan2(dy, dx);
        // startAngle is 0 if from and towards are the same position

        if (spawnNum % 2 == 0) startAngle += 0.5 * spreadAngle;

        for (int i = 0; i<spawnNum; i++) {
            double angle;
            int mult = i - spawnNum/2;
            angle = startAngle + Math.toRadians(spreadAngle * mult);

            double x = centerX + Math.cos(angle) * radius;
            double y = centerY + Math.sin(angle) * radius;

            generators[i] = new Blueprint(worldIndex, x, y);
        }

        return generators;

    }

    public Blueprint directionSpawn(double[] from, double[] towards, double radius) {
        return directionSpawn(from, towards, radius, 1, 0)[0];
    }


    /// spawns many generators inside the range
    /// the helper getRandomCoordinates provides the locations
    public Blueprint[] randomSpawn(int range, int spawnNum, GridObject[][][] layers) {
        Blueprint[] spawned = new Blueprint[spawnNum];
        int[][] coordinates = getRandomCoordinates(range, spawnNum, layers);
        for (int i = 0; i<spawnNum; i++) {
            int[] coordinate = coordinates[i];
            spawned[i] = new Blueprint(worldIndex, coordinate[0], coordinate[1]);
        }
        return spawned;
    }

    /// returns spawnNum unique coordinate pair
    /// checks for not spawning on solid objects
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

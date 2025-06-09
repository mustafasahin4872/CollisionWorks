package mapobjects;

import game.Player;

public abstract class RangedComponent extends MapObject {

    protected final int range;
    protected final double[] rangeBox = new double[4];
    protected long startTime;

    public RangedComponent(int worldIndex, int xNum, int yNum, String fileName, int range, boolean recurring, double period, double cooldown){
        super(worldIndex, xNum, yNum, fileName);
        this.range = range;
        rangeBox[0] = coordinates[0] - range*TILE_SIDE;
        rangeBox[2] = coordinates[2] + range*TILE_SIDE;
        rangeBox[1] = coordinates[1] - range*TILE_SIDE;
        rangeBox[3] = coordinates[3] + range*TILE_SIDE;
    }

    protected abstract void playerInRange(Player player);


}

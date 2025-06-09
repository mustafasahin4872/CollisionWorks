package mapobjects;

import game.Player;

public abstract class RangedMapObject extends MapObject {

    protected final int range;
    protected final boolean recurring;
    protected boolean active, complete;
    protected final double[] rangeBox = new double[4];
    protected long startTime;
    protected final double period, cooldown; //in milliseconds

    public RangedMapObject(int worldIndex, int xNum, int yNum, String fileName, int range, boolean recurring, double period, double cooldown){
        super(worldIndex, xNum, yNum, fileName);
        this.range = range;
        this.recurring = recurring;
        this.period = period;
        this.cooldown = cooldown;
        rangeBox[0] = coordinates[0] - range*TILE_SIDE;
        rangeBox[2] = coordinates[2] + range*TILE_SIDE;
        rangeBox[1] = coordinates[1] - range*TILE_SIDE;
        rangeBox[3] = coordinates[3] + range*TILE_SIDE;
    }

    protected void playerInRange(Player player) {
        active = true;
    }

    protected void countDown() {
        if (!complete && active && System.currentTimeMillis()-startTime>period){
            startTime = System.currentTimeMillis();
            complete = true;
        }
        if (recurring && complete && System.currentTimeMillis()-startTime>cooldown) {
            startTime = System.currentTimeMillis();
            complete = false;
        }
    }

}

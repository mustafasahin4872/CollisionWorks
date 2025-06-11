package mapobjects.initialized;

import game.Player;
import lib.StdDraw;
import mapobjects.framework.MapObject;
import mapobjects.framework.RangeComponent;

import static helperobjects.CollisionMethods.playerIsIn;

public class Mine extends MapObject {

    private final RangeComponent rangeComponent;

    private static final double RANGE = 2; //in tiles
    private boolean active, complete;
    private static final double DEFAULT_DAMAGE = 30;
    private final double damage;
    private static final double DEFAULT_FULL_TIME = 3000; //in milliseconds
    private final double fullTime;
    private double timePassed;
    private long startTime;

    public Mine(int worldIndex, int xNum, int yNum) {
        super(worldIndex, xNum, yNum);
        rangeComponent = new RangeComponent(this, RANGE);
        damage = worldIndex* DEFAULT_DAMAGE;
        fullTime = DEFAULT_FULL_TIME/worldIndex;
        collisionBox[0] -= RANGE*TILE_SIDE;
        collisionBox[1] -= RANGE*TILE_SIDE;
        collisionBox[2] += RANGE*TILE_SIDE;
        collisionBox[3] += RANGE*TILE_SIDE;
    }

    public boolean isComplete() {
        return complete;
    }

    @Override
    public void playerIsOn(Player player) {
        if (active) return;
        active = true;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void draw() {
        if (!active || complete) return;
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.filledCircle(centerCoordinates[0], centerCoordinates[1], (timePassed/ fullTime)*HALF_SIDE);
    }

    public void countDown(Player player) {
        if (!active || complete) return;
        timePassed = System.currentTimeMillis()-startTime;
        if (timePassed >= fullTime) {
            complete = true;
            if (playerIsIn(player, coordinates)) {
                player.damage(damage);
            }
        }
    }

}

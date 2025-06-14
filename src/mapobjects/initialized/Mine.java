package mapobjects.initialized;

import game.Player;
import lib.StdDraw;
import mapobjects.framework.*;

public class Mine extends MapObject implements OnEffector, Ranged, Timed {

    private final Box rangeBox;
    private final Box effectBox;
    private final Timer timer;

    private static final double RANGE = 2; //in tiles
    private static final double DEFAULT_DAMAGE = 30;
    private final double damage;
    private static final double DEFAULT_PERIOD = 3000; //in milliseconds

    public Mine(int worldIndex, int xNum, int yNum) {
        super(worldIndex, xNum, yNum);
        rangeBox = new Box(centerCoordinates, RANGE*TILE_SIDE, RANGE*TILE_SIDE);
        effectBox = new Box(this);
        timer = new Timer(DEFAULT_PERIOD / worldIndex, -1);
        damage = worldIndex* DEFAULT_DAMAGE;
    }


    public boolean isActive() {
        return timer.isActive();
    }

    public boolean isComplete() {
        return timer.isCompleted();
    }

    @Override
    public double[] getEffectBox() {
        return effectBox.getBox();
    }


    @Override
    public void call(Player player) {
        updateTimer(); //update timer (might set complete)
        if (!isActive() && !isComplete()) {checkPlayerInRange(player);} //trigger the timer
        if (isComplete()) {timeIsUp(player);} //if set complete, call time is up effect
    }

    //triggers only if time is up!
    @Override
    public void playerIsOn(Player player) {
        player.damage(damage);
    }

    @Override
    public double[] getRangeBox() {
        return rangeBox.getBox();
    }

    @Override
    public void playerInRange(Player player) {
        activateTimer();
    }

    @Override
    public void activateTimer() {
        timer.activate();
    }

    @Override
    public void updateTimer() {
        timer.tick();
    }

    @Override
    public void timeIsUp(Player player) {
        checkPlayerIsOn(player);
        expire();
    }

    @Override
    public void draw() {
        if (timer.isCompleted() || !timer.isActive()) return;
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.filledCircle(centerCoordinates[0], centerCoordinates[1], timer.progressRatio()*HALF_SIDE);
    }

}

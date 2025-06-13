package mapobjects.initialized;

import game.Player;
import lib.StdDraw;
import mapobjects.framework.*;

public class Mine extends MapObject implements Ranged, Timed {

    private final RangeComponent rangeComponent;
    private final TimeComponent timer;

    private static final double RANGE = 2; //in tiles
    private static final double DEFAULT_DAMAGE = 30;
    private final double damage;
    private static final double DEFAULT_PERIOD = 3000; //in milliseconds

    public Mine(int worldIndex, int xNum, int yNum) {
        super(worldIndex, xNum, yNum);
        rangeComponent = new RangeComponent(this, RANGE);
        timer = new TimeComponent(DEFAULT_PERIOD / worldIndex, -1);
        damage = worldIndex* DEFAULT_DAMAGE;
    }


    public boolean isActive() {
        return timer.isActive();
    }

    public boolean isComplete() {
        return timer.isCompleted();
    }


    @Override
    public void call(Player player) {
        if (isComplete()) return; //no need to search if complete
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
        return rangeComponent.getRangeBox();
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
    }

    @Override
    public void draw() {
        if (timer.isCompleted() || !timer.isActive()) return;
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.filledCircle(centerCoordinates[0], centerCoordinates[1], timer.progressRatio()*HALF_SIDE);
    }

}

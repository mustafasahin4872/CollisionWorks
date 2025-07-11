package mapobjects.initialized;

import game.Accessory;
import game.Player;
import lib.StdDraw;
import mapobjects.framework.*;

import java.awt.*;

public class Mine extends GridObject implements OnEffector, Ranged, Timed {

    private final Box rangeBox;
    private final Box effectBox;
    private final Timer timer;

    private static final double RANGE = 6; //in tiles
    private static final double DEFAULT_DAMAGE = 30;
    private final double damage;
    private static final double DEFAULT_PERIOD = 3000; //in milliseconds

    public Mine(int worldIndex, int xNum, int yNum) {
        super(worldIndex, xNum, yNum);
        rangeBox = new Box(getCenterCoordinates(), RANGE*TILE_SIDE, RANGE*TILE_SIDE);
        effectBox = positionBox.clone();
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
    public Box getEffectBox() {
        return effectBox;
    }

    @Override
    public void call(Player player) {
        updateTimer(); //update timer (might set complete)
        if (!isActive() && !isComplete()) {checkPlayerInRange(player);} //trigger the timer
        if (isComplete()) {timeIsUp(player);}
    }

    @Override
    public void checkPlayerIsOn(Player player) {
        if (isComplete()) {
            checkPlayerCornerIsOn(player);
            expire();
        }
    }

    //triggers only if time is up!
    @Override
    public void playerIsOn(Player player) {
        player.damage(damage);
    }

    @Override
    public Box getRangeBox() {
        return rangeBox;
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
        expire(); //this object is set null and never called again
    }

    @Override
    public void draw() {
        if (!timer.isActive()) return;
        StdDraw.setPenRadius();
        StdDraw.setPenColor(new Color(255, 150, 30, 200));
        StdDraw.circle(getX(), getY(), HALF_SIDE);
        StdDraw.setPenColor(new Color(255, 0, 0, 100));
        StdDraw.filledCircle(getX(), getY(), timer.progressRatio()*HALF_SIDE);

    }

}

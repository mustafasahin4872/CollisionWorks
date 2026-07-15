package mapobjects.entities;

import game.io.Drawer.CircleDrawer;
import game.io.Drawer.FilledCircleDrawer;
import mapobjects.components.Box;
import mapobjects.components.Damager;
import mapobjects.components.Timer;
import mapobjects.effects.DamageEffect;
import mapobjects.traits.*;
import mapobjects.traits.effectors.Damaging;
import mapobjects.traits.receivers.HealthBearer;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.schemas.Timed;
import mapobjects.traits.triggerables.Ranged;

import java.awt.*;
import java.util.Set;

public class Mine extends GridObject implements Ranged, Timed, OnEffector, Damaging, Drawable {

    private final Box rangeBox;
    private final Timer timer;
    private final Damager damager;
    private Set<HealthBearer> targets;
    private Set<Moving> triggerers;

    private static final double RANGE = 6; //in tiles
    private static final double DEFAULT_DAMAGE = 30;
    private static final double DEFAULT_PERIOD = 3000; //in milliseconds

    private final CircleDrawer outlineDrawer;
    private final FilledCircleDrawer drawer;

    // TODO: FIX THE DUPLICATE CONSTRUCTORS AND MAKE MINE A MAPOBJECT, NOT A GRIDOBJECT
    public Mine(int worldIndex, int xNum, int yNum) {
        super(worldIndex, xNum, yNum);
        rangeBox = new Box(getCenterCoordinates(), RANGE*TILE_SIDE, RANGE*TILE_SIDE);
        timer = new Timer(DEFAULT_PERIOD / worldIndex, 0, false);
        damager = new Damager(worldIndex* DEFAULT_DAMAGE);

        outlineDrawer = new CircleDrawer(positionBox, HALF_SIDE, new Color(255, 150, 30, 200));
        drawer = new FilledCircleDrawer(positionBox, 0, new Color(255, 0, 0, 100));
    }

    //non grid mine, cannot initialize in mapmaker
    public Mine(int worldIndex, double x, double y) {
        super(worldIndex, 0, 0);
        positionBox.setCenterX(x);
        positionBox.setCenterY(y);
        rangeBox = new Box(x, y, RANGE*TILE_SIDE, RANGE*TILE_SIDE);
        timer = new Timer(DEFAULT_PERIOD / worldIndex, 0, false);
        damager = new Damager(worldIndex* DEFAULT_DAMAGE);

        outlineDrawer = new CircleDrawer(positionBox, HALF_SIDE, new Color(255, 150, 30, 200));
        drawer = new FilledCircleDrawer(positionBox, 0, new Color(255, 0, 0, 100));
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public Damager getDamager() {
        return damager;
    }

    @Override
    public DamageEffect getEffect() {
        return new DamageEffect(damager.getDamage(), damager.getShred());
    }

    @Override
    public void setTargets(Set<HealthBearer> targets) {
        this.targets = targets;
    }

    @Override
    public Set<Moving> getTriggerers() {
        return triggerers;
    }

    @Override
    public void setTriggerers(Set<Moving> triggerers) {
        this.triggerers = triggerers;
    }

    // unused
    @Override
    public FilledCircleDrawer getDrawer() {
        return new FilledCircleDrawer(new Box(0, 0, 0, 0), 0, new Color(0, 0, 0));
    }

    @Override
    public void call(Player player) {
        callTimer(); //update timer (might set complete)
        if (!isActive() && !isComplete()) checkForTriggers(); //trigger the timer
    }

    @Override
    public void checkPlayerIsOn(Player player) {
        if (isComplete()) {
            OnEffector.super.checkPlayerIsOn(player);
            expire();
        }
    }

    //triggers only if time is up!
    @Override
    public void action(Player player) {
        dealDamage(player);
    }

    @Override
    public Box getRangeBox() {
        return rangeBox;
    }

    @Override
    public void action(Moving moving) {
        activateTimer();
    }

    @Override
    public void whenCompleted() {
        checkPlayerIsOn(new Player()); // TODO: ADD TARGET HERE
        expire(); //this object is set null and never called again
    }

    @Override
    public void draw() {
        if (!timer.isActive()) return;
        outlineDrawer.draw();
        //for a more realistic effect, the ratio will be 1 when 85% is completed
        drawer.setRadius(Math.min(timer.progressRatio()*100/85, 1)*HALF_SIDE);
        drawer.draw();
    }

}

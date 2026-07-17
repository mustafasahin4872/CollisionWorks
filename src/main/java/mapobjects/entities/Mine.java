package mapobjects.entities;

import game.io.Drawer.CircleDrawer;
import game.io.Drawer.FilledCircleDrawer;
import mapobjects.components.Box;
import mapobjects.components.Effector;
import mapobjects.components.Timer;
import mapobjects.components.Trigger;
import mapobjects.effects.DamageEffect;
import mapobjects.traits.collisions.Movable;
import mapobjects.traits.senders.Damaging;
import mapobjects.traits.receivers.HealthEffectReceiver;
import mapobjects.traits.receivers.Receiver;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.schemas.Timed;
import mapobjects.traits.senders.Sender;
import mapobjects.traits.triggerables.RangeTriggerable;
import mapobjects.traits.triggerables.MovedOverTriggerable;

import java.awt.*;

public class Mine extends GridObject implements RangeTriggerable, MovedOverTriggerable, Timed, Damaging, Drawable, Sender {

    private final Box rangeBox;
    private final Timer timer;
    private final Effector effector;
    private final Class<? extends HealthEffectReceiver> targetClass = Player.class;
    private final Trigger<Movable> rangeTrigger;
    private final Trigger<Movable> explosionTrigger;

    private static final double RANGE = 6; //in tiles
    private static final double DEFAULT_DAMAGE = 30;
    private static final double DEFAULT_PERIOD = 3000; //in milliseconds

    private final CircleDrawer outlineDrawer;
    private final FilledCircleDrawer drawer;

    // TODO: MAKE MINE A MAPOBJECT, ABLE TO SPAWN WHEREVER
    public Mine(int worldIndex, int xNum, int yNum) {
        super(worldIndex, xNum, yNum);
        rangeBox = new Box(getCenterCoordinates(), RANGE*TILE_SIDE, RANGE*TILE_SIDE);
        timer = new Timer(DEFAULT_PERIOD / worldIndex, 0, false);
        effector = new Effector(new DamageEffect(worldIndex * DEFAULT_DAMAGE, 0));

        rangeTrigger = new Trigger<>(rangeBox, this::triggerRange);
        explosionTrigger = new Trigger<>(positionBox, this::triggerExplosion);

        outlineDrawer = new CircleDrawer(positionBox, HALF_SIDE, new Color(255, 150, 30, 200));
        drawer = new FilledCircleDrawer(positionBox, 0, new Color(255, 0, 0, 100));
    }

    private void triggerRange(Movable movable) {
        if (!isActive() && !isComplete()) {
            activateTimer();
        }
    }

    private void triggerExplosion(Movable movable) {
        if (isComplete() && movable instanceof Receiver r) {
            sendEffect(r);
            expire();
        }
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public Effector getEffector() {
        return effector;
    }

    @Override
    public Class<? extends HealthEffectReceiver> getTargetClass() {
        return targetClass;
    }

    @Override
    public Trigger<Movable> getRangeTrigger() {
        return rangeTrigger;
    }

    @Override
    public Trigger<Movable> getMovedOverTrigger() {
        return explosionTrigger;
    }


    // unused
    @Override
    public FilledCircleDrawer getDrawer() {
        return new FilledCircleDrawer(new Box(0, 0, 0, 0), 0, new Color(0, 0, 0));
    }

    @Override
    public void call() {
        callTimer(); //update timer (might set complete)
    }

    @Override
    public void whenCompleted() {
        Player player = game.core.GameState.gameState.getPlayer();
        if (targetClass.isInstance(player) && helpers.CollisionEngine.intersects(positionBox, player.getPositionBox())) {
            explosionTrigger.whenTriggered(player);
        }
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

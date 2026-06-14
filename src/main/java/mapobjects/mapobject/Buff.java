package mapobjects.mapobject;

import mapobjects.category.GridObject;
import mapobjects.category.OnEffector;
import mapobjects.category.Timed;
import mapobjects.component.Box;
import mapobjects.component.Timer;

public abstract class Buff extends GridObject implements OnEffector, Timed {

    private static final int BUFF_DURATION = 10 * 1000;
    private final boolean permanent;

    private final Box effectBox;
    private final Timer timer;
    private final Timer animationTimer;

    public Buff(int xNum, int yNum, String name, boolean permanent) {
        super(0, xNum, yNum, name);
        this.permanent = permanent;

        effectBox = positionBox.clone();

        if (permanent) timer = null;
        else timer = new Timer(BUFF_DURATION, 0);

        animationTimer = new Timer(3000, -1);
        animationTimer.activate();
    }

    public Buff(int xNum, int yNum, String name) {
        this(xNum, yNum, name,false);
    }

    @Override
    public Box getEffectBox() {
        return effectBox;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public void checkPlayerIsOn(Player player) {
        checkPlayerCornerIsOn(player);
    }

    @Override
    public void playerIsOn(Player player) {
        timer.activate();
    }

    @Override
    public void timeIsUp(Player player) {} // unused


    @Override
    public void draw() {
        if (expired) {
            super.draw();
        } else {
            drawAnimated(animationTimer);
        }
    }


    public static class SpeedBuff extends Buff {

        public SpeedBuff(int xNum, int yNum) {
            super(xNum, yNum, "speedBuff");
        }

        @Override
        public void playerIsOn(Player player) {
            expire();
        }

    }

    public static class ShrinkBuff extends Buff {

        public ShrinkBuff(int xNum, int yNum) {
            super(xNum, yNum, "shrinkBuff");
        }

        @Override
        public void playerIsOn(Player player) {
            expire();
        }

    }

    public static class ShieldBuff extends Buff {

        public ShieldBuff(int xNum, int yNum) {
            super(xNum, yNum, "shieldBuff");
        }

        @Override
        public void playerIsOn(Player player) {
            expire();
        }

    }

    public static class MagnetBuff extends Buff {

        public MagnetBuff(int xNum, int yNum) {
            super(xNum, yNum, "magnetBuff");
        }

        @Override
        public void playerIsOn(Player player) {
            expire();
        }

    }

    public static class VisionBuff extends Buff {

        public VisionBuff(int xNum, int yNum) {
            super(xNum, yNum, "visionBuff");
        }

        @Override
        public void playerIsOn(Player player) {
            expire();
        }

    }

}

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

    public Buff(int xNum, int yNum, boolean permanent) {
        super(0, xNum, yNum);
        this.permanent = permanent;

        effectBox = positionBox.clone();

        if (permanent) timer = null;
        else timer = new Timer(BUFF_DURATION, 0);

        animationTimer = new Timer(3000, -1);
        animationTimer.activate();
    }

    public Buff(int xNum, int yNum) {
        this(xNum, yNum, false);
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
    public void timeIsUp(Player player) {

    }

    @Override
    public void draw() {
        double multiplier;
        double ratio = animationTimer.progressRatio();
        double maxDiff = 0.05;
        if (ratio > 0.5) {
            multiplier = 1 + maxDiff - 2 * maxDiff * ((ratio - 0.5) / 0.5);
        } else {
            multiplier = 1 - maxDiff + 2 * maxDiff * (ratio / 0.5);
        }
        resize(multiplier);
        super.draw();
        resize(1/multiplier);
    }


    public static class SpeedBuff extends Buff {

        public SpeedBuff(int xNum, int yNum) {
            super(xNum, yNum);
        }

        @Override
        public void playerIsOn(Player player) {

        }

    }

    public class ShrinkBuff extends Buff {

        public ShrinkBuff(int xNum, int yNum) {
            super(xNum, yNum);
        }

        @Override
        public void playerIsOn(Player player) {

        }

    }

    public class ShieldBuff extends Buff {

        public ShieldBuff(int xNum, int yNum) {
            super(xNum, yNum);
        }

        @Override
        public void playerIsOn(Player player) {

        }

    }

    public class MagnetBuff extends Buff {

        public MagnetBuff(int xNum, int yNum) {
            super(xNum, yNum);
        }

        @Override
        public void playerIsOn(Player player) {

        }

    }

    public class EagleEyeBuff extends Buff {

        public EagleEyeBuff(int xNum, int yNum) {
            super(xNum, yNum);
        }

        @Override
        public void playerIsOn(Player player) {

        }

    }

}

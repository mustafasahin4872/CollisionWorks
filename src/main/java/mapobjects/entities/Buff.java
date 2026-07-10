package mapobjects.entities;

import helpers.methods.HelperMethods;
import mapobjects.traits.*;
import mapobjects.components.Box;
import mapobjects.components.Timer;

import static mapobjects.traits.GridObject.TILE_SIDE;

public abstract class Buff extends Equippable implements OnEffector, Timed {

    private static final int BUFF_DURATION = 10 * 1000;
    private final boolean permanent;

    private final Box effectBox;
    private final Timer timer;

    public Buff(int xNum, int yNum, String name, boolean permanent) {
        super(0, xNum * TILE_SIDE, yNum * TILE_SIDE, TILE_SIDE, TILE_SIDE, name, RARITY.RARE);
        this.permanent = permanent;

        effectBox = positionBox.clone();

        if (permanent) timer = null;
        else timer = new Timer(BUFF_DURATION, 0);

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
            drawAnimated();
        }
    }

    @Override
    public String[] getStats() {
        String effect = "Temporary Effect";
        if (this instanceof Buff.SpeedBuff) effect = "Increases Speed";
        else if (this instanceof Buff.ShieldBuff) effect = "Invulnerability";
        else if (this instanceof Buff.ShrinkBuff) effect = "Shrinks Hitbox";
        else if (this instanceof Buff.MagnetBuff) effect = "Attracts Items";
        else if (this instanceof Buff.VisionBuff) effect = "Increases Sight";
        return new String[]{
            HelperMethods.capitalize(getName()),
            effect
        };
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

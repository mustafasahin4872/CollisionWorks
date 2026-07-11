package mapobjects.entities;

import helpers.methods.TextMethods;
import helpers.utils.Drawer.PictureDrawer;
import mapobjects.traits.*;
import mapobjects.components.Box;
import mapobjects.components.Timer;

import static mapobjects.traits.GridObject.TILE_SIDE;

public abstract class Buff extends Equippable implements OnEffector, Timed, Drawable {

    private static final int BUFF_DURATION = 10 * 1000;
    private final boolean permanent;
    private static final double DEFAULT_SIDE = TILE_SIDE;

    private final Box effectBox;
    private final Timer timer;

    private final PictureDrawer drawer;

    public Buff(int xNum, int yNum, String name, boolean permanent) {
        super(0, xNum * TILE_SIDE, yNum * TILE_SIDE, DEFAULT_SIDE, DEFAULT_SIDE, name, RARITY.RARE);
        this.permanent = permanent;

        effectBox = positionBox.clone();

        if (permanent) timer = null;
        else timer = new Timer(BUFF_DURATION, 0);

        drawer = new PictureDrawer(positionBox, getDirectory1(), name);
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
    public PictureDrawer getDrawer() {
        return drawer;
    }

    @Override
    public void draw1() {
        if (expired) {
            drawer.draw1();
        } else {
            drawer.drawAnimated1();
        }
    }

    @Override
    public void expire() {
        super.expire();
        setWidth(DEFAULT_SIDE);
        setHeight(DEFAULT_SIDE);
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
            TextMethods.capitalize(drawer.getName()),
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

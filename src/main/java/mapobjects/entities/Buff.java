package mapobjects.entities;

import helpers.TextMethods;
import game.io.Drawer.PictureDrawer;
import mapobjects.components.Timer;
import mapobjects.components.Trigger;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.Equippable;
import mapobjects.traits.triggerables.PlayerOnTriggerable;

import static mapobjects.traits.schemas.GridObject.TILE_SIDE;

public abstract class Buff extends Equippable implements PlayerOnTriggerable, Drawable {

    private static final int BUFF_DURATION = 10 * 1000;
    private final boolean permanent;
    private static final double DEFAULT_SIDE = TILE_SIDE;

    private final Timer timer;
    private final Trigger<Player> playerTrigger;

    private final PictureDrawer drawer;

    public Buff(int xNum, int yNum, String name, boolean permanent) {
        super(0, xNum * TILE_SIDE, yNum * TILE_SIDE, DEFAULT_SIDE, DEFAULT_SIDE, name, RARITY.RARE);
        this.permanent = permanent;

        if (permanent) timer = null;
        else timer = new Timer(BUFF_DURATION, 0, true);

        drawer = new PictureDrawer(positionBox, getDirectory1(), name);
        playerTrigger = new Trigger<>(positionBox, this::triggerBuff);
    }

    public Buff(int xNum, int yNum, String name) {
        this(xNum, yNum, name,false);
    }

    private void triggerBuff(Player player) {
        applyBuff(player);
    }

    protected void applyBuff(Player player) {
        if (timer != null) timer.activate();
    }

    @Override
    public Trigger<Player> getPlayerOnTrigger() {
        return playerTrigger;
    }

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }

    @Override
    public void draw() {
        if (expired) {
            drawer.draw();
        } else {
            drawer.drawAnimated();
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
        protected void applyBuff(Player player) {
            expire();
        }

    }

    public static class ShrinkBuff extends Buff {

        public ShrinkBuff(int xNum, int yNum) {
            super(xNum, yNum, "shrinkBuff");
        }

        @Override
        protected void applyBuff(Player player) {
            expire();
        }

    }

    public static class ShieldBuff extends Buff {

        public ShieldBuff(int xNum, int yNum) {
            super(xNum, yNum, "shieldBuff");
        }

        @Override
        protected void applyBuff(Player player) {
            expire();
        }

    }

    public static class MagnetBuff extends Buff {

        public MagnetBuff(int xNum, int yNum) {
            super(xNum, yNum, "magnetBuff");
        }

        @Override
        protected void applyBuff(Player player) {
            expire();
        }

    }

    public static class VisionBuff extends Buff {

        public VisionBuff(int xNum, int yNum) {
            super(xNum, yNum, "visionBuff");
        }

        @Override
        protected void applyBuff(Player player) {
            expire();
        }

    }

}

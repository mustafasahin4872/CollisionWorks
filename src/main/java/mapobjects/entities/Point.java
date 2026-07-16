package mapobjects.entities;

import game.core.GameState;
import game.core.GameState.STATE;
import game.io.Drawer.PictureDrawer;
import mapobjects.components.Effector;
import mapobjects.components.Trigger;
import mapobjects.effects.SpawnPointEffect;
import mapobjects.effects.StateEffect;
import mapobjects.traits.senders.Sender;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.triggerables.PlayerOnTriggerable;

import java.util.Set;

public abstract class Point extends GridObject implements Drawable, Sender, PlayerOnTriggerable {

    protected final int index;
    protected boolean isBig;
    protected final PictureDrawer drawer;
    private static final double DEFAULT_SIDE = TILE_SIDE;
    private final Trigger<Player> playerTrigger;

    public Point(int worldIndex, int xNum, int yNum, int index, boolean isBig) {
        super(worldIndex, xNum, yNum, getSize(isBig), getSize(isBig), index+"", isBig);
        this.index = index;
        this.isBig = isBig;
        drawer = new PictureDrawer(positionBox, getDirectory1(), index+"");
        playerTrigger = new Trigger<>(positionBox, this::triggerPoint);
    }

    private static int getSize(boolean isBig) {
        return isBig ? 2 : 1;
    }

    private void triggerPoint(Player player) {
        applyPoint(player);
    }

    protected abstract void applyPoint(Player player);

    @Override
    public Trigger<Player> getPlayerOnTrigger() {
        return playerTrigger;
    }

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }

    /// TODO: FIX THIS MESS
    //special override for the selection screen
    @Override
    public void draw() {
        setWidth(DEFAULT_SIDE);
        setHeight(DEFAULT_SIDE);
        drawer.draw();
    }


    public static class WinPoint extends Point {

        private final Effector effector;

        public WinPoint(int worldIndex, int xNum, int yNum, int index) {
            this(worldIndex, xNum, yNum, index, false);
        }

        public WinPoint(int worldIndex, int xNum, int yNum, int index, boolean isBig) {
            super(worldIndex, xNum, yNum, index, isBig);
            STATE state = switch (index) {
                case 0 -> STATE.PASSED;
                case 1 -> STATE.ALTERNATE1;
                case 2 -> STATE.ALTERNATE2;
                case 3 -> STATE.NEXT;
                case 4 -> STATE.SHOP;
                default -> STATE.GAME;
            };
            effector = new Effector(new StateEffect(state));
        }

        @Override
        protected void applyPoint(Player player) {
            sendEffect(player);
        }

        @Override
        public Effector getEffector() {
            return effector;
        }

    }

    public static class CheckPoint extends Point {

        private CheckPoint prev;
        protected boolean visited;
        private static final Sign ERROR_SIGN = new Sign(0, 0, 0, new String[]{"first unlock the previous checkpoint"}, false);
        private static int lastCheckPointIndex;
        private final Effector effector;

        public CheckPoint(int worldIndex, int xNum, int yNum, int index) {
            this(worldIndex, xNum, yNum, index, false);
        }

        public CheckPoint(int worldIndex, int xNum, int yNum, int index, boolean isBig) {
            super(worldIndex, xNum, yNum, index, isBig);
            effector = new Effector(new SpawnPointEffect(getX(), getY()));
        }

        public static void resetLastCheckPointIndex() {
            lastCheckPointIndex = 0;
        }

        @Override
        public void call() {
            if (visited) return;
            ERROR_SIGN.call();
        }

        @Override
        protected void applyPoint(Player player) {

            if (index == lastCheckPointIndex + 1) {
                markVisited();
                sendEffect(player);
            } else if (index > lastCheckPointIndex) {
                ERROR_SIGN.setDisplay();
            }

        }

        @Override
        public Effector getEffector() {
            return effector;
        }

        private void markVisited() {
            visited = true;
            drawer.setName("0");
            if (prev != null) prev.getDrawer().setName("-1");
            lastCheckPointIndex++;
        }

        @Override
        public void draw() {
            super.draw();
            ERROR_SIGN.draw();
        }

        public void setPrev(CheckPoint prev) {
            this.prev = prev;
        }

    }

    public static class SpawnPoint extends CheckPoint {

        public SpawnPoint(int worldIndex, int xNum, int yNum) {
            this(worldIndex, xNum, yNum, false);
        }

        public SpawnPoint(int worldIndex, int xNum, int yNum, boolean isBig) {
            super(worldIndex, xNum, yNum, 0, isBig);
            visited = true;
        }

        @Override
        protected void applyPoint(Player player) {} //no action needed

    }

}

package mapobjects.entities;

import game.io.Drawer.PictureDrawer;
import mapobjects.components.Box;
import mapobjects.traits.*;

public abstract class Tile extends GridObject implements Drawable {

    private final PictureDrawer drawer;
    public Tile(int worldIndex, int xNum, int yNum) {
        super(worldIndex, xNum, yNum, worldIndex+"", "jpg");
        drawer = new PictureDrawer(positionBox, getDirectory1(), worldIndex+"", PictureDrawer.FILE_TYPE.jpg);
    }

    protected void resetPlayerStats(Player player) {
        player.resetDeceleration();
        player.resetAcceleration();
        player.resetMaxSpeed();
    }

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }

    public static abstract class ImpassableTile extends Tile implements Collidable {

        protected final Box collisionBox;
        public ImpassableTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
            collisionBox = positionBox.clone();
        }

        @Override
        public Box getCollisionBox() {
            return collisionBox;
        }

    }

    public static class WallTile extends ImpassableTile {

        public WallTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

    }


    public static class RiverTile extends ImpassableTile {

        public RiverTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

    }


    public static abstract class PassableTile extends Tile implements OnEffector {

        private final Box effectBox;
        public PassableTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
            effectBox = positionBox.clone();
        }

        @Override
        public Box getTriggerBox() {
            return effectBox;
        }

        @Override
        public void checkPlayerIsOn(Player player) {
            if (isCenterOn(player)) playerIsOn(player);
        }

    }


    public static class SpaceTile extends PassableTile{

        public SpaceTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
        }

    }


    public static class SlowTile extends PassableTile {

        public SlowTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum); // Earthy brown
        }

        @Override
        public void playerIsOn(Player player) {
            player.slow();
        }

    }


    public static class SpecialTile extends PassableTile {

        public SpecialTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

        @Override
        public void playerIsOn(Player player) {
            player.slip();
        }

    }


    public static class DamageTile extends PassableTile {

        public DamageTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
            player.damage(worldIndex);
        }

    }


    public static class HealTile extends PassableTile {

        public HealTile(int worldIndex, int xNum, int yNum) { // Golden yellow tile, draw a + in it
            super(worldIndex, xNum, yNum);
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
            player.heal(3.0 / worldIndex);
        }

    }

}

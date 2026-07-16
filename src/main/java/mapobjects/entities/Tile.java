package mapobjects.entities;

import game.io.Drawer.PictureDrawer;
import mapobjects.components.Box;
import mapobjects.components.Effector;
import mapobjects.effects.DamageEffect;
import mapobjects.effects.Effect;
import mapobjects.effects.HealEffect;
import mapobjects.effects.MovementEffect;
import mapobjects.traits.collisions.Collidable;
import mapobjects.traits.senders.Sender;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;

public abstract class Tile extends GridObject implements Drawable {

    private final PictureDrawer drawer;
    public Tile(int worldIndex, int xNum, int yNum) {
        super(worldIndex, xNum, yNum, worldIndex+"", "jpg");
        drawer = new PictureDrawer(positionBox, getDirectory1(), worldIndex+"", PictureDrawer.FILE_TYPE.jpg);
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


    public static abstract class PassableTile extends Tile {

        public PassableTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

    }


    public static class SpaceTile extends PassableTile {

        public SpaceTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

    }


    public static class SlowTile extends PassableTile implements Sender {

        private static final Effector effector = new Effector(new MovementEffect(0.5, 0.12, 2));
        public SlowTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum); // Earthy brown
        }

        @Override
        public Effector getEffector() {
            return effector;
        }

    }


    public static class IceTile extends PassableTile implements Sender {

        private static final Effector effector = new Effector(new MovementEffect(3, 0.12, 0.12));
        public IceTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

        @Override
        public Effector getEffector() {
            return effector;
        }

    }


    public static class DamageTile extends PassableTile implements Sender {

        private static final Effector effector = new Effector(new DamageEffect(2, 0));
        public DamageTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

        @Override
        public Effector getEffector() {
            return effector;
        }

    }


    public static class HealTile extends PassableTile implements Sender {

        public static final Effector effector = new Effector(new HealEffect(4));
        public HealTile(int worldIndex, int xNum, int yNum) { // Golden yellow tile, draw a + in it
            super(worldIndex, xNum, yNum);
        }

        @Override
        public Effector getEffector() {
            return effector;
        }

    }

}

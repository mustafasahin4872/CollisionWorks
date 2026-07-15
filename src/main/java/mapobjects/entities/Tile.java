package mapobjects.entities;

import game.io.Drawer.PictureDrawer;
import mapobjects.components.Box;
import mapobjects.effects.DamageEffect;
import mapobjects.effects.Effect;
import mapobjects.effects.HealEffect;
import mapobjects.effects.MovementEffect;
import mapobjects.traits.*;
import mapobjects.traits.effectors.Effector;
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


    public static class SlowTile extends PassableTile implements Effector {

        public SlowTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum); // Earthy brown
        }

        @Override
        public Effect getEffect() {
            return new MovementEffect(0.5, 0.12, 2);
        }

    }


    public static class SpecialTile extends PassableTile implements Effector {

        public SpecialTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

        @Override
        public Effect getEffect() {
            return new MovementEffect(3, 0.12, 0.12);
        }

    }


    public static class DamageTile extends PassableTile implements Effector {

        public DamageTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum);
        }

        @Override
        public Effect getEffect() {
            return new DamageEffect(worldIndex, 0);
        }

    }


    public static class HealTile extends PassableTile implements Effector {

        public HealTile(int worldIndex, int xNum, int yNum) { // Golden yellow tile, draw a + in it
            super(worldIndex, xNum, yNum);
        }

        @Override
        public Effect getEffect() {
            return new HealEffect(4);
        }

    }

}

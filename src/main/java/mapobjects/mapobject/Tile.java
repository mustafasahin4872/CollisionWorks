package mapobjects.mapobject;

import game.Player;
import mapobjects.component.Box;
import mapobjects.category.*;

public abstract class Tile extends GridObject {

    private static final String ROOT = "src/main/resources/tileImages/";

    public Tile(int worldIndex, int xNum, int yNum, String type) {
        super(worldIndex, xNum, yNum, ROOT + type + worldIndex + ".jpg");
    }

    protected void resetPlayerStats(Player player) {
        player.resetDeceleration();
        player.resetAcceleration();
        player.resetMaxSpeed();
    }


    public static abstract class ImpassableTile extends Tile implements Collidable {

        protected final Box collisionBox;
        public ImpassableTile(int worldIndex, int xNum, int yNum, String type) {
            super(worldIndex, xNum, yNum, type);
            collisionBox = positionBox.clone();
        }

        @Override
        public Box getCollisionBox() {
            return collisionBox;
        }

    }

    public static class WallTile extends ImpassableTile {

        public WallTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, "WallTile");
        }

    }


    public static class RiverTile extends ImpassableTile {

        public RiverTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, "RiverTile");
        }

    }


    public static abstract class PassableTile extends Tile implements OnEffector {

        private final Box effectBox;
        public PassableTile(int worldIndex, int xNum, int yNum, String type) {
            super(worldIndex, xNum, yNum, type);
            effectBox = positionBox.clone();
        }

        @Override
        public Box getEffectBox() {
            return effectBox;
        }

        @Override
        public void checkPlayerIsOn(Player player) {
            checkPlayerCenterIsOn(player);
        }

    }


    public static class SpaceTile extends PassableTile{

        public SpaceTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, "SpaceTile");
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
        }

    }


    public static class SlowTile extends PassableTile {

        public SlowTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, "SlowTile"); // Earthy brown
        }

        @Override
        public void playerIsOn(Player player) {
            player.slow();
        }

    }


    public static class SpecialTile extends PassableTile {

        public SpecialTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, "SpecialTile");
        }

        @Override
        public void playerIsOn(Player player) {
            player.slip();
        }

    }


    public static class DamageTile extends PassableTile {

        public DamageTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, "DamageTile");
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
            player.damage(worldIndex);
        }

        @Override
        public void checkPlayerIsOn(Player player) {
            checkPlayerCornerIsOn(player);
        }

    }


    public static class HealTile extends PassableTile {

        public HealTile(int worldIndex, int xNum, int yNum) { // Golden yellow tile, draw a + in it
            super(worldIndex, xNum, yNum, "HealTile");
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
            player.heal(3.0 / worldIndex);
        }

        @Override
        public void checkPlayerIsOn(Player player) {
            checkPlayerCornerIsOn(player);
        }

    }

}

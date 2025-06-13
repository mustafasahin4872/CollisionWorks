package mapobjects.initialized;

import game.Player;
import mapobjects.framework.*;

import static helperobjects.CollisionMethods.*;

public abstract class Tile extends MapObject {

    private static final String ROOT = "misc/tileImages/";
    private final boolean isSolid, isApproachable;

    public Tile(int worldIndex, int xNum, int yNum, boolean isApproachable, boolean isSolid, String type) {
        super(worldIndex, xNum, yNum, ROOT + type + worldIndex + ".jpg");
        this.isApproachable = isApproachable;
        this.isSolid = isSolid;
    }


    public boolean isSolid() {
        return isSolid;
    }

    public boolean isApproachable() {
        return isApproachable;
    }

    protected void resetPlayerStats(Player player) {
        player.resetDeceleration();
        player.resetAcceleration();
        player.resetMaxSpeed();
    }


    public static abstract class ImpassableTile extends Tile implements Collidable {

        protected final CollisionBox collisionBox;
        public ImpassableTile(int worldIndex, int xNum, int yNum, boolean isApproachable, String type) {
            super(worldIndex, xNum, yNum, isApproachable, true, type);
            collisionBox = new CollisionBox(this);
        }

        @Override
        public double[] getCollisionBox() {
            return collisionBox.getCollisionBox();
        }

    }

    public static class WallTile extends ImpassableTile {

        public WallTile(int worldIndex, int xNum, int yNum, boolean isApproachable) {
            super(worldIndex, xNum, yNum, isApproachable, "WallTile");
        }

        //check for collision instead of on tile effect
        @Override
        public void call(Player player) {
            if (isApproachable()) checkCollision(player);
        }

    }


    public static class RiverTile extends ImpassableTile {

        public RiverTile(int worldIndex, int xNum, int yNum, boolean isApproachable) {
            super(worldIndex, xNum, yNum, isApproachable, "RiverTile");
        }

        //check for collision instead of on tile effect
        @Override
        public void call(Player player) {
            if (isApproachable()) checkCollision(player);
        }

    }


    public static abstract class PassableTile extends Tile implements Effector {

        private EffectBox effectBox;
        public PassableTile(int worldIndex, int xNum, int yNum, String type) {
            super(worldIndex, xNum, yNum, true, false, type);
            effectBox = new EffectBox(this);
        }

        @Override
        public double[] getEffectBox() {
            return effectBox.getEffectBox();
        }

    }


    public static class SpaceTile extends PassableTile{

        public SpaceTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, "SpaceTile");
        }

        //check for player center instead of corners and sides
        @Override
        public void call(Player player) {
            checkPlayerCenterIsOn(player);
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

        //check for player center instead of corners and sides
        @Override
        public void call(Player player) {
            checkPlayerCenterIsOn(player);
        }

        @Override
        public void playerIsOn(Player player) {
            player.slow();
        }

    }


    public static class SpecialTile extends PassableTile {

        public SpecialTile(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, "SpecialTile"); // Icy blue
        }

        //check for player center instead of corners and sides
        @Override
        public void call(Player player) {
            checkPlayerCenterIsOn(player);
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

        //check for player center instead of corners and sides
        @Override
        public void call(Player player) {
            checkPlayerCenterIsOn(player);
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
            player.damage(worldIndex);
        }

    }


    public static class HealTile extends PassableTile {

        public HealTile(int worldIndex, int xNum, int yNum) { // Golden yellow tile, draw a + in it
            super(worldIndex, xNum, yNum, "HealTile");
        }

        //check for player center instead of corners and sides
        @Override
        public void call(Player player) {
            checkPlayerCenterIsOn(player);
        }

        @Override
        public void playerIsOn(Player player) {
            resetPlayerStats(player);
            player.heal(3.0 / worldIndex);
        }

    }

}

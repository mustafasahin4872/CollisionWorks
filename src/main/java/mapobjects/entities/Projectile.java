package mapobjects.entities;

import game.io.Frame;
import game.io.Drawer.PictureDrawer;
import mapobjects.components.Box;
import mapobjects.components.Direction;
import mapobjects.components.Effector;
import mapobjects.effects.DamageEffect;
import mapobjects.traits.collisions.Collidable;
import mapobjects.traits.collisions.MovingCollidable;
import mapobjects.traits.receivers.Receiver;
import mapobjects.traits.schemas.Damaging;
import mapobjects.traits.schemas.HealthBearer;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.schemas.MapObject;
import mapobjects.traits.senders.Sender;

import java.util.Set;

import static mapobjects.traits.schemas.GridObject.TILE_SIDE;

//projectiles are not grid objects, they are bullets that crash to collidables and pass through water and other tiles
public class Projectile extends MapObject implements MovingCollidable, Damaging, Drawable, Sender {

    public enum ProjectileType {
        REGULAR(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_SPEED, DEFAULT_DAMAGE, 7, false, 0, 0),
        HOMING(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_SPEED/3, DEFAULT_DAMAGE, 5, false, 0.1, 0),
        SMALL(DEFAULT_WIDTH/2, DEFAULT_HEIGHT/2, DEFAULT_SPEED * 2, DEFAULT_DAMAGE / 2, 5, false, 0, 0),
        BIG(1.5 * DEFAULT_WIDTH, 1.5 * DEFAULT_HEIGHT, DEFAULT_SPEED/2, DEFAULT_DAMAGE * 2, 5, false, 0, 0),
        SHOTGUN(1.8 * DEFAULT_WIDTH, 1.8 * DEFAULT_HEIGHT, DEFAULT_SPEED/2, DEFAULT_DAMAGE * 3, 2, false, 0, 0),
        LAUNCHER(3 * DEFAULT_WIDTH, 3 * DEFAULT_HEIGHT, DEFAULT_SPEED/4, DEFAULT_DAMAGE*8, 8, false, 0, 0)
        ;
        private final double width;
        private final double height;
        private final double speed;
        private final double damage;
        private final double range;
        public final boolean piercing;
        private final double inertia;
        private final double bounceFactor;

        ProjectileType(double width, double height, double speed, double damage, double range, boolean piercing, double inertia, double bounceFactor) {
            this.width = width;
            this.height = height;
            this.speed = speed;
            this.damage = damage;
            this.range = range * TILE_SIDE;
            this.piercing = piercing;
            this.inertia = inertia;
            this.bounceFactor = bounceFactor;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public double getSpeed() {
            return speed;
        }

        public double getDamage() {
            return damage;
        }

        public double getRange() {
            return range;
        }

        public double getInertia() {
            return inertia;
        }

        public boolean isPiercing() {
            return piercing;
        }

        public double getBounceFactor() {
            return bounceFactor;
        }

    }

    private final Box collisionBox;
    boolean xCollided, yCollided;
    private Set<HealthBearer> targets = Set.of();
    private final Effector effector;

    protected final Direction direction;
    private final double speed;
    private final double range;
    private final boolean piercing; // TODO: ADD PIERCING LOGIC
    private final double inertia;
    private final double bounceFactor; // TODO: ADD BOUNCING LOGIC

    private double totalDistance = 0;

    public static final double DEFAULT_SPEED = 20;
    public static final double DEFAULT_WIDTH = 40;
    public static final double DEFAULT_HEIGHT = 20;
    public static final double DEFAULT_DAMAGE = 20;

    private final PictureDrawer drawer;

    /// accept direction in degrees
    public Projectile(int worldIndex, double x, double y, double width, double height, String name, double direction, double range, double damage, double speed, double inertia, double bounceFactor, boolean piercing) {
        super(worldIndex, x, y, width, height);
        this.direction = new Direction(direction);
        this.speed = speed;
        this.range = range;
        this.inertia = inertia;
        this.bounceFactor = bounceFactor;
        this.piercing = piercing;

        collisionBox = new Box(x, y, width, height);
        effector = new Effector(new DamageEffect(damage, 0));

        drawer = new PictureDrawer(positionBox, getDirectory1(), name);

    }

    public void call(Player player, GridObject[][][] layers) {

        if (inertia != 0) {
            direction.rotateTowards(getCenterCoordinates(), player.getCenterCoordinates(), inertia);
        }

        if (totalDistance >= range) expire();
        move();
        boolean collided = false;

        int[] gridNumbers = getGridNumbers();

        int checkRange = 2; //the checking checkRange

        if (targets.contains(player)) {
            player.checkCollision(this);
            if (xCollided || yCollided) {
                sendEffect(player);
                collided = true;
                expire();
            }
        }

        for (GridObject[][] layer : layers) {
            if (collided) break;
            for (int i = gridNumbers[1]-checkRange; i<gridNumbers[1]+checkRange; i++) {
                if (collided) break;
                for (int j = gridNumbers[0]-checkRange; j<gridNumbers[0]+checkRange; j++) {
                    if (i<0 || j<0 || i>=layer.length || j>=layer[0].length) continue;
                    GridObject currentGridObject = layer[i][j];
                    if (currentGridObject instanceof Collidable c) {
                        c.checkCollision(this);
                        if (xCollided || yCollided) {
                            collided = true;
                            if (c instanceof Receiver r && c instanceof HealthBearer h && targets.contains(h)) {
                                sendEffect(r);
                            }
                            expire();
                            break;
                        }
                    }
                }
            }
        }

    }

    private void move() {
        double deltaX = getXVelocity()*Frame.DT;
        double deltaY = getYVelocity()*Frame.DT;
        positionBox.xShift(deltaX);
        positionBox.yShift(deltaY);
        collisionBox.xShift(deltaX);
        collisionBox.yShift(deltaY);
        totalDistance += Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Override
    public boolean isXCollided() {
        return xCollided;
    }

    @Override
    public boolean isYCollided() {
        return yCollided;
    }

    @Override
    public void xCollide() {
        xCollided = true;
    }

    @Override
    public void yCollide() {
        yCollided = true;
    }

    public void rotate(double[] towards) {
        direction.rotateTowards(getCenterCoordinates(), towards, 0);
    }

    @Override
    public double getXVelocity() {
        return speed*direction.getXComponent();
    }

    @Override
    public double getYVelocity() {
        return speed*direction.getYComponent();
    }

    @Override
    public Box getCollisionBox() {
        return collisionBox;
    }

    @Override
    public Effector getEffector() {
        return effector;
    }

    @Override
    public void setTargets(Set<HealthBearer> targets) {
        this.targets = targets;
    }

    // unused
    @Override
    public PictureDrawer getDrawer() {
        return new PictureDrawer(new Box(0, 0, 0, 0), "");
    }

    @Override
    public void draw() {
        drawer.setDegrees(direction.getDegreeDirection());
        drawer.draw();
    }

}

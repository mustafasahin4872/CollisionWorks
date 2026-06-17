package mapobjects.mapobject;

import game.Frame;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.component.Damager;
import mapobjects.component.Direction;
import mapobjects.category.*;

import java.util.Locale;
import java.util.Set;

import static mapobjects.category.GridObject.TILE_SIDE;

//projectiles are not grid objects, they are bullets that crash to collidables and pass through water and other tiles
public class Projectile extends MapObject implements MovingCollidable, Damaging {

    public enum ProjectileType {
        REGULAR(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_SPEED, DEFAULT_DAMAGE, 7, false, 0, 0),
        HOMING(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_SPEED/3, DEFAULT_DAMAGE, 5, false, 0.1, 0),
        SMALL(DEFAULT_WIDTH/2, DEFAULT_HEIGHT/2, DEFAULT_SPEED * 2, DEFAULT_DAMAGE / 2, 5, false, 0, 0),
        BIG(1.5 * DEFAULT_WIDTH, 1.5 * DEFAULT_HEIGHT, DEFAULT_SPEED/2, DEFAULT_DAMAGE * 2, 5, false, 0, 0),
        SHOTGUN(1.8 * DEFAULT_WIDTH, 1.8 * DEFAULT_HEIGHT, DEFAULT_SPEED/2, DEFAULT_DAMAGE * 3, 2, false, 0, 0),
        ;
        private final double width;
        private final double height;
        private final double speed;
        private final double damage;
        private final double range;
        private final boolean piercing;
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

        public double getBounceFactor() {
            return bounceFactor;
        }

    }

    public static class ProjectileBlueprint {
        private final String name;
        private double width;
        private double height;
        private double speed;
        private double damage;
        private double range;
        private boolean piercing;
        private double inertia;
        private double bounceFactor;

        public ProjectileBlueprint(ProjectileType projectileType) {
            this.name = projectileType.name().toLowerCase(Locale.ROOT);
            this.width = projectileType.width;
            this.height = projectileType.height;
            this.speed = projectileType.speed;
            this.damage = projectileType.damage;
            this.range = projectileType.range;
            this.piercing = projectileType.piercing;
            this.inertia = projectileType.inertia;
            this.bounceFactor = projectileType.bounceFactor;
        }

        public Projectile createProjectile(int worldIndex, double x, double y, double direction) {
            return new Projectile(worldIndex, x, y, width, height, name, direction, range, damage, speed, inertia, bounceFactor, piercing);
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public void setHeight(double height) {
            this.height = height;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public void setDamage(double damage) {
            this.damage = damage;
        }

        public void setRange(double range) {
            this.range = range;
        }

        public void setInertia(double inertia) {
            this.inertia = inertia;
        }

        public void setBounceFactor(double bounceFactor) {
            this.bounceFactor = bounceFactor;
        }

        public void addPiercing() {
            piercing = true;
        }

    }

    private final Box collisionBox;
    private final Damager damager;
    boolean xCollided, yCollided;
    private Set<HealthBearer> targets = Set.of();

    protected final Direction direction;
    private final double speed;
    private final double range;
    private final boolean piercing; // TODO: ADD PIERCING LOGIC
    private final double inertia;
    private final double bounceFactor; // TODO: ADD BOUNCING LOGIC

    private double totalDistanceSqared;

    public static final double DEFAULT_SPEED = 20;
    public static final double DEFAULT_WIDTH = 40;
    public static final double DEFAULT_HEIGHT = 20;
    public static final double DEFAULT_DAMAGE = 20;

    /// accept direction in degrees
    public Projectile(int worldIndex, double x, double y, double width, double height, String name, double direction, double range, double damage, double speed, double inertia, double bounceFactor, boolean piercing) {
        super(worldIndex, x, y, width, height);
        setName(name);
        this.direction = new Direction(direction);
        this.speed = speed;
        this.range = range;
        this.inertia = inertia;
        this.bounceFactor = bounceFactor;
        this.piercing = piercing;

        collisionBox = new Box(x, y, width, height);
        damager = new Damager(damage);

    }

    public void call(Player player, GridObject[][][] layers) {

        if (inertia != 0) {
            direction.rotateTowards(getCenterCoordinates(), player.getCenterCoordinates(), inertia);
        }

        if (totalDistanceSqared >= range*range) expire();
        move();
        boolean collided = false;

        int[] gridNumbers = getGridNumbers();

        int checkRange = 2; //the checking checkRange

        player.checkCollision(this);
        if (xCollided || yCollided) {
            dealDamage(player);
            collided = true;
            expire();
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
                            if (c instanceof HealthBearer h && targets.contains(h)) dealDamage(h);
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
        totalDistanceSqared += deltaX * deltaX + deltaY * deltaY;
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
    public Damager getDamager() {
        return damager;
    }

    @Override
    public void setTargets(Set<HealthBearer> targets) {
        this.targets = targets;
    }

    @Override
    public Set<HealthBearer> getTargets() {
        return targets;
    }

    @Override
    public void draw() {
        StdDraw.picture(getX(), getY(), imageFileName, getWidth(), getHeight(), direction.getDegreeDirection());
    }

}

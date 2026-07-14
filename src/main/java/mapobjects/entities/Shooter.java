package mapobjects.entities;

import game.io.Frame;
import game.io.Drawer.PictureDrawer;
import mapobjects.effects.DamageEffect;
import mapobjects.effects.Effect;
import mapobjects.factories.Blueprint;
import mapobjects.components.*;
import mapobjects.traits.*;
import mapobjects.components.Spawner;

import java.util.HashSet;
import java.util.Set;

import static game.core.GameMap.outOfMapBounds;
import mapobjects.entities.Projectile.ProjectileType;
import mapobjects.factories.ProjectileBlueprint;

public abstract class Shooter extends GridObject implements Collidable, Timed, Generator, HealthBearer, Drawable {

    protected static final char VERTICAL = '|', HORIZONTAL = '—',
            RIGHT = '>', LEFT = '<', UP = '^', DOWN = 'v';

    protected final ProjectileBlueprint projectileBlueprint;
    protected final Box collisionBox;
    protected final Timer timer;
    protected final Spawner spawner;
    private final HPBar hpBar;
    protected final char type;
    protected Set<MapObject> spawnedObjects;
    protected static final double DEFAULT_COOLDOWN = 3000; // in milliseconds
    protected final GridObject[][][] layers;
    protected boolean broken;
    protected Set<HealthBearer> targets;
    private final Inbox inbox;
    protected boolean readyToSpawn;

    private final PictureDrawer drawer;

    public Shooter(int worldIndex, int xNum, int yNum, char type, ProjectileType projectileType,
            GridObject[][][] layers) {
        super(worldIndex, xNum, yNum);
        this.type = type;
        this.layers = layers;
        this.projectileBlueprint = new ProjectileBlueprint(projectileType);
        collisionBox = positionBox.clone();
        timer = new Timer(Long.MAX_VALUE, DEFAULT_COOLDOWN / worldIndex, true);
        spawner = new Spawner(this);
        hpBar = new HPBar(50);

        drawer = new PictureDrawer(positionBox, getDirectory1());
        inbox = new Inbox();
    }

    @Override
    public void setSpawnedObjects(Set<MapObject> spawnedObjects) {
        this.spawnedObjects = spawnedObjects;
    }

    @Override
    public Box getCollisionBox() {
        return collisionBox;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public HPBar getHealthBar() {
        return hpBar;
    }

    @Override
    public void ifDied() {}

    @Override
    public void ifNoLivesLeft() {
        broken = true;
        drawer.setName("1");
    }

    public void setTargets(Set<HealthBearer> targets) {
        this.targets = targets;
    }

    @Override
    public void call(Player player) {
        checkDead();
        if (broken) return;
        callTimer();
        if (readyToSpawn) {
            spawn();
            readyToSpawn = false;
            startCooldown();
        }
    }

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }

    @Override
    public void draw() {
        drawer.draw();
        hpBar.drawHPBar(this);
    }

    @Override
    public void spawn() {
        Blueprint blueprint;
        blueprint = spawner.summonSpawn(getX(), getY(), type);
        int d = switch (type) {
            case RIGHT -> 0;
            case UP -> 270;
            case LEFT -> 180;
            case DOWN -> 90;
            default -> {
                System.out.println("error in direction conversion to integer in shooter");
                yield 0;
            }
        };
        Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, d);
        projectile.setTargets(targets);
        spawnedObjects.add(projectile);
    }

    @Override
    public Inbox getInbox() {
        return inbox;
    }

    @Override
    public void processEffects() {

        double totalDamage = 0;
        double totalShred = 0;

        for (Effect effect : inbox.getEffects()) {
            if (effect instanceof DamageEffect(double damage, double shred)) {
                totalDamage += damage;
                totalShred += shred;
            }
        }

        hpBar.takeDamage(totalDamage, totalShred);

    }

    // shoots periodically
    public static class RegularShooter extends Shooter {
        public RegularShooter(int worldIndex, int xNum, int yNum, char type, GridObject[][][] layers) {
            super(worldIndex, xNum, yNum, type, ProjectileType.REGULAR, layers);
            activateTimer();
        }

        @Override
        public void whenActive() {
            readyToSpawn = true;
        }
    }

    // shoots only if triggered and not in cooldown
    public static abstract class RangedShooter extends Shooter implements Ranged {

        private final Box rangeBox;
        private Set<Moving> triggerers;
        private static final double RANGE = 6.5;
        private final Set<Moving> targetsInRange = new HashSet<>();

        public RangedShooter(int worldIndex, int xNum, int yNum, char type, ProjectileType projectileType, GridObject[][][] layers) {
            super(worldIndex, xNum, yNum, type, projectileType, layers);
            rangeBox = new Box(positionBox.getCenterCoordinates(), RANGE * TILE_SIDE * 2, RANGE * TILE_SIDE * 2);
        }

        @Override
        public Box getRangeBox() {
            return rangeBox;
        }

        @Override
        public Set<Moving> getTriggerers() {
            return triggerers;
        }

        @Override
        public void setTriggerers(Set<Moving> triggerers) {
            this.triggerers = triggerers;
        }

        @Override
        public void action(Moving moving) {
            targetsInRange.add(moving);
            readyToSpawn = true;
        }

        @Override
        public void call(Player player) {
            super.call(player);
            if (!inCooldown()) checkForTriggers();
        }

        @Override
        public void spawn() {
            Moving closest = new Player();
            double leastDistanceSquared = Double.MAX_VALUE;
            for (Moving moving : targetsInRange) {
                double dx = moving.getX() - getX();
                double dy = moving.getY() - getY();
                double distSq = dx * dx + dy * dy;
                if (distSq < leastDistanceSquared) {
                    leastDistanceSquared = distSq;
                    closest = moving;
                }
            }

            double[] target = new double[]{closest.getX(), closest.getY()};
            Blueprint blueprint = spawner.directionSpawn(getCenterCoordinates(), target, 50);
            Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, 0);
            projectile.rotate(target);
            projectile.setTargets(targets);
            spawnedObjects.add(projectile);

        }

    }


    public static class DirectionShooter extends RangedShooter {

        public DirectionShooter(int worldIndex, int xNum, int yNum, GridObject[][][] layers, Player player) {
            super(worldIndex, xNum, yNum, 'x', ProjectileType.REGULAR, layers);
        }

    }

    public static class HomingShooter extends RangedShooter {

        public HomingShooter(int worldIndex, int xNum, int yNum, GridObject[][][] layers, Player player) {
            super(worldIndex, xNum, yNum, 'h', ProjectileType.HOMING, layers);
        }

    }

    public static class MovingShooter extends RegularShooter implements MovingCollidable {

        private static final double SPEED = -1;
        private boolean xCollided, yCollided;
        private double xVelocity, yVelocity;
        private final char alignment;

        public MovingShooter(int worldIndex, int xNum, int yNum, char type, char alignment, GridObject[][][] layers) {
            super(worldIndex, xNum, yNum, type, layers);
            this.alignment = alignment;
            if (alignment == HORIZONTAL) {
                xVelocity = SPEED;
            } else {
                yVelocity = SPEED;
            }
        }

        @Override
        public void call(Player player) {
            checkCollision(player);
            player.checkCollision(this);

            super.call(player);

            checkDead();
            if (broken) return;

            int[] gridNumbers = getGridNumbers();
            int range = 2; // the checking range
            boolean collided = false;
            for (GridObject[][] layer : layers) {
                if (collided)
                    break;
                for (int i = gridNumbers[1] - range; i < gridNumbers[1] + range; i++) {
                    if (collided)
                        break;
                    for (int j = gridNumbers[0] - range; j < gridNumbers[0] + range; j++) {
                        if (alignment == HORIZONTAL && xCollided) {
                            collided = true;
                            xVelocity *= -1;
                            xCollided = false;
                            break;
                        }
                        if (alignment == VERTICAL && yCollided) {
                            collided = true;
                            yVelocity *= -1;
                            yCollided = false;
                            break;
                        }
                        if (outOfMapBounds(layer, i, j))
                            continue;

                        GridObject currentGridObject = layer[i][j];
                        if (currentGridObject == this)
                            continue;
                        if (currentGridObject instanceof Collidable c) {
                            c.checkCollision(this);
                        }
                    }
                }
            }
            move();
        }

        public void move() {
            positionBox.xShift(xVelocity * Frame.DT);
            positionBox.yShift(yVelocity * Frame.DT);
            collisionBox.xShift(xVelocity * Frame.DT);
            collisionBox.yShift(yVelocity * Frame.DT);
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

        @Override
        public double getXVelocity() {
            return xVelocity;
        }

        @Override
        public double getYVelocity() {
            return yVelocity;
        }

        @Override
        public void setX(double x) {
            super.setX(x);
            collisionBox.setCenterX(x);
        }

        @Override
        public void setY(double y) {
            super.setY(y);
            collisionBox.setCenterY(y);
        }
    }

}

package mapobjects.entities;

import game.io.Frame;
import game.io.Drawer.PictureDrawer;
import mapobjects.factories.Blueprint;
import mapobjects.components.*;
import mapobjects.components.Spawner;

import java.util.HashSet;
import java.util.Set;

import mapobjects.entities.Projectile.ProjectileType;
import mapobjects.factories.ProjectileBlueprint;
import mapobjects.traits.collisions.Collidable;
import mapobjects.traits.collisions.Movable;
import mapobjects.traits.collisions.MovingCollidable;
import mapobjects.traits.receivers.Enemy;
import mapobjects.traits.receivers.HealthEffectReceiver;
import mapobjects.traits.receivers.Receiver;
import mapobjects.traits.schemas.*;
import mapobjects.traits.triggerables.RangeTriggerable;

public abstract class Shooter extends GridObject implements Collidable, Timed, Generator, HealthEffectReceiver, Drawable, Receiver, Enemy {

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
    protected boolean broken;
    protected Class<? extends HealthEffectReceiver> targetClass = Player.class;
    private final Inbox inbox;
    protected boolean readyToSpawn;

    private final PictureDrawer drawer;

    public Shooter(int worldIndex, int xNum, int yNum, char type, ProjectileType projectileType) {
        super(worldIndex, xNum, yNum);
        this.type = type;
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


    @Override
    public void call() {
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
        projectile.setTargetClass(targetClass);
        spawnedObjects.add(projectile);
    }

    @Override
    public Inbox getInbox() {
        return inbox;
    }

    // shoots periodically
    public static class RegularShooter extends Shooter {
        public RegularShooter(int worldIndex, int xNum, int yNum, char type) {
            super(worldIndex, xNum, yNum, type, ProjectileType.REGULAR);
            activateTimer();
        }

        @Override
        public void whenActive() {
            readyToSpawn = true;
        }
    }

    // shoots only if triggered and not in cooldown
    public static abstract class RangeTriggerableShooter extends Shooter implements RangeTriggerable {

        private final Box rangeBox;
        private final Trigger<Movable> rangeTrigger;
        private static final double RANGE = 6.5;
        private final Set<Movable> targetsInRange = new HashSet<>();

        public RangeTriggerableShooter(int worldIndex, int xNum, int yNum, char type, ProjectileType projectileType) {
            super(worldIndex, xNum, yNum, type, projectileType);
            rangeBox = new Box(positionBox.getCenterCoordinates(), RANGE * TILE_SIDE * 2, RANGE * TILE_SIDE * 2);
            rangeTrigger = new Trigger<>(rangeBox, this::triggerShooter);
        }

        private void triggerShooter(Movable movable) {
            targetsInRange.add(movable);
            readyToSpawn = true;
        }

        @Override
        public Trigger<Movable> getRangeTrigger() {
            return rangeTrigger;
        }



        @Override
        public void spawn() {
            Movable closest = new Player();
            double leastDistanceSquared = Double.MAX_VALUE;
            for (Movable movable : targetsInRange) {
                double dx = movable.getX() - getX();
                double dy = movable.getY() - getY();
                double distSq = dx * dx + dy * dy;
                if (distSq < leastDistanceSquared) {
                    leastDistanceSquared = distSq;
                    closest = movable;
                }
            }

            double[] target = new double[]{closest.getX(), closest.getY()};
            Blueprint blueprint = spawner.directionSpawn(getCenterCoordinates(), target, 50);
            Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, 0);
            projectile.rotate(target);
            projectile.setTargetClass(targetClass);
            spawnedObjects.add(projectile);

        }

    }


    public static class DirectionShooter extends RangeTriggerableShooter {

        public DirectionShooter(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, 'x', ProjectileType.REGULAR);
        }

    }

    public static class HomingShooter extends RangeTriggerableShooter {

        public HomingShooter(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, 'h', ProjectileType.HOMING);
        }

    }

    public static class MovingShooter extends RegularShooter implements MovingCollidable {

        private static final double SPEED = -1;
        private boolean xCollided, yCollided;
        private double xVelocity, yVelocity;
        private final char alignment;

        public MovingShooter(int worldIndex, int xNum, int yNum, char type, char alignment) {
            super(worldIndex, xNum, yNum, type);
            this.alignment = alignment;
            if (alignment == HORIZONTAL) {
                xVelocity = SPEED;
            } else {
                yVelocity = SPEED;
            }
        }

        @Override
        public void call() {
            super.call();

            checkDead();
            if (broken) return;

            if (alignment == HORIZONTAL && xCollided) {
                xVelocity *= -1;
                xCollided = false;
            }
            if (alignment == VERTICAL && yCollided) {
                yVelocity *= -1;
                yCollided = false;
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

    }

}

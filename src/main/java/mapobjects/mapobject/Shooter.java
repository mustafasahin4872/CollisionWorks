package mapobjects.mapobject;

import game.Frame;
import helpers.Blueprint;
import mapobjects.component.*;
import mapobjects.category.*;
import mapobjects.component.Spawner;

import java.util.HashSet;
import java.util.Set;

import static helpers.HelperMethods.outOfMapBounds;
import mapobjects.mapobject.Projectile.ProjectileType;
import mapobjects.mapobject.Projectile.ProjectileBlueprint;

public abstract class Shooter extends GridObject implements Collidable, Timed, Generator, HealthBearer {

    protected final ProjectileBlueprint projectileBlueprint;
    protected final Box collisionBox;
    protected final Timer timer;
    protected final Spawner spawner;
    private final HPBar HPBar;
    protected final char type;
    protected final Set<Projectile> projectiles = new HashSet<>();
    protected static final double DEFAULT_COOLDOWN = 3000; //in milliseconds
    protected final GridObject[][][] layers;
    protected boolean broken;
    protected Set<HealthBearer> targets;

    public Shooter(int worldIndex, int xNum, int yNum, char type, ProjectileType projectileType, GridObject[][][] layers) {
        super(worldIndex, xNum, yNum, "0");
        this.type = type;
        this.layers = layers;
        this.projectileBlueprint = new ProjectileBlueprint(projectileType);
        collisionBox = positionBox.clone();
        timer = new Timer(0, DEFAULT_COOLDOWN / worldIndex);
        spawner = new Spawner(this);
        HPBar = new HPBar(50);
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
        return HPBar;
    }

    @Override
    public void timeIsUp(Player player) {
        spawn();
    }

    @Override
    public void ifDied() {
    }

    @Override
    public void ifNoLivesLeft() {
        broken = true;
        setName("1");
    }

    public void setTargets(Set<HealthBearer> targets) {
        this.targets = targets;
    }

    @Override
    public void call(Player player) {
        for (Projectile projectile : projectiles) {
            projectile.call(player, layers);
        }
        projectiles.removeIf(Projectile::isExpired);

        checkDead();
        if (broken) return;

        if (isActive()) {
            spawn();
        }
        updateTimer();
        if (!isComplete() && !isActive()) { //cooldown over, activate
            activateTimer();
        }
    }

    @Override
    public void draw() {
        super.draw();
        HPBar.drawHPBar(this);
        for (Projectile projectile : projectiles) {
            projectile.draw();
        }
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
        projectiles.add(projectile);
    }


    public static class RegularShooter extends Shooter {
        public RegularShooter(int worldIndex, int xNum, int yNum, char type, GridObject[][][] layers) {
            super(worldIndex, xNum, yNum, type, ProjectileType.REGULAR, layers);
            activateTimer();
        }

    }


    public static class DirectionShooter extends Shooter implements Ranged {

        private final Box rangeBox;
        protected final Player player;
        private static final double RANGE = 1.5;

        public DirectionShooter(int worldIndex, int xNum, int yNum, GridObject[][][] layers, Player player) {
            super(worldIndex, xNum, yNum, 'x', ProjectileType.REGULAR, layers);
            rangeBox = new Box(positionBox.getCenterCoordinates(), RANGE*TILE_SIDE*2, RANGE*TILE_SIDE*2);
            this.player = player;
        }

        @Override
        public Box getRangeBox() {
            return rangeBox;
        }

        @Override
        public void playerInRange(Player player) {
            activateTimer();
        }


        @Override
        public void call(Player player) {
            for (Projectile projectile : projectiles) {
                projectile.call(player, layers);
            }
            projectiles.removeIf(Projectile::isExpired);

            checkDead();
            if (broken) return;

            if (isActive()) {
                spawn();
            }

            updateTimer();

            if (cooldownOver()) {
                timeIsUp(player);
            }


        }

        @Override
        public void spawn() {
            Blueprint blueprint = spawner.directionSpawn(getCenterCoordinates(), player.getCenterCoordinates(), 50);
            Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, 0);
            projectile.rotate(player.getCenterCoordinates());
            projectiles.add(projectile);
        }

        @Override
        public void timeIsUp(Player player) {
            checkPlayerInRange(player);
        }
    }

    public static class HomingShooter extends Shooter implements  Ranged {

        private final Box rangeBox;
        protected final Player player;
        private static final double RANGE = 9.5;

        public HomingShooter(int worldIndex, int xNum, int yNum, GridObject[][][] layers, Player player) {
            super(worldIndex, xNum, yNum, 'h', ProjectileType.HOMING, layers);
            this.player = player;
            rangeBox = new Box(positionBox.getCenterCoordinates(), RANGE*TILE_SIDE*2, RANGE*TILE_SIDE*2);
        }

        @Override
        public Box getRangeBox() {
            return rangeBox;
        }

        @Override
        public void playerInRange(Player player) {
            activateTimer();
        }


        @Override
        public void call(Player player) {
            for (Projectile projectile : projectiles) {
                projectile.call(player, layers);
            }
            projectiles.removeIf(Projectile::isExpired);

            checkDead();
            if (broken) return;

            if (isActive()) {
                spawn();
            }

            updateTimer();

            if (cooldownOver()) {
                timeIsUp(player);
            }


        }

        @Override
        public void spawn() {
            Blueprint blueprint = spawner.directionSpawn(getCenterCoordinates(), player.getCenterCoordinates(), 50);
            Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, 0);
            projectile.rotate(player.getCenterCoordinates());
            projectile.setTargets(targets);
            projectiles.add(projectile);
        }

        @Override
        public void timeIsUp(Player player) {
            checkPlayerInRange(player);
        }
    }


    public static class MovingShooter extends Shooter implements MovingCollidable {

        private static final double SPEED = -1;
        private boolean xCollided, yCollided;
        private double xVelocity, yVelocity;
        private final char alignment;
        public MovingShooter(int worldIndex, int xNum, int yNum, char type, char alignment, GridObject[][][] layers) {
            super(worldIndex, xNum, yNum, type, ProjectileType.REGULAR, layers);
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
            int range = 2; //the checking range
            boolean collided = false;
            for (GridObject[][] layer : layers) {
                if (collided) break;
                for (int i = gridNumbers[1]-range; i<gridNumbers[1]+range; i++) {
                    if (collided) break;
                    for (int j = gridNumbers[0]-range; j<gridNumbers[0]+range; j++) {
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
                        if (outOfMapBounds(layer, i, j)) continue;

                        GridObject currentGridObject = layer[i][j];
                        if (currentGridObject == this) continue;
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

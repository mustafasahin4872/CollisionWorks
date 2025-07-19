package mapobjects.mapobject;

import game.Frame;
import game.Player;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.component.Damager;
import mapobjects.component.Direction;
import mapobjects.component.Timer;
import mapobjects.category.*;

//projectiles are not grid objects, they are bullets that crash to collidables and pass through water and other tiles
public class Projectile extends MapObject implements MovingCollidable, Timed, Damaging {

    private final Box collisionBox;
    private final Damager damager;
    boolean xCollided, yCollided;
    protected final Direction direction;
    private double speed;
    public static final double DEFAULT_INITIAL_SPEED = 7;
    private final double initialSpeed;
    private static final double MAX_SPEED = 30;
    private final Timer timer = new Timer(5000, -1);


    //accept direction in degrees
    public Projectile(int worldIndex, double x, double y, int direction) {
        this(worldIndex, x, y, 40, 20, direction);
    }

    public Projectile(int worldIndex, double x, double y, double width, double height, int direction) {
        this(worldIndex, x, y, width, height, direction, DEFAULT_INITIAL_SPEED);
    }

    public Projectile(int worldIndex, double x, double y, double width, double height, int direction, double speed) {
        super(worldIndex, x, y, width, height, "misc/misc/projectile.png");
        this.direction = new Direction(direction);
        collisionBox = new Box(x, y, height, height);
        damager = new Damager(20);
        initialSpeed = speed;
        this.speed = initialSpeed;
        activateTimer();
    }

    public void call(Player player, GridObject[][][] layers) {
        updateTimer();
        if (isComplete()) {timeIsUp(player);}
        speed = initialSpeed + timer.progressRatio() * (MAX_SPEED- initialSpeed);
        move();
        boolean collided = false;

        player.checkCollision(this);

        if (xCollided || yCollided) {
            expire();
            dealDamage(player);
            collided = true;
        }

        int[] gridNumbers = getGridNumbers();

        int range = 2; //the checking range

        for (GridObject[][] layer : layers) {
            if (collided) break;
            for (int i = gridNumbers[1]-range; i<gridNumbers[1]+range; i++) {
                if (collided) break;
                for (int j = gridNumbers[0]-range; j<gridNumbers[0]+range; j++) {
                    if (i<0 || j<0 || i>=layer.length || j>=layer[0].length) continue;
                    GridObject currentGridObject = layer[i][j];
                    if (currentGridObject instanceof Collidable c) {
                        c.checkCollision(this);
                        if (xCollided || yCollided) {
                            collided = true;
                            if (c instanceof HealthBearer h) dealDamage(h);
                            expire();
                            break;
                        }
                    }
                }
            }
        }

    }

    private void move() {
        positionBox.xShift(getXVelocity()*Frame.DT);
        positionBox.yShift(getYVelocity()*Frame.DT);
        collisionBox.xShift(getXVelocity()*Frame.DT);
        collisionBox.yShift(getYVelocity()*Frame.DT);
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
    public void draw() {
        StdDraw.picture(getX(), getY(), fileName, getWidth(), getHeight(), direction.getDegreeDirection());
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public void timeIsUp(Player player) {
        expire();
    }

    public static class HomingProjectile extends Projectile {

        private final double I;

        public HomingProjectile(int worldIndex, double x, double y, int direction, double I) {
            super(worldIndex, x, y, direction);
            this.I = I;
            setFileName("misc/misc/homingProjectile.png");
        }

        @Override
        public void call(Player player, GridObject[][][] layers) {
            direction.rotateTowards(getCenterCoordinates(), player.getCenterCoordinates(), I);
            super.call(player, layers);
        }

    }

}

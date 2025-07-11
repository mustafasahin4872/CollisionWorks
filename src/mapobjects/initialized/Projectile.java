package mapobjects.initialized;

import game.Frame;
import game.Player;
import mapobjects.framework.*;

//projectiles are not grid objects, they are bullets that crash to collidables and pass through water and other tiles
public class Projectile extends MapObject implements MovingCollidable {

    private final Box collisionBox;
    boolean xCollided, yCollided;
    private final Direction direction;
    private final double speed = 50;
    private final double damage = 20;

    //accept direction in degrees
    public Projectile(int worldIndex, double x, double y, double width, double height, int direction) {
        super(worldIndex, x, y, width, height);
        this.direction = new Direction(direction);
        collisionBox = positionBox.clone();
    }

    public void call(Player player) {
        move();
        if (xCollided || yCollided) {
            expire();
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

    public void setDirection(int direction) {
        this.direction.setDirection(direction);
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
}

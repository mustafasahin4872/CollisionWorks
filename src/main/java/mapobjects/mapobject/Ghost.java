package mapobjects.mapobject;

import game.Frame;
import game.Player;
import mapobjects.category.*;
import mapobjects.component.Box;
import mapobjects.component.Damager;
import mapobjects.component.Timer;

import static helperobjects.Helpers.outOfMapBounds;

//passable ticking damager, moves
//collides with everything except player and other ghosts
public class Ghost extends GridObject implements OnEffector, MovingCollidable, Damaging, Timed{

    private final String fileRoot;
    private final Box collisionBox;
    private final Box effectBox;
    private final Damager damager;
    private final Timer timer;
    private char alignment;
    private double speed = 10, xVelocity, yVelocity;
    private boolean xCollided, yCollided;
    private final GridObject[][][] layers;

    public Ghost(int worldIndex, int xNum, int yNum, char alignment, GridObject[][][] layers) {
        super(worldIndex, xNum, yNum, "src/main/resources/ghostImages/ghost"+worldIndex+".png");
        fileRoot = rollForGhostType(worldIndex);
        collisionBox = positionBox.clone();
        effectBox = positionBox.clone();
        damager = new Damager(worldIndex * 10);
        timer = new Timer(0, 500);
        this.layers = layers;
        if (alignment == HORIZONTAL) {
            xVelocity = speed;
        } else {
            yVelocity = speed;
        }
    }

    //rare ghost image!!!
    private String rollForGhostType(int worldIndex) {
        final String fileRoot;
        String extra = "";
        double roll = Math.random();
        if (roll>0.9) {
            extra = "_";
            speed *= 2;
        }
        fileRoot = "src/main/resources/ghostImages/ghost"+ worldIndex +extra;
        return fileRoot;
    }

    @Override
    public void draw() {
        String direction = switch (alignment) {
            case HORIZONTAL -> {
                if (xVelocity<0) yield "L";
                else yield "R";
            }
            case VERTICAL -> {
                if (yVelocity>0) yield "U";
                else yield "D";
            }
            default -> "";
        };
        setFileName(fileRoot + direction + ".png");
        super.draw();
    }

    @Override
    public void call(Player player) {
        updateTimer();
        if (cooldownOver()) timeIsUp(player);
        move();

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
        effectBox.xShift(xVelocity*Frame.DT);
        effectBox.yShift(yVelocity*Frame.DT);
        collisionBox.xShift(xVelocity * Frame.DT);
        collisionBox.yShift(yVelocity * Frame.DT);
    }

    @Override
    public Box getCollisionBox() {
        return collisionBox;
    }

    @Override
    public Box getEffectBox() {
        return effectBox;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public Damager getDamager() {
        return damager;
    }

    @Override
    public void timeIsUp(Player player) {
        checkPlayerIsOn(player);
    }

    @Override
    public void checkPlayerIsOn(Player player) {
        checkPlayerCornerIsOn(player);
    }

    @Override
    public void playerIsOn(Player player) {
        dealDamage(player);
        activateTimer();
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
}

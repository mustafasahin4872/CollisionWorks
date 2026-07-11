package mapobjects.entities;

import game.io.Frame;
import game.io.Drawer.PictureDrawer;
import mapobjects.traits.*;
import mapobjects.components.Box;
import mapobjects.components.Damager;
import mapobjects.components.Timer;

import java.util.Set;

import static helpers.HelperMethods.*;


//passable ticking damager, moves
//collides with everything except player and other ghosts
public class Ghost extends GridObject implements OnEffector, MovingCollidable, Damaging, Timed, Drawable {

    public enum ghostTypes {
        DEMON, ANGEL, BLUE, WHITE, GOLD, SAKURA, RANDOM,
        DEFAULT1, DEFAULT2, DEFAULT3, DEFAULT4,
        SPECIAL1, SPECIAL2, SPECIAL3, SPECIAL4
    }

    protected static final char VERTICAL = '|', HORIZONTAL = '—';

    private final ghostTypes type;
    private final Box collisionBox;
    private final Box effectBox;
    private final Damager damager;
    private final Timer timer;
    private final char alignment;
    private double speed = 3, xVelocity, yVelocity;
    private boolean xCollided, yCollided;
    private final GridObject[][][] layers;
    private Set<HealthBearer> targets;
    private final PictureDrawer drawer;

    public Ghost(int worldIndex, int xNum, int yNum, char alignment, GridObject[][][] layers) {
        super(worldIndex, xNum, yNum);
        this.alignment = alignment;
        type = rollForGhostType();
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
        drawer = new PictureDrawer(positionBox, getDirectory1());

    }

    private ghostTypes rollForGhostType() {
        int roll = (int)(Math.random() * 1000); // Use 0–999 for 0.1% granularity

        if (roll < 400) { // 40% → world default
            return switch (worldIndex) {
                case 1 -> ghostTypes.DEFAULT1;
                case 2 -> ghostTypes.DEFAULT2;
                case 3 -> ghostTypes.DEFAULT3;
                default -> ghostTypes.DEFAULT4;
            };
        } else if (roll < 550) { // 15% → other world defaults
            return switch (worldIndex) {
                case 1 -> ghostTypes.DEFAULT2;
                case 2 -> ghostTypes.DEFAULT3;
                case 3 -> ghostTypes.DEFAULT4;
                default -> ghostTypes.DEFAULT1;
            };
        } else if (roll < 650) { // 10% → rare for world
            return switch (worldIndex) {
                case 1 -> ghostTypes.SPECIAL1;
                case 2 -> ghostTypes.SPECIAL2;
                case 3 -> ghostTypes.SPECIAL3;
                default -> ghostTypes.SPECIAL4;
            };
        } else if (roll < 725) { // 7.5% → blue
            return ghostTypes.BLUE;
        }  else if (roll < 800) { // 7.5% → sakura
            return ghostTypes.SAKURA;
        } else if (roll < 850) { // 5% → gold
            return ghostTypes.GOLD;
        } else if (roll < 900) { // 5% → white
            return ghostTypes.WHITE;
        } else if (roll < 950) { // 5% → random
            return ghostTypes.RANDOM;
        } else if (roll < 975) { // 2.5% → demon
            return ghostTypes.DEMON;
        } else { // 2.5% → angel
            return ghostTypes.ANGEL;
        }
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
            for (int i = gridNumbers[1] - range; i < gridNumbers[1] + range; i++) {
                if (collided) break;
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
                    if (outOfMapBounds(layer, i, j)) continue;

                    GridObject currentGridObject = layer[i][j];
                    if (currentGridObject == this) continue;
                    if (currentGridObject instanceof Collidable c && !(c instanceof Ghost)) {
                        c.checkCollision(this);
                    }
                }
            }
        }
        String direction = switch (alignment) {
            case HORIZONTAL -> {
                if (xVelocity < 0) yield "L";
                else yield "R";
            }
            case VERTICAL -> {
                if (yVelocity > 0) yield "D";
                else yield "U";
            }
            default -> "";
        };
        drawer.setName(type.name() + direction);
    }

    public void move() {
        positionBox.xShift(xVelocity * Frame.DT);
        positionBox.yShift(yVelocity * Frame.DT);
        effectBox.xShift(xVelocity * Frame.DT);
        effectBox.yShift(yVelocity * Frame.DT);
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
    public void setTargets(Set<HealthBearer> targets) {
        this.targets = targets;
    }

    @Override
    public Set<HealthBearer> getTargets() {
        return targets;
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

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }
}

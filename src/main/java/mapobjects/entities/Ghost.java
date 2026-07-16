package mapobjects.entities;

import game.core.GameMap;
import game.io.Frame;
import game.io.Drawer.PictureDrawer;
import mapobjects.components.Effector;
import mapobjects.effects.DamageEffect;
import mapobjects.components.Box;
import mapobjects.traits.collisions.Collidable;
import mapobjects.traits.collisions.Movable;
import mapobjects.traits.collisions.MovingCollidable;
import mapobjects.traits.receivers.Receiver;
import mapobjects.traits.schemas.Damaging;
import mapobjects.traits.schemas.HealthBearer;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.senders.Sender;
import mapobjects.traits.triggerables.MovedOverTriggerable;

import mapobjects.components.Trigger;
import java.util.Set;

// collides with everything except player and other ghosts
public class Ghost extends GridObject implements MovedOverTriggerable, MovingCollidable, Damaging, Drawable, Sender {

    public enum ghostTypes {
        DEMON, ANGEL, BLUE, WHITE, GOLD, SAKURA, RANDOM,
        DEFAULT1, DEFAULT2, DEFAULT3, DEFAULT4,
        SPECIAL1, SPECIAL2, SPECIAL3, SPECIAL4
    }

    protected static final char VERTICAL = '|', HORIZONTAL = '—';

    private final ghostTypes type;
    private final Box collisionBox;
    private final Effector effector;
    private final char alignment;
    private double xVelocity;
    private double yVelocity;
    private boolean xCollided, yCollided;
    private final GridObject[][][] layers;
    private final PictureDrawer drawer;
    private Set<HealthBearer> targets;
    private final Trigger<Movable> collisionTrigger;

    public Ghost(int worldIndex, int xNum, int yNum, char alignment, GridObject[][][] layers) {
        super(worldIndex, xNum, yNum);
        this.alignment = alignment;
        type = rollForGhostType();
        collisionBox = positionBox.clone();
        effector = new Effector(new DamageEffect(worldIndex * 10, 0));
        collisionTrigger = new Trigger<>(positionBox, this::triggerCollision);
        this.layers = layers;
        double speed = 3;
        if (alignment == HORIZONTAL) {
            xVelocity = speed;
        } else {
            yVelocity = speed;
        }
        drawer = new PictureDrawer(positionBox, getDirectory1());

    }

    private ghostTypes rollForGhostType() {
        int roll = (int) (Math.random() * 1000); // Use 0–999 for 0.1% granularity

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
        } else if (roll < 800) { // 7.5% → sakura
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
    public void call() {

        move();

        int[] gridNumbers = getGridNumbers();
        int range = 2; // the checking range
        boolean collided = false;
        for (GridObject[][] layer : layers) {
            if (collided)
                break;
            for (int y = gridNumbers[1] - range; y < gridNumbers[1] + range; y++) {
                if (collided)
                    break;
                for (int x = gridNumbers[0] - range; x < gridNumbers[0] + range; x++) {
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
                    if (GameMap.outOfMapBounds(layer, x, y))
                        continue;

                    GridObject currentGridObject = layer[y][x];
                    if (currentGridObject == this)
                        continue;
                    if (currentGridObject instanceof Collidable c && !(c instanceof Ghost)) {
                        c.checkCollision(this);
                    }
                }
            }
        }
        String direction = switch (alignment) {
            case HORIZONTAL -> {
                if (xVelocity < 0)
                    yield "L";
                else
                    yield "R";
            }
            case VERTICAL -> {
                if (yVelocity > 0)
                    yield "D";
                else
                    yield "U";
            }
            default -> "";
        };
        drawer.setName(type.name() + direction);
    }

    public void move() {
        positionBox.xShift(xVelocity * Frame.DT);
        positionBox.yShift(yVelocity * Frame.DT);
        collisionBox.xShift(xVelocity * Frame.DT);
        collisionBox.yShift(yVelocity * Frame.DT);
    }

    @Override
    public Box getCollisionBox() {
        return collisionBox;
    }

    @Override
    public void setTargets(Set<HealthBearer> targets) {
        this.targets = targets;
    }

    @Override
    public Trigger<Movable> getMovedOverTrigger() {
        return collisionTrigger;
    }

    private void triggerCollision(Movable movable) {
        if (movable instanceof Receiver r) {
            sendEffect(r);
        }
    }

    @Override
    public Effector getEffector() {
        return effector;
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

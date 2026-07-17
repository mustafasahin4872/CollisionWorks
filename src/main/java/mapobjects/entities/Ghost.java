package mapobjects.entities;

import game.io.Frame;
import game.io.Drawer.PictureDrawer;
import mapobjects.components.Effector;
import mapobjects.effects.DamageEffect;
import mapobjects.components.Box;
import mapobjects.traits.collisions.Movable;
import mapobjects.traits.collisions.Moving;
import mapobjects.traits.collisions.MovingCollidable;
import mapobjects.traits.receivers.Receiver;
import mapobjects.traits.senders.Damaging;
import mapobjects.traits.receivers.HealthEffectReceiver;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.senders.Sender;
import mapobjects.traits.triggerables.MovedOverTriggerable;

import mapobjects.components.Trigger;

// collides with everything except player and other ghosts
public class Ghost extends GridObject implements MovedOverTriggerable, Moving, Damaging, Drawable, Sender {

    public enum ghostTypes {
        DEMON, ANGEL, BLUE, WHITE, GOLD, SAKURA, RANDOM,
        DEFAULT1, DEFAULT2, DEFAULT3, DEFAULT4,
        SPECIAL1, SPECIAL2, SPECIAL3, SPECIAL4
    }

    protected static final char VERTICAL = '|', HORIZONTAL = '—';
    private double start, end;

    private final ghostTypes type;
    private final Effector effector;
    private final char alignment;
    private double xVelocity;
    private double yVelocity;
    private final PictureDrawer drawer;
    private final Class<? extends HealthEffectReceiver> targetClass = Player.class;
    private final Trigger<Movable> collisionTrigger;

    public Ghost(int worldIndex, int xNum, int yNum, char alignment) {
        super(worldIndex, xNum, yNum);
        this.alignment = alignment;
        type = rollForGhostType();
        effector = new Effector(new DamageEffect(worldIndex * 10, 0));
        collisionTrigger = new Trigger<>(positionBox, this::triggerCollision);
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

        if (alignment == HORIZONTAL) {
            if (getX() > end) {
                xVelocity *= -1;
                setX(end);
            } else if (getX() < start) {
                xVelocity *= -1;
                setX(start);
            }
        }
        if (alignment == VERTICAL) {
            if (getY() > end) {
                yVelocity *= -1;
                setY(end);
            } else if (getY() < start) {
                yVelocity *= -1;
                setY(start);
            }
        }

        move();

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

    public void setRange(double start, double end) {
        this.start = start;
        this.end = end;
    }

    public void move() {
        positionBox.xShift(xVelocity * Frame.DT);
        positionBox.yShift(yVelocity * Frame.DT);
    }

    public char getAlignment() {
        return alignment;
    }

    @Override
    public Class<? extends HealthEffectReceiver> getTargetClass() {
        return targetClass;
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
    public PictureDrawer getDrawer() {
        return drawer;
    }
}

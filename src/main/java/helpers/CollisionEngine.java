package helpers;

import game.core.GameMap;
import mapobjects.components.Box;
import mapobjects.entities.Player;
import mapobjects.entities.Ghost;
import mapobjects.entities.Projectile;
import mapobjects.traits.collisions.MovingCollidable;
import mapobjects.traits.collisions.Collidable;
import mapobjects.traits.schemas.MapObject;
import mapobjects.traits.triggerables.MovedOverTriggerable;
import mapobjects.traits.triggerables.PlayerOnTriggerable;
import mapobjects.traits.receivers.HealthEffectReceiver;

import java.util.Set;

public class CollisionEngine {

    private final GameMap gameMap;

    public CollisionEngine(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void checkAndResolveCollisions() {
        // clear and rebuild the dynamic grid map
        gameMap.rebuildGrid();

        // loop through all moving objects on the map
        Set<MovingCollidable> movingObjects = gameMap.getMovingCollidableObjects();
        for (MovingCollidable moving : movingObjects) {
            if (moving instanceof MapObject mo && mo.isExpired()) continue;

            int[] bounds = moving.getCollidedTileIndexes();
            // Since getObjectsInTileRange uses exclusive upper bounds (y < endY),
            // we add 1 to the end index to query the tiles inclusively.
            Set<MapObject> neighbors = gameMap.getObjectsInTileRange(bounds[0], bounds[1], bounds[2] + 1, bounds[3] + 1);

            for (MapObject neighbor : neighbors) {
                if (neighbor == moving || neighbor.isExpired()) continue;

                // --- A. Projectile Hits ---
                if (moving instanceof Projectile projectile) {
                    if (neighbor instanceof HealthEffectReceiver target && projectile.getTargets().contains(target)) {
                        if (intersects(projectile.getCollisionBox(), neighbor.getPositionBox())) {
                            projectile.sendEffect(target);
                            projectile.expire();
                            continue;
                        }
                    }
                }

                // --- B. Solid AABB Collisions ---
                if (neighbor instanceof Collidable obstacle) {
                    // Ignore ghost-to-ghost solid collision (but check triggers if applicable)
                    if (moving instanceof Ghost && obstacle instanceof Ghost) {
                        continue;
                    }
                    resolveCollision(moving, obstacle);
                }

                // --- C. MovedOver Triggers (Buttons, Chests, etc.) ---
                if (neighbor instanceof MovedOverTriggerable triggerable) {
                    if (intersects(moving.getCollisionBox(), neighbor.getPositionBox())) {
                        triggerable.getMovedOverTrigger().checkForTriggers();
                    }
                }

                // --- D. PlayerOn Triggers (Only applies if moving is the Player) ---
                if (moving instanceof Player player && neighbor instanceof PlayerOnTriggerable triggerable) {
                    if (intersects(player.getCollisionBox(), neighbor.getPositionBox())) {
                        triggerable.getPlayerOnTrigger().checkForTriggers();
                    }
                }
            }
        }
    }

    private void resolveCollision(MovingCollidable moving, Collidable obstacle) {
        boolean xCollided = moving.isXCollided();
        boolean yCollided = moving.isYCollided();

        if (xCollided && yCollided) return;

        Box collidedBox = obstacle.getCollisionBox();
        Box movingBox = moving.getCollisionBox();
        Box nextXBox = moving.getNextXBox();
        Box nextYBox = moving.getNextYBox();

        if (!xCollided && intersects(nextXBox, collidedBox)) {
            double xVelocity = moving.getXVelocity();
            moving.xCollide();
            if (xVelocity > 0) {
                moving.setX(collidedBox.getCorner(0) - movingBox.getWidth() / 2);
            } else if (xVelocity < 0) {
                moving.setX(collidedBox.getCorner(2) + movingBox.getWidth() / 2);
            }
        }

        if (!yCollided && intersects(nextYBox, collidedBox)) {
            double yVelocity = moving.getYVelocity();
            moving.yCollide();
            if (yVelocity > 0) {
                moving.setY(collidedBox.getCorner(1) - movingBox.getHeight() / 2);
            } else if (yVelocity < 0) {
                moving.setY(collidedBox.getCorner(3) + movingBox.getHeight() / 2);
            }
        }
    }

    public static boolean intersects(Box a, Box b) {
        double[] A = a.getCorners(), B = b.getCorners();
        double ax0 = A[0], ay0 = A[1], ax1 = A[2], ay1 = A[3];
        double bx0 = B[0], by0 = B[1], bx1 = B[2], by1 = B[3];

        return ax0 < bx1 && ax1 > bx0 &&
                ay0 < by1 && ay1 > by0;
    }

    public static boolean isIn(double x, double y, Box box) {
        return isIn(x, y, box.getCorners());
    }

    public static boolean isIn(double x, double y, double[] obstacle) {
        return ((x > obstacle[0]) && (x < obstacle[2]) && (y > obstacle[1]) && (y < obstacle[3]));
    }

    public static boolean isIn(double x, double y, int[] obstacle) {
        return ((x > obstacle[0]) && (x < obstacle[2]) && (y > obstacle[1]) && (y < obstacle[3]));
    }

}

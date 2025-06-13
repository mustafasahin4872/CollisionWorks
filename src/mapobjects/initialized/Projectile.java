package mapobjects.initialized;

import game.Frame;
import game.Player;
import lib.StdDraw;
import mapobjects.framework.EffectBox;
import mapobjects.framework.Effector;
import mapobjects.framework.MapObject;

public class Projectile extends MapObject implements Effector {

    private final EffectBox effectBox;
    private boolean crashed;
    private final char direction;
    private static final double DEFAULT_SPEED = 1;
    private final double speed, delta;
    private static final double DEFAULT_DAMAGE = 20;
    private final double damage;

    public Projectile(int worldIndex, int xNum, int yNum, char direction, double width, double height) {
        super(worldIndex, xNum, yNum, width, height);
        this.direction = direction;
        damage = DEFAULT_DAMAGE*worldIndex;

        if (direction == DOWN || direction == RIGHT) {
            speed = DEFAULT_SPEED*worldIndex;
        } else {
            speed = -1*DEFAULT_SPEED*worldIndex;
        }
        delta = speed* Frame.DT;
        effectBox = new EffectBox(this);
    }


    public void call(Player player) {}

    public void move() {
        if (direction == DOWN || direction == UP) {
            yShiftPosition(delta);
        } else {
            xShiftPosition(delta);
        }
    }

    public void checkWallCollision(Tile[] tiles, int xTile) {
        int tileIndex = switch (direction) {
            case DOWN -> ((int) ((centerCoordinates[1] + height / 2) / TILE_SIDE)) * xTile + (xNum - 1);
            case UP -> ((int) ((centerCoordinates[1] - height / 2) / TILE_SIDE)) * xTile + (xNum - 1);
            case RIGHT -> ((int) ((centerCoordinates[0] + width / 2) / TILE_SIDE)) + (yNum - 1) * xTile;
            case LEFT -> ((int) ((centerCoordinates[0] - width / 2) / TILE_SIDE)) + (yNum - 1) * xTile;
            default -> {
                System.out.println("default error message for wall collision: direction is ambiguous");
                yield ((int) ((centerCoordinates[0] - width / 2) / TILE_SIDE)) + (yNum - 1) * xTile;
            }
        };
        if (tiles[tileIndex] instanceof Tile.WallTile) {
            crashed = true;
        }
    }

    @Override
    public double[] getEffectBox() {
        return effectBox.getEffectBox();
    }

    @Override
    public void playerIsOn(Player player) {
        crashed = true;
        player.damage(damage);
    }

    @Override
    public void draw() {
        if (crashed) return;
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], "misc/misc/projectile.png", width, height);
    }

}

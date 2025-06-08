package mapobjects;

import game.Frame;
import game.Player;
import helperobjects.Alignment;
import helperobjects.Direction;
import lib.StdDraw;

public class Projectile extends MapObject{

    private boolean crashed;
    private final Direction direction;
    private static final double DEFAULT_SPEED = 1;
    private final double speed, delta;
    private final double width, height;
    private static final double DEFAULT_DAMAGE = 20;
    private final double damage;

    public Projectile(int worldIndex, int xNum, int yNum, Direction direction, double width, double height) {
        super(worldIndex, xNum, yNum);
        this.direction = direction;
        this.width = width;
        this.height = height;
        coordinates[0] += TILE_SIDE-width/2;
        coordinates[1] += TILE_SIDE-height/2;
        coordinates[2] -= TILE_SIDE-width/2;
        coordinates[3] -= TILE_SIDE-height/2;
        resetCollisionBox();
        damage = DEFAULT_DAMAGE*worldIndex;

        if (direction == Direction.D || direction == Direction.R) {
            speed = DEFAULT_SPEED*worldIndex;
        } else {
            speed = -1*DEFAULT_SPEED*worldIndex;
        }
        delta = speed* Frame.DT;
    }

    public void move() {
        if (direction == Direction.D || direction == Direction.U) {
            yShift(delta);
        } else {
            xShift(delta);
        }
    }

    public void checkWallCollision(Tile[] tiles, int xTile) {
        int tileIndex = switch (direction) {
            case D -> ((int) ((centerCoordinates[1] + height / 2) / TILE_SIDE)) * xTile + (xNum - 1);
            case U -> ((int) ((centerCoordinates[1] - height / 2) / TILE_SIDE)) * xTile + (xNum - 1);
            case R -> ((int) ((centerCoordinates[0] + width / 2) / TILE_SIDE)) + (yNum - 1) * xTile;
            case L -> ((int) ((centerCoordinates[0] - width / 2) / TILE_SIDE)) + (yNum - 1) * xTile;
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

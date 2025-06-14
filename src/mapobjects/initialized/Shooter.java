package mapobjects.initialized;

import game.Player;
import mapobjects.framework.Collidable;
import mapobjects.framework.Box;
import mapobjects.framework.MapObject;

import java.util.ArrayList;

//can move?
public class Shooter extends MapObject implements Collidable {

    private final Box collisionBox;
    private final char alignment, direction;
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private static final double DEFAULT_PERIOD = 12000; //in milliseconds
    private final double period;
    private final long startTime;
    private int shotProjectilesNumber;
    private final Tile[] tiles;
    private final int xTile;

    public Shooter(int worldIndex, int xNum, int yNum, char alignment, char direction, Tile[] tiles, int xTile) {
        super(worldIndex, xNum, yNum, "misc/misc/shooter.png");
        this.alignment = alignment;
        this.direction = direction;
        this.tiles = tiles;
        this.xTile = xTile;
        period = DEFAULT_PERIOD/worldIndex;
        startTime = System.currentTimeMillis();
        collisionBox = new Box(this);
    }


    @Override
    public double[] getCollisionBox() {
        return collisionBox.getBox();
    }

    @Override
    public void call(Player player) {
        checkCollision(player);
        shoot();
        for (Projectile projectile : projectiles) {
            projectile.call(player);
            projectile.checkWallCollision(tiles, xTile);
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void shoot() {
        int timePassed = (int)(System.currentTimeMillis() - startTime);
        if (timePassed/period>shotProjectilesNumber) {
            projectiles.add(new Projectile(worldIndex, xNum, yNum, direction, 50, 25));
            shotProjectilesNumber++;
        }
    }

}

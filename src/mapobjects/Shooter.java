package mapobjects;

import game.Player;
import helperobjects.Alignment;
import helperobjects.Direction;
import lib.StdDraw;

import java.util.ArrayList;

//can move?
public class Shooter extends MapObject {

    private final Alignment alignment;
    private final Direction direction;
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private static final double DEFAULT_PERIOD = 12000; //in milliseconds
    private final double period;
    private final long startTime;
    private int shotProjectilesNumber;

    public Shooter(int worldIndex, int xNum, int yNum, Alignment alignment, Direction direction) {
        super(worldIndex, xNum, yNum);
        this.alignment = alignment;
        this.direction = direction;
        period = DEFAULT_PERIOD/worldIndex;
        startTime = System.currentTimeMillis();
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

    @Override
    public void playerIsOn(Player player) {} //impossible

    @Override
    public void draw() {
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], "misc/misc/shooter.png", TILE_SIDE, TILE_SIDE);
    }
}

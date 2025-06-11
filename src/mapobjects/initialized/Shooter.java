package mapobjects.initialized;

import game.Player;
import mapobjects.framework.MapObject;

import java.util.ArrayList;

//can move?
public class Shooter extends MapObject {

    private final char alignment, direction;
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private static final double DEFAULT_PERIOD = 12000; //in milliseconds
    private final double period;
    private final long startTime;
    private int shotProjectilesNumber;

    public Shooter(int worldIndex, int xNum, int yNum, char alignment, char direction) {
        super(worldIndex, xNum, yNum, "misc/misc/shooter.png");
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

}

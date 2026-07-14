package mapobjects.entities;

import game.io.Drawer.PictureDrawer;
import mapobjects.components.*;
import mapobjects.effects.DamageEffect;
import mapobjects.effects.Effect;
import mapobjects.factories.Blueprint;
import mapobjects.traits.*;

import java.util.Set;

public class Mortar extends GridObject implements Collidable, Ranged, Timed, Generator, HealthBearer, Drawable {

    private final Box collisionBox;
    private final Timer timer;
    private final Box rangeBox;
    private final Spawner spawner;
    private final HPBar hpBar;
    private final Inbox inbox;

    private boolean readyToSpawn;
    private boolean broken;
    private static final int RANGE = 5;
    private static final int BASE_MINE_NUM = 3;
    //period is max because the time of cooldown is determined by the mines
    private static final double DEFAULT_COOLDOWN = 4000;
    private final int mineNum;
    private Set<MapObject> spawnedObjects;
    private final GridObject[][][] layers;
    private Set<Moving> triggerers;

    private final PictureDrawer drawer;

    public Mortar(int worldIndex, int xNum, int yNum, GridObject[][][] layers) {
        this(worldIndex, xNum, yNum, layers, BASE_MINE_NUM*worldIndex);
    }

    public Mortar(int worldIndex, int xNum, int yNum, GridObject[][][] layers, int mineNum) {
        super(worldIndex, xNum, yNum, 2, 2, true);
        this.mineNum = mineNum;
        this.layers = layers;
        collisionBox = positionBox.clone();
        rangeBox = new Box(positionBox.getCenterCoordinates(), RANGE*TILE_SIDE*2, RANGE*TILE_SIDE*2);
        timer = new Timer(Long.MAX_VALUE, DEFAULT_COOLDOWN/worldIndex, true);
        spawner = new Spawner(this);
        hpBar = new HPBar(getWidth()*4);
        drawer = new PictureDrawer(positionBox, getDirectory1());
        inbox = new Inbox();
    }

    @Override
    public void setSpawnedObjects(Set<MapObject> spawnedObjects) {
        this.spawnedObjects = spawnedObjects;
    }

    @Override
    public Inbox getInbox() {
        return inbox;
    }

    @Override
    public void processEffects() {

        double totalDamage = 0;
        double totalShred = 0;

        for (Effect effect : inbox.getEffects()) {
            if (effect instanceof DamageEffect(double damage, double shred)) {
                totalDamage += damage;
                totalShred += shred;
            }
        }

        hpBar.takeDamage(totalDamage, totalShred);

    }

    @Override
    public void call(Player player) {
        checkDead();
        callTimer();

        if (!broken && !inCooldown()) {
            checkForTriggers();
            if (readyToSpawn) {
                spawn();
                startCooldown();
                readyToSpawn = false;
            }
        }
    }

    @Override
    public Set<Moving> getTriggerers() {
        return triggerers;
    }

    @Override
    public void setTriggerers(Set<Moving> triggerers) {
        this.triggerers = triggerers;
    }

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }

    @Override
    public void draw() {
        drawer.draw();
        hpBar.drawHPBar(this);
    }

    @Override
    public Box getCollisionBox() {
        return collisionBox;
    }

    @Override
    public Box getRangeBox() {
        return rangeBox;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public HPBar getHealthBar() {
        return hpBar;
    }

    @Override
    public void ifDied() {
    }

    @Override
    public void ifNoLivesLeft() {
        broken = true;
        drawer.setName("1");
    }

    @Override
    public void action(Moving moving) {
        readyToSpawn = true;
    }

    public void spawn() {
        Blueprint[] generators = spawner.randomSpawn(RANGE/2, mineNum, layers);
        for (int i = 0; i<mineNum; i++) {
            Mine mine = generators[i].mutateToMine();
            mine.activateTimer();
            spawnedObjects.add(mine);
        }
    }

}

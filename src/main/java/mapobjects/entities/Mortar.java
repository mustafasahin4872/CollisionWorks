package mapobjects.entities;

import helpers.utils.Drawer.PictureDrawer;
import mapobjects.factories.Blueprint;
import mapobjects.components.Spawner;
import mapobjects.components.Box;
import mapobjects.components.HPBar;
import mapobjects.components.Timer;
import mapobjects.traits.*;

public class Mortar extends GridObject implements Collidable, Ranged, Timed, Generator, HealthBearer, Drawable {

    private final Box collisionBox;
    private final Timer timer;
    private final Box rangeBox;
    private final Spawner spawner;
    private final HPBar HPBar;

    private boolean broken;
    private static final int RANGE = 8;
    private static final int BASE_MINE_NUM = 3;
    //period is max because the time of cooldown is determined by the mines
    private static final double PERIOD = Double.MAX_VALUE, DEFAULT_COOLDOWN = 3000;
    private final int mineNum;
    private final Mine[] mines;
    private final GridObject[][][] layers;

    private PictureDrawer drawer;

    public Mortar(int worldIndex, int xNum, int yNum, GridObject[][][] layers) {
        this(worldIndex, xNum, yNum, layers, BASE_MINE_NUM*worldIndex);
    }

    public Mortar(int worldIndex, int xNum, int yNum, GridObject[][][] layers, int mineNum) {
        super(worldIndex, xNum, yNum, 2, 2, true);
        this.mineNum = mineNum;
        this.layers = layers;
        collisionBox = positionBox.clone();
        rangeBox = new Box(positionBox.getCenterCoordinates(), RANGE*TILE_SIDE*2, RANGE*TILE_SIDE*2);
        timer = new Timer(PERIOD, DEFAULT_COOLDOWN/worldIndex);
        spawner = new Spawner(this);
        HPBar = new HPBar(getWidth()*4);
        mines = new Mine[mineNum];
        drawer = new PictureDrawer(positionBox, getDirectory1());
    }

    private boolean areMinesComplete() {
        for (Mine mine : mines) {if (!mine.isComplete()) {return false;}}
        return true;
    }


    @Override
    public void call(Player player) {
        checkDead();
        updateTimer();
        if (isComplete()) return; //in cooldown
        if (isActive()) {
            for (Mine mine : mines) mine.call(player);
            if (areMinesComplete()) {timeIsUp(player);} //start cooldown
        } else if (!broken) {checkPlayerInRange(player);}

    }

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }

    @Override
    public void draw() {
        drawer.draw();
        for (Mine mine : mines) {
            if (mine!=null) mine.draw();
        }
        HPBar.drawHPBar(this);
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
        return HPBar;
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
    public void playerInRange(Player player) {
        activateTimer();
        spawn();
        for (Mine mine : mines) mine.activateTimer();
    }


    @Override
    public void timeIsUp(Player player) {
        timer.startCooldown();
    }

    public void spawn() {
        Blueprint[] generators = spawner.randomSpawn(RANGE/2, mineNum, layers);
        Mine[] newMines = new Mine[mineNum];
        for (int i = 0; i<mineNum; i++) {
            newMines[i] = generators[i].mutateToMine();
        }
        System.arraycopy(newMines, 0, mines, 0, mineNum);
    }

}

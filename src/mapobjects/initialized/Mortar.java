package mapobjects.initialized;

import game.Player;
import helperobjects.Blueprint;
import mapobjects.framework.*;

public class Mortar extends GridObject implements Collidable, Ranged, Timed, Spawnable {

    private final Box collisionBox;
    private final Timer timer;
    private final Box rangeBox;
    private final Spawner spawner;

    private static final int RANGE = 8;
    private static final int BASE_MINE_NUM = 3;
    //period is max because the time of cooldown is determined by the mines
    private static final double PERIOD = Double.MAX_VALUE, DEFAULT_COOLDOWN = 3000;
    private final int mineNum;
    private final Mine[] mines;
    private final GridObject[][][] layers;

    public Mortar(int worldIndex, int xNum, int yNum, GridObject[][][] layers) {
        this(worldIndex, xNum, yNum, layers, BASE_MINE_NUM*worldIndex);
    }

    public Mortar(int worldIndex, int xNum, int yNum, GridObject[][][] layers, int mineNum) {
        super(worldIndex, xNum, yNum, 2, 2, "misc/misc/mortar.png", true);
        this.mineNum = mineNum;
        this.layers = layers;
        collisionBox = positionBox.clone();
        rangeBox = new Box(positionBox.getCenterCoordinates(), RANGE*TILE_SIDE, RANGE*TILE_SIDE);
        timer = new Timer(PERIOD, DEFAULT_COOLDOWN/worldIndex);
        spawner = new Spawner(this, mineNum);
        mines = new Mine[mineNum];
    }


    private boolean isActive() {
        return timer.isActive();
    }

    private boolean areMinesComplete() {
        for (Mine mine : mines) {if (!mine.isComplete()) {return false;}}
        return true;
    }

    private boolean isComplete() {
        return timer.isCompleted();
    }


    @Override
    public void call(Player player) {
        checkCollision(player);
        updateTimer();
        if (isComplete()) return; //in cooldown
        if (isActive()) {
            callSpawnObjects(player);
            if (areMinesComplete()) {timeIsUp(player);} //start cooldown
        } else {checkPlayerInRange(player);}

    }

    @Override
    public void draw() {
        super.draw();
        for (Mine mine : mines) {
            if (mine!=null) mine.draw();
        }
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
    public void playerInRange(Player player) {
        activateTimer();
        spawn();
        for (Mine mine : mines) mine.activateTimer();
    }

    @Override
    public void activateTimer() {
        timer.activate();
    }

    @Override
    public void updateTimer() {
        timer.tick();
    }

    @Override
    public void timeIsUp(Player player) {
        timer.startCooldown();
    }

    @Override
    public Mine[] getSpawnObjects() {
        return mines;
    }

    @Override
    public Mine mutate(int index) {
        return spawner.getSpawnObjects()[index].mutateToMine();
    }

    @Override
    public Mine[] mutateAll() {
        Mine[] mutated = new Mine[mineNum];
        Blueprint[] blueprints = spawner.getSpawnObjects();
        for (int i = 0; i<mineNum; i++) {
            mutated[i] = blueprints[i].mutateToMine();
        }
        return mutated;
    }

    @Override
    public void spawn() {
        spawner.replaceAll(spawner.randomSpawn(RANGE/2, layers));
        Mine[] newMines = mutateAll();
        System.arraycopy(newMines, 0, mines, 0, mineNum);
    }

}

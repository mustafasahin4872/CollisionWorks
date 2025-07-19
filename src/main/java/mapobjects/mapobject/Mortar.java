package mapobjects.mapobject;

import game.Player;
import helperobjects.Blueprint;
import mapobjects.component.Box;
import mapobjects.component.HPBar;
import mapobjects.component.Spawner;
import mapobjects.component.Timer;
import mapobjects.category.*;

public class Mortar extends GridObject implements Collidable, Ranged, Timed, Spawnable, HealthBearer {

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

    public Mortar(int worldIndex, int xNum, int yNum, GridObject[][][] layers) {
        this(worldIndex, xNum, yNum, layers, BASE_MINE_NUM*worldIndex);
    }

    public Mortar(int worldIndex, int xNum, int yNum, GridObject[][][] layers, int mineNum) {
        super(worldIndex, xNum, yNum, 2, 2, "src/main/resources/misc/mortar.png", true);
        this.mineNum = mineNum;
        this.layers = layers;
        collisionBox = positionBox.clone();
        rangeBox = new Box(positionBox.getCenterCoordinates(), RANGE*TILE_SIDE*2, RANGE*TILE_SIDE*2);
        timer = new Timer(PERIOD, DEFAULT_COOLDOWN/worldIndex);
        spawner = new Spawner(this);
        HPBar = new HPBar(getWidth()*4);
        mines = new Mine[mineNum];
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
            callSpawnObjects(player);
            if (areMinesComplete()) {timeIsUp(player);} //start cooldown
        } else if (!broken) {checkPlayerInRange(player);}

    }

    @Override
    public void draw() {
        super.draw();
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
        setFileName("src/main/resources/misc/brokenMortar.png");
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

    @Override
    public Mine[] getSpawnObjects() {
        return mines;
    }

    public Mine[] mutate() {
        Mine[] mutated = new Mine[mineNum];
        Blueprint[] blueprints = spawner.getSpawnObjects();
        for (int i = 0; i<mineNum; i++) {
            mutated[i] = blueprints[i].mutateToMine();
        }
        return mutated;
    }

    public void spawn() {
        spawner.replaceAll(spawner.randomSpawn(RANGE/2, mineNum, layers));
        Mine[] newMines = mutate();
        System.arraycopy(newMines, 0, mines, 0, mineNum);
    }

}

package mapobjects.initialized;

import game.Player;
import mapobjects.framework.*;

import java.util.Arrays;

import java.util.ArrayList;

import static helperobjects.CollisionMethods.checkCollision;

public class Mortar extends MapObject implements Ranged, Timed {

    private final TimeComponent timer;
    private final RangeComponent rangeComponent;

    private static final int RANGE = 4;
    private static final int BASE_MINE_NUM = 3;
    //period is max because the time of cooldown is determined by the mines
    private static final double PERIOD = Double.MAX_VALUE, DEFAULT_COOLDOWN = 3000;
    private final int mineNum;
    private final Mine[] mines;
    private final Tile[] tiles;
    private final int xTile;

    public Mortar(int worldIndex, int xNum, int yNum, Tile[] tiles, int xTile) {
        this(worldIndex, xNum, yNum, tiles, xTile, BASE_MINE_NUM*worldIndex);
    }

    public Mortar(int worldIndex, int xNum, int yNum, Tile[] tiles, int xTile, int mineNum) {
        super(worldIndex, xNum, yNum, 2, 2, "misc/misc/mortar.png", true);
        this.mineNum = mineNum;
        this.tiles = tiles;
        this.xTile = xTile;
        rangeComponent = new RangeComponent(this, RANGE);
        timer = new TimeComponent(PERIOD, DEFAULT_COOLDOWN/worldIndex);
        mines = new Mine[mineNum];
        Arrays.fill(mines, null);
    }

    @Override
    public void playerIsOn(Player player) {} //not possible

    @Override
    public void call(Player player) {
        checkCollision(player, collisionBox);
        updateTimer();
        if (isComplete()) return; //in cooldown
        if (isActive()) {
            for (Mine mine : mines) {mine.call(player);} //call all mines
            if (areMinesComplete()) {timeIsUp(player);} //start cooldown
        } else {checkPlayerInRange(player);}
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
    public void draw() {
        super.draw();
        for (Mine mine : mines) {
            if (mine!=null) mine.draw();
        }
    }

    private void renewMines() {
        int[][] coordinates = getValidCoordinates(mineNum);
        for (int i = 0; i<mineNum; i++) {
            int[] coordinate = coordinates[i];
            mines[i] = new Mine(worldIndex, coordinate[0], coordinate[1]);
        }
    }

    @Override
    public double[] getRangeBox() {
        return rangeComponent.getRangeBox();
    }

    @Override
    public void playerInRange(Player player) {
        activateTimer();
        renewMines();
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

    //returns n unique coordinate pair, checks for not being impassable as well
    private int[][] getValidCoordinates(int n) {
        ArrayList<int[]> holder = new ArrayList<>();
        holder.add(new int[]{xNum, yNum});
        holder.add(new int[]{xNum+1, yNum});
        holder.add(new int[]{xNum, yNum+1});
        holder.add(new int[]{xNum+1, yNum+1});
        int xBound = xNum - RANGE, yBound = yNum - RANGE;
        int SIDE = RANGE*2+2;
        while (holder.size()<4+n) {
            int x = xBound + (int) (Math.random()*SIDE);
            int y = yBound + (int) (Math.random()*SIDE);
            boolean valid = true;
            for (int[] held : holder) {
                if (x == held[0] && y == held[1]) {
                    valid = false;
                    break;
                }
            }
            int tileIndex = (y-1)*xTile + x - 1;
            if (tiles[tileIndex].isSolid()) {valid = false;}
            if (valid) {
                holder.add(new int[]{x, y});
            }
        }
        holder.subList(0, 4).clear();

        return holder.toArray(new int[0][]);
    }

}

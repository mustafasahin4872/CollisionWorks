package mapobjects;

import game.Player;
import lib.StdDraw;

import java.util.Arrays;
import java.util.Random;

import java.util.ArrayList;

public class Mortar extends MapObject {

    private static final int RANGE = 4;
    private static final int BASE_MINE_NUM = 3;
    private final int mineNum;
    private final Mine[] mines;
    private boolean active;
    private final Tile[] tiles;
    private final int xTile;

    public Mortar(int worldIndex, int xNum, int yNum, Tile[] tiles, int xTile) {
        this(worldIndex, xNum, yNum, tiles, xTile, BASE_MINE_NUM*worldIndex);
    }

    public Mortar(int worldIndex, int xNum, int yNum, Tile[] tiles, int xTile, int mineNum) {
        super(worldIndex, xNum, yNum);
        this.mineNum = mineNum;
        this.tiles = tiles;
        this.xTile = xTile;
        mines = new Mine[mineNum];
        Arrays.fill(mines, new Mine(worldIndex, xNum, yNum));
        set2x2CenterCoordinates();
        set2x2Coordinates();
        collisionBox[0] -= (RANGE)*TILE_SIDE;
        collisionBox[1] -= (RANGE)*TILE_SIDE;
        collisionBox[2] += (RANGE+1)*TILE_SIDE;
        collisionBox[3] += (RANGE+1)*TILE_SIDE;
    }

    public Mine[] getMines() {
        return mines;
    }

    @Override
    public void playerIsOn(Player player) {

        if (!active) {
            active = true;
            renewMines();
        }

        boolean allComplete = true;
        for (Mine mine : mines) {
            mine.playerIsOn(player);
            if (!mine.isComplete()) {
                allComplete = false;
            }
        }
        if (allComplete) {
            active = false;
        }
    }

    @Override
    public void draw() {
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], "misc/misc/mortar.png", TILE_SIDE*2, TILE_SIDE*2);
        for (Mine mine : mines) {mine.draw();}
    }

    private void renewMines() {
        int[][] coordinates = getValidCoordinates(mineNum);
        for (int i = 0; i<mineNum; i++) {
            int[] coordinate = coordinates[i];
            mines[i] = new Mine(worldIndex, coordinate[0], coordinate[1]);
        }
    }

    //returns n unique coordinate pair
    private int[][] getValidCoordinates(int n) {
        Random random = new Random();
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

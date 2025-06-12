package game;

import helperobjects.MapMaker;
import mapobjects.framework.MapObject;
import mapobjects.initialized.*;

public class GameMap {

    private final Player player;
    private final int worldIndex, levelIndex;

    private final double width, height; //default 3200x1800
    private final int xTile, yTile; //default 64x34

    private static final int RANGE = 15; //the tiles to be included in calculations
    private int[] tileRange;

    //all the elements in map is rectangular. The format of each rectangular region coordinates is
    //{x0, y0, x1, y1} unless stated otherwise.
    //(x0, y0) and (x1, y1) are the rectangle's bottom left and top right corners.

    private final MapObject[][][] layers;

    private final Tile[] tiles;
    private final Button[] buttons;
    private final Door[] doors;
    private final Chest[] chests;
    private final Coin[] coins;
    private final Sign[] signs, errorMessages;
    private final Mine[] mines;
    private final Mortar[] mortars;
    private final Shooter[] shooters;
    private final Point.WinPoint[] winPoints;
    private final Point.CheckPoint[] checkPoints;

    private final int totalCoinOnMap;

    //------------------------------------------------------------------------------------------------------------
    //CONSTRUCTOR


    public GameMap(int worldIndex, int levelIndex, Player player) {
        this(worldIndex, levelIndex, 64, 36, player);
    }

    public GameMap(int worldIndex, int levelIndex, int xTile, int yTile, Player player) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.xTile = xTile;
        this.yTile = yTile;
        this.player = player;

        width = xTile*Tile.HALF_SIDE*2;
        height = yTile*Tile.HALF_SIDE*2;

        MapMaker mapMaker = new MapMaker(worldIndex, levelIndex, xTile, yTile);
        mapMaker.mapMaker();
        layers = mapMaker.getLayers();
        tiles = mapMaker.getTiles();
        buttons = mapMaker.getButtons().toArray(new Button[0]);
        chests = mapMaker.getChests().toArray(new Chest[0]);
        doors = mapMaker.getDoors().toArray(new Door[0]);
        coins = mapMaker.getCoins().toArray(new Coin[0]);
        signs = mapMaker.getSigns().toArray(new Sign[0]);
        mines = mapMaker.getMines().toArray(new Mine[0]);
        mortars = mapMaker.getMortars().toArray(new Mortar[0]);
        shooters = mapMaker.getShooters().toArray(new Shooter[0]);
        winPoints = mapMaker.getWinPoints().toArray(new Point.WinPoint[0]);
        checkPoints = mapMaker.getCheckPoints();

        if (checkPoints.length!=0) {
            player.setSpawnPoint(checkPoints[0].getCenterCoordinates());
            player.respawn();
            errorMessages = new Sign[checkPoints.length-1];
            for (int i = 1; i<checkPoints.length; i++) {
                errorMessages[i-1] = checkPoints[i].getErrorSign();
            }
        } else {
            errorMessages = new Sign[0];
        }


        totalCoinOnMap = calculateTotalCoinAmount();
        setFrameTileRange();
    }

    //------------------------------------------------------------------------------------------------------------
    //GETTERS AND SETTERS

    public int getWorldIndex() {
        return worldIndex;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public Player getPlayer() {
        return player;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    //------------------------------------------------------------------------------------------------------------
    //POSITION AND VELOCITY UPDATES

    //the loops of the tiles,
    public void mapObjectCalls() {

        for (int y = tileRange[1]; y<tileRange[3]; y++) {
            for (int x = tileRange[0]; x<tileRange[2]; x++) {
                int index = (y-1)*xTile+x-1;
                Tile tile = tiles[index];
                tile.call(player);

                for (MapObject[][] layer : layers) {
                    MapObject mapObject = layer[y][x];
                    if (mapObject != null) {
                        mapObject.call(player);
                    }
                }

            }
        }

        callMapObjects(buttons);
        callMapObjects(doors);
        callMapObjects(chests);
        callMapObjects(coins);
        callMapObjects(signs);
        callMapObjects(mines);
        callMapObjects(mortars);
        callMapObjects(winPoints);
        callMapObjects(checkPoints);
        callMapObjects(shooters);

        if (!player.isXCollided()) {
            player.setX(player.getX()+ player.getXVelocity() * Frame.DT);
        }
        if (!player.isYCollided()) {
            player.setY(player.getY() + player.getYVelocity() * Frame.DT);
        }

        player.resetXCollided();
        player.resetYCollided();

    }

    private void callMapObjects(MapObject[] mapObjects) {
        for (MapObject mapObject : mapObjects) {
            mapObject.call(player);
        }
    }


    //---------------------------------------------------------------------------------------------
    //DRAW

    public void draw() {

        for (int y = tileRange[1]; y<= tileRange[3]; y++) {
            for (int x = tileRange[0]; x<=tileRange[2]; x++) {
                int index = (y-1)*xTile+x-1;
                tiles[index].draw();
            }
        }

        drawDrawables(buttons);
        drawDrawables(chests);
        drawDrawables(coins);
        drawDrawables(checkPoints);
        drawDrawables(winPoints);
        drawDrawables(doors);
        drawDrawables(signs);
        drawDrawables(errorMessages);
        drawDrawables(mines);
        drawDrawables(mortars);
        drawDrawables(shooters);

    }

    private static void drawDrawables(MapObject[] drawables) {
        if (drawables != null) {
            for (MapObject drawable : drawables) {
                drawable.draw();
            }
        }
    }

    //--------------------------------------------------------------------------------------------------

    private int calculateTotalCoinAmount() {
        int total = 0;
        for (Coin coin : coins) {
            total += coin.getValue();
        }
        for (Chest chest : chests) {
            total += chest.getCoinNum();
        }
        return total;
    }

    public void setFrameTileRange() {
        int xNum = (int) (player.getX() / MapObject.TILE_SIDE);
        int yNum = (int) (player.getY() / MapObject.TILE_SIDE);

        int minX = xNum - RANGE;
        int maxX = xNum + RANGE;

        if (minX<1) {
            minX = 1;
            maxX = Math.min(minX + RANGE * 2, xTile);

        } else if (maxX> xTile) {
            maxX = xTile;
            minX = Math.max(maxX - RANGE * 2, 1);

        }

        int minY = yNum - RANGE;
        int maxY = yNum + RANGE;

        if (minY<1) {
            minY = 1;
            maxY = Math.min(minY + RANGE * 2, yTile);
        }
        else if (maxY > yTile) {
            maxY = yTile;
            minY = Math.max(maxY - RANGE *2, 1);
        }

        tileRange = new int[]{minX, minY, maxX, maxY};
    }

}

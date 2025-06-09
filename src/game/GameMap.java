package game;

import helperobjects.MapMaker;
import mapobjects.*;

import java.util.ArrayList;

import static helperobjects.CollisionMethods.isIn;

public class GameMap {

    private final Player player;
    private final int worldIndex, levelIndex;
    private boolean xCollided, yCollided;

    private final double width, height; //default 3200x1800
    private final int xTile, yTile; //default 64x34

    private static final int RANGE = 15; //the tiles to be included in calculations
    private int[] tileRange;

    //all the elements in map is rectangular. The format of each rectangular region coordinates is
    //{x0, y0, x1, y1} unless stated otherwise.
    //(x0, y0) and (x1, y1) are the rectangle's bottom left and top right corners.

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
    public void playerPositionChecks() {

        if (tiles != null) {
            for (int y = tileRange[1]; y<tileRange[3]; y++) {
                for (int x = tileRange[0]; x<tileRange[2]; x++) {
                    int index = (y-1)*xTile+x-1;
                    Tile tile = tiles[index];
                    if (tile.isSolid() && tile.isApproachable()) {
                        checkCollision(tile.getCollisionBox());
                    }
                    if (isIn(player.getX(), player.getY(), tile.getCollisionBox())) {
                        tile.playerIsOn(player);
                    }
                }
            }
        }
        checkPlayerIsOn(buttons);
        checkPlayerIsOn(chests);
        checkPlayerIsOn(coins);
        checkPlayerIsOn(signs);
        checkPlayerIsOn(mines);
        checkPlayerIsOn(mortars);
        checkPlayerIsOn(winPoints);
        checkPlayerIsOn(checkPoints);

        if (doors != null) {
            for (Door door : doors) {
                checkCollision(door.getCoordinates());
                door.checkOpen();
            }
        }

        for (Mine mine : mines) {
            mine.countDown(player);
        }

        for (Mortar mortar : mortars) {
            checkCollision(mortar.getCoordinates());
            for (Mine mine : mortar.getMines()) {
                mine.countDown(player);
            }
        }

        for (Shooter shooter : shooters) {
            checkCollision(shooter.getCoordinates());
            shooter.shoot();
            ArrayList<Projectile> projectiles = shooter.getProjectiles();
            checkPlayerIsOn(projectiles.toArray(new Projectile[0]));
            for (Projectile projectile : projectiles) {
                projectile.checkWallCollision(tiles, xTile);
            }
        }


        if (!xCollided) {
            player.setX(player.getX()+ player.getXVelocity() * Frame.DT);
        }
        if (!yCollided) {
            player.setY(player.getY() + player.getYVelocity() * Frame.DT);
        }

        xCollided = false;
        yCollided = false;

    }

    //updates position and velocity if player collides to a wall
    private void checkCollision(double[] coordinates) {
        // X-axis collision
        if (!xCollided) {
            if (player.getXVelocity() > 0) {
                if (playerLineCollision(coordinates, Side.LEFT)) {
                    xCollided = true;
                    player.setX(coordinates[0] - player.getSide() / 2);
                    player.setXVelocity(0);
                }
            } else if (player.getXVelocity() < 0) {
                if (playerLineCollision(coordinates, Side.RIGHT)) {
                    xCollided = true;
                    player.setX(coordinates[2] + player.getSide() / 2);
                    player.setXVelocity(0);
                }
            }
        }
        // Y-axis collision
        if (!yCollided) {
            if (player.getYVelocity() > 0) {
                if (playerLineCollision(coordinates, Side.BOTTOM)) {
                    yCollided = true;
                    player.setY(coordinates[1] - player.getSide() / 2);
                    player.setYVelocity(0);
                }
            } else if (player.getYVelocity() < 0) {
                if (playerLineCollision(coordinates, Side.TOP)) {
                    yCollided = true;
                    player.setY(coordinates[3] + player.getSide() / 2);
                    player.setYVelocity(0);
                }
            }
        }
    }

    private void checkPlayerIsOn(MapObject[] mapObjects) {
        for (MapObject mapObject : mapObjects) {
            mapObject.isPlayerOn(player);
        }
    }


    //----------------------------------------------------------------------------------------------
    //COLLISIONS

    public enum Side {TOP, BOTTOM, RIGHT, LEFT}
    private boolean playerLineCollision(double[] obstacle, Side side) {
        double x0 = obstacle[0], y0 = obstacle[1], x1 = obstacle[2], y1 = obstacle[3];

        double x = player.getX();
        double y = player.getY();
        double nextX = x + player.getXVelocity() * Frame.DT;
        double nextY = y + player.getYVelocity() * Frame.DT;
        double halfSide = player.getSide() / 2;

        if (side == Side.TOP) {
            return xLineCollision(x - halfSide, y - halfSide, nextY - halfSide, x0, x1, y1) ||
                    xLineCollision(x + halfSide, y - halfSide, nextY - halfSide, x0, x1, y1) ||
                    xLineCollision(x, y - halfSide, nextY - halfSide, x0, x1, y1);
        } else if (side == Side.BOTTOM) {
            return xLineCollision(x - halfSide, y + halfSide, nextY + halfSide, x0, x1, y0) ||
                    xLineCollision(x + halfSide, y + halfSide, nextY + halfSide, x0, x1, y0) ||
                    xLineCollision(x, y + halfSide, nextY + halfSide, x0, x1, y0);
        } else if (side == Side.LEFT) {
            return yLineCollision(x + halfSide, y - halfSide, nextX - halfSide, y0, y1, x0) ||
                    yLineCollision(x + halfSide, y + halfSide, nextX - halfSide, y0, y1, x0) ||
                    yLineCollision(x + halfSide, y, nextX - halfSide, y0, y1, x0);
        } else { // RIGHT
            return yLineCollision(x - halfSide, y - halfSide, nextX + halfSide, y0, y1, x1) ||
                    yLineCollision(x - halfSide, y + halfSide, nextX + halfSide, y0, y1, x1) ||
                    yLineCollision(x - halfSide, y, nextX + halfSide, y0, y1, x1);
        }
    }

    private boolean xLineCollision(double x, double y, double nextY, double x0, double x1, double y0) {
        // Check if y0 is between y and nextY, or if both y and nextY are equal to y0
        if (y <= y0 && y0 <= nextY || y >= y0 && y0 >= nextY) {

            // Handle the case where the line is exactly horizontal (y == nextY == y0)
            if (y == nextY && y != y0) {
                return false;
            }
            return x0 < x && x < x1;
        }
        return false;
    }
    private boolean yLineCollision(double x, double y, double nextX, double y0, double y1, double x0) {
        // Check if x0 is between x and nextX, or if both x and nextX are equal to x0
        if (x <= x0 && x0 <= nextX || x >= x0 && x0 >= nextX) {

            // Handle the case where the line is exactly vertical (x == nextX == x0)
            if (x == nextX && x != x0) {
                return false;
            }
            return y0<y && y<y1;
        }
        return false;

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
            total += coin.getCoinAmount();
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

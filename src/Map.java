public class Map {

    private final Player player;

    private int worldIndex, levelIndex;

    private boolean xCollided = false, yCollided = false;

    //every map has boundaries of 0-3200 and 0-1800 for x and y.
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

    private final int totalCoinOnMap;

    private final double[] winArea = {3200, 100, 3500, 600};

    //------------------------------------------------------------------------------------------------------------
    //CONSTRUCTOR


    public Map(int worldIndex, int levelIndex) {
        this(worldIndex, levelIndex, 64, 36);
    }

    public Map(int worldIndex, int levelIndex, int xTile, int yTile) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.xTile = xTile;
        this.yTile = yTile;
        width = xTile*Tile.HALF_SIDE*2;
        height = yTile*Tile.HALF_SIDE*2;
        player = new Player();
        MapMaker mapMaker = new MapMaker(worldIndex, levelIndex);
        mapMaker.mapMaker();
        tiles = mapMaker.getTiles();
        buttons = mapMaker.getButtons().toArray(new Button[0]);
        chests = mapMaker.getChests().toArray(new Chest[0]);
        doors = mapMaker.getDoors().toArray(new Door[0]);
        coins = mapMaker.getCoins().toArray(new Coin[0]);
        totalCoinOnMap = calculateTotalCoinAmount();
        setFrameTileRange();
    }

    //------------------------------------------------------------------------------------------------------------
    //GETTERS AND SETTERS

    public boolean stagePassed() {
        return playerIsIn(player.getX(), player.getY(), winArea);
    }

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

    public int getxTile() {
        return xTile;
    }

    public int getyTile() {
        return yTile;
    }

    //------------------------------------------------------------------------------------------------------------
    //POSITION AND VELOCITY UPDATES

    //the loops of the tiles,
    public void playerPositionChecks() {

        if (tiles != null) {
            for (int y = tileRange[1]; y<=tileRange[3]; y++) {
                for (int x = tileRange[0]; x<=tileRange[2]; x++) {
                    int index = (y-1)*64+x-1;
                    Tile tile = tiles[index];
                    if (tile.isSolid() && tile.isApproachable()) {
                        checkCollision(tile.getCoordinates());
                    }
                    if (playerIsIn(player, tile.getCoordinates())) {
                        tile.playerIsOn(player);
                    }
                }
            }
        }
        checkPlayerIsOn(buttons);
        checkPlayerIsOn(chests);
        checkPlayerIsOn(coins);


        if (doors != null) {
            for (Door door : doors) {
                checkCollision(door.getCoordinates());
                door.checkOpen(player);
            }
        }

        if (!xCollided) {
            player.setX(player.getX()+ player.getxVelocity()*Frame.DT);
        }
        if (!yCollided) {
            player.setY(player.getY() + player.getyVelocity() * Frame.DT);
        }

        xCollided = false;
        yCollided = false;

    }

    //updates position and velocity if player collides to a wall
    private void checkCollision(double[] coordinates) {
        // X-axis collision
        if (!xCollided) {
            if (player.getxVelocity() > 0) {
                if (playerLineCollision(coordinates, Side.LEFT)) {
                    xCollided = true;
                    player.setX(coordinates[0] - Player.getSide() / 2);
                    player.setxVelocity(0);
                }
            } else if (player.getxVelocity() < 0) {
                if (playerLineCollision(coordinates, Side.RIGHT)) {
                    xCollided = true;
                    player.setX(coordinates[2] + Player.getSide() / 2);
                    player.setxVelocity(0);
                }
            }
        }
        // Y-axis collision
        if (!yCollided) {
            if (player.getyVelocity() > 0) {
                if (playerLineCollision(coordinates, Side.BOTTOM)) {
                    yCollided = true;
                    player.setY(coordinates[1] - Player.getSide() / 2);
                    player.setyVelocity(0);
                }
            } else if (player.getyVelocity() < 0) {
                if (playerLineCollision(coordinates, Side.TOP)) {
                    yCollided = true;
                    player.setY(coordinates[3] + Player.getSide() / 2);
                    player.setyVelocity(0);
                }
            }
        }
    }

    private void checkPlayerIsOn(Passable[] passables) {
        if (passables != null) {
            for (Passable passable : passables) {
                if (playerIsIn(player, passable.getCoordinates())) {
                    passable.playerIsOn(player);
                }
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    //COLLISIONS

    public enum Side {TOP, BOTTOM, RIGHT, LEFT}
    private boolean playerLineCollision(double[] obstacle, Side side) {
        double x0 = obstacle[0], y0 = obstacle[1], x1 = obstacle[2], y1 = obstacle[3];

        double x = player.getX();
        double y = player.getY();
        double nextX = x + player.getxVelocity() * Frame.DT;
        double nextY = y + player.getyVelocity() * Frame.DT;
        double halfSide = Player.getSide() / 2;

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

    public static boolean playerIsIn(double x, double y, double[] obstacle) {
        double halfSide = Player.getSide() / 2;
        return isIn(x + halfSide, y + halfSide, obstacle) ||
                isIn(x + halfSide, y - halfSide, obstacle) ||
                isIn(x - halfSide, y + halfSide, obstacle) ||
                isIn(x - halfSide, y - halfSide, obstacle);
    }

    public static boolean playerIsIn(Player player, double[] obstacle) {
        double x = player.getX(), y = player.getY();
        double halfSide = Player.getSide() / 2;
        return isIn(x + halfSide, y + halfSide, obstacle) ||
                isIn(x + halfSide, y - halfSide, obstacle) ||
                isIn(x - halfSide, y + halfSide, obstacle) ||
                isIn(x - halfSide, y - halfSide, obstacle);
    }


    public static boolean isIn(double x, double y, double[] obstacle) {
        return ((x > obstacle[0]) && (x < obstacle[2]) && (y > obstacle[1]) && (y < obstacle[3]));
    }


    //---------------------------------------------------------------------------------------------
    //DRAW

    public void draw() {

        for (int y = tileRange[1]; y<= tileRange[3]; y++) {
            for (int x = tileRange[0]; x<=tileRange[2]; x++) {
                int index = (y-1)*64+x-1;
                tiles[index].draw();
            }
        }

        drawDrawables(buttons);
        drawDrawables(chests);
        drawDrawables(coins);
        drawDrawables(doors);

        player.draw();
    }

    private static void drawDrawables(Drawable[] drawables) {
        if (drawables!= null) {
            for (Drawable drawable : drawables) {
                drawable.draw();
            }
        }
    }

    public static void drawRectangle(double[] region) {
        StdDraw.filledRectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
    }

    public static void drawRectangleOutline(double[] region) {
        StdDraw.rectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
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
        int tileSize = Tile.HALF_SIDE * 2;

        int xNum = (int) (player.getX() / tileSize);
        int yNum = (int) ((height - player.getY()) / tileSize); // FLIP THE Y

        int minX = xNum - RANGE;
        int maxX = xNum + RANGE;

        if (minX<1) {
            minX = 1;
            maxX = minX+ RANGE *2;
        } else if (maxX> xTile) {
            maxX = xTile;
            minX = maxX - RANGE * 2;
        }

        int minY = yNum - RANGE;
        int maxY = yNum + RANGE;

        if (minY<1) {
            minY = 1;
            maxY = minY+ RANGE *2;
        }
        else if (maxY > yTile) {
            maxY = yTile;
            minY = maxY - RANGE *2;
        }

        tileRange = new int[]{minX, minY, maxX, maxY};
    }
}

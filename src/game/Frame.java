package game;

import mapobjects.initialized.Sign;
import lib.StdDraw;

import java.awt.event.KeyEvent;

public class Frame {

    //the area of player's sight
    public static final double X_SCALE = 900, Y_SCALE = 600;

    public static final double DT = 1; //time step between each frame
    public static final int PAUSE = 20; //pause value for each frame

    private final GameMap gameMap;
    private final Player player;

    public double frameX, frameY;

    private static final int[] RIGHT_CODES = {KeyEvent.VK_RIGHT, KeyEvent.VK_D};
    private static final int[] UP_CODES = {KeyEvent.VK_UP, KeyEvent.VK_W};
    private static final int[] LEFT_CODES = {KeyEvent.VK_LEFT, KeyEvent.VK_A};
    private static final int[] DOWN_CODES = {KeyEvent.VK_DOWN, KeyEvent.VK_S};

    public Frame(GameMap gameMap, Player player) {
        this.gameMap = gameMap;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    //returns special codes for different win areas
    public Player.PASSCODE play() {

        while (player.getPassCode() == Player.PASSCODE.ZERO) {
            long startTime = System.currentTimeMillis();

            //takes input and sets regarding player data fields
            handleInput(player);
            //uses moveDirection to update velocities
            player.updateVelocity();
            //checks for collisions with player and the on tile effects, also updates the map objects
            gameMap.callMapObjects();
            //updates player position and other attributes at last
            player.update();
            //checks if player died, and calls corresponding methods for the situation
            player.checkPlayerDied();

            //sets the frame to center player if it is not in the edges of gameMap
            //if the frame would get out of the gameMap, set frame to show up to edge
            setFrameCenter();
            gameMap.setFrameTileRange();
            Sign.updateDisplayCenter(frameX, frameY);

            //draws everything
            StdDraw.clear();
            draw();
            StdDraw.show();

            player.setXDirection(0);
            player.setYDirection(0);
            player.setPlayerTime();

            int timeElapsed = (int)(System.currentTimeMillis()- startTime);
            if (timeElapsed<PAUSE) {
                StdDraw.pause(PAUSE-timeElapsed);
            }
        }

        return player.getPassCode();
    }

    public GameMap getNextMap() {
        GameMap nextGameMap;
        if (gameMap.getLevelIndex()==12) {
            if (gameMap.getWorldIndex()==4) {
                nextGameMap = new GameMap(1,1, player);
            } else {
                nextGameMap = new GameMap(gameMap.getWorldIndex()+1, 1, player);
            }
        } else {
            nextGameMap = new GameMap(gameMap.getWorldIndex(), gameMap.getLevelIndex()+1, player);
        }
        return nextGameMap;
    }

    public void draw() {
        gameMap.draw();
        StdDraw.setPenColor();
        StdDraw.setFont();
        StdDraw.textLeft(frameX - X_SCALE/2 + 10, frameY + Y_SCALE/2 - 10, "x-y: %.1f %.1f".formatted(player.getX(), player.getY()));
        StdDraw.textLeft(frameX - X_SCALE/2 + 10, frameY + Y_SCALE/2 - 30, "vx: %.1f".formatted(player.getXVelocity()));
        StdDraw.textLeft(frameX - X_SCALE/2 + 10, frameY + Y_SCALE/2 - 50, "vy: %.1f".formatted(player.getYVelocity()));
        player.draw();
        player.drawHPBar(frameX, frameY);
        player.drawCoinAmount(frameX, frameY);
        player.drawLifeAmount(frameX, frameY);
    }

    public void handleInput(Player player) {

        for (int RIGHT_CODE : RIGHT_CODES) {
            if (StdDraw.isKeyPressed(RIGHT_CODE)) {
                player.setXDirection(1);
            }
        }
        for (int LEFT_CODE : LEFT_CODES) {
                if (StdDraw.isKeyPressed(LEFT_CODE)) {
                player.setXDirection(-1);
            }
        }
        for (int UP_CODE : UP_CODES) {
            if (StdDraw.isKeyPressed(UP_CODE)) {
                player.setYDirection(-1);
            }
        }
        for (int DOWN_CODE : DOWN_CODES) {
            if (StdDraw.isKeyPressed(DOWN_CODE)) {
                player.setYDirection(1);
            }
        }

    }

    private void setFrameCenter() {
        if (player.getX()-X_SCALE/2<0) {
            StdDraw.setXscale(0, X_SCALE);
            frameX = X_SCALE/2;
        } else if (player.getX()+X_SCALE/2> gameMap.getWidth()) {
            StdDraw.setXscale(gameMap.getWidth() -X_SCALE, gameMap.getWidth());
            frameX = gameMap.getWidth() -X_SCALE/2;
        } else {
            StdDraw.setXscale(player.getX() - Frame.X_SCALE / 2, player.getX() + Frame.X_SCALE / 2);
            frameX = player.getX();
        }

        if (player.getY()-Y_SCALE/2<0) {
            StdDraw.setYscale(Y_SCALE, 0);
            frameY = Y_SCALE/2;
        } else {
            if (player.getY()+Y_SCALE/2> gameMap.getHeight()) {
                StdDraw.setYscale(gameMap.getHeight(), gameMap.getHeight() -Y_SCALE);
                frameY = gameMap.getHeight() -Y_SCALE/2;
            } else {
                StdDraw.setYscale(player.getY() + Frame.Y_SCALE / 2, player.getY() - Frame.Y_SCALE / 2);
                frameY = player.getY();
            }
        }
    }

}

package game;

import mapobjects.Sign;
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
    public int play() {

        int passCode = 0;
        while (passCode == 0 && !player.isPlayerDead()) {
            passCode = gameMap.stagePassed();
            long startTime = System.currentTimeMillis();

            //takes input and sets moveDirection variables
            handleInput(player);
            //uses moveDirection to update velocities
            player.updateVelocity();
            //with the updated velocities, sets player's x and y coordinates
            gameMap.playerPositionChecks();

            //sets the frame to center player if it is not in the edges of gameMap
            //if the frame would get out of the gameMap, set frame to show up to edge
            setFrame();
            gameMap.setFrameTileRange();
            Sign.updateDisplayCenter(frameX, frameY);

            //draws everything
            StdDraw.clear();
            draw();
            StdDraw.show();

            player.setxDirection(0);
            player.setyDirection(0);

            int timeElapsed = (int)(System.currentTimeMillis()- startTime);
            if (timeElapsed<PAUSE) {
                StdDraw.pause(Frame.PAUSE-timeElapsed);
            }
        }

        return passCode;
    }

    public GameMap getNextMap() {
        GameMap nextGameMap;
        if (gameMap.getLevelIndex()==12) {
            if (gameMap.getWorldIndex()==4) {
                nextGameMap = new GameMap(1,1, player, gameMap.getAccessories());
            } else {
                nextGameMap = new GameMap(gameMap.getWorldIndex()+1, 1, player, gameMap.getAccessories());
            }
        } else {
            nextGameMap = new GameMap(gameMap.getWorldIndex(), gameMap.getLevelIndex()+1, player, gameMap.getAccessories());
        }
        return nextGameMap;
    }

    public void draw() {
        gameMap.draw();
        StdDraw.setPenColor();
        StdDraw.setFont();
        StdDraw.textLeft(frameX - X_SCALE/2 + 10, frameY + Y_SCALE/2 - 10, "x-y: %.1f %.1f".formatted(player.getX(), player.getY()));
        StdDraw.textLeft(frameX - X_SCALE/2 + 10, frameY + Y_SCALE/2 - 30, "vx: %.1f".formatted(player.getxVelocity()));
        StdDraw.textLeft(frameX - X_SCALE/2 + 10, frameY + Y_SCALE/2 - 50, "vy: %.1f".formatted(player.getyVelocity()));
        player.drawHPBar(frameX, frameY);
        player.drawCoinAmount(frameX, frameY);
    }

    public void handleInput(Player player) {
        int RIGHT_CODE = KeyEvent.VK_RIGHT;
        int UP_CODE = KeyEvent.VK_UP;
        int LEFT_CODE = KeyEvent.VK_LEFT;
        int DOWN_CODE = KeyEvent.VK_DOWN;

        int[] keyCodes = {RIGHT_CODE, UP_CODE, LEFT_CODE, DOWN_CODE};

        if (StdDraw.isKeyPressed(keyCodes[0])) {
            player.setxDirection(1);
        } else if (StdDraw.isKeyPressed(keyCodes[2])) {
            player.setxDirection(-1);
        }
        if (StdDraw.isKeyPressed(keyCodes[1])) {
            player.setyDirection(-1);
        }  else if (StdDraw.isKeyPressed(keyCodes[3])) {
            player.setyDirection(1);
        }
    }

    private void setFrame() {
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

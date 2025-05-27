import java.awt.event.KeyEvent;

public class Frame {

    //the area of player's sight
    public static final double X_SCALE = 900;
    public static final double Y_SCALE = 600;

    public static double frameX;
    public static double frameY;

    //time step between each frame
    public static final double DT = 1;
    //pause value for each frame
    public static final int PAUSE = 20;

    private final Map map;
    private final Player player;

    public Frame(Map map) {
        this.map = map;
        player = map.getPlayer();
    }

    public void play() {

        //the gameplay
        while (!map.stagePassed() && !player.isPlayerDead()) {
            long startTime = System.currentTimeMillis();

            //takes input and sets moveDirection variables
            handleInput(player);
            //uses moveDirection to update velocities
            player.updateVelocity();
            //with the updated velocities, sets player's x and y coordinates
            map.playerPositionChecks();
            player.readBuffs();

            //sets the frame to center player if it is not in the edges of map
            //if the frame would get out of the map, set frame to show up to edge
            setFrame();
            map.setFrameTileRange();

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

        Map nextMap = getNextMap();
        Frame nextFrame = new Frame(nextMap);
        nextFrame.play();

    }

    private Map getNextMap() {
        Map nextMap;
        if (map.getLevelIndex()==15) {
            if (map.getWorldIndex()==3) {
                nextMap = new Map(1,1);
            } else {
                nextMap = new Map(map.getWorldIndex(), 1);
            }
        } else {
            nextMap = new Map(map.getWorldIndex(), map.getLevelIndex()+1);
        }
        return nextMap;
    }

    public void draw() {
        map.draw();
        StdDraw.setPenColor();
        StdDraw.setFont();
        StdDraw.textLeft(frameX - X_SCALE/2 + 10, frameY - Y_SCALE/2 + 10, "x-y: %.1f %.1f".formatted(player.getX(), player.getY()));
        StdDraw.textLeft(frameX - X_SCALE/2 + 10, frameY - Y_SCALE/2 + 30, "vx: %.1f".formatted(player.getxVelocity()));
        StdDraw.textLeft(frameX - X_SCALE/2 + 10, frameY - Y_SCALE/2 + 50, "vy: %.1f".formatted(player.getyVelocity()));
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
            player.setyDirection(1);
        }  else if (StdDraw.isKeyPressed(keyCodes[3])) {
            player.setyDirection(-1);
        }
    }

    private void setFrame() {
        if (player.getX()-X_SCALE/2<0) {
            StdDraw.setXscale(0, X_SCALE);
            frameX = X_SCALE/2;
        } else if (player.getX()+X_SCALE/2>map.getWidth()) {
            StdDraw.setXscale(map.getWidth() -X_SCALE, map.getWidth());
            frameX = map.getWidth() -X_SCALE/2;
        } else {
            StdDraw.setXscale(player.getX() - Frame.X_SCALE / 2, player.getX() + Frame.X_SCALE / 2);
            frameX = player.getX();
        }

        if (player.getY()-Y_SCALE/2<0) {
            StdDraw.setYscale(0, Y_SCALE);
            frameY = Y_SCALE/2;
        } else {
            if (player.getY()+Y_SCALE/2> map.getHeight()) {
                StdDraw.setYscale(map.getHeight() -Y_SCALE, map.getHeight());
                frameY = map.getHeight() -Y_SCALE/2;
            } else {
                StdDraw.setYscale(player.getY() - Frame.Y_SCALE / 2, player.getY() + Frame.Y_SCALE / 2);
                frameY = player.getY();
            }
        }
    }

}

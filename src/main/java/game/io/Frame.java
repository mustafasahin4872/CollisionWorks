package game.io;

import lib.StdDraw;

// handles input and screen for given player and map
public class Frame {

    //the area of player's sight
    public static final double X_SCALE = 900, Y_SCALE = 600;

    public static final double DT = 1; //time step between each frame
    public static final int PAUSE = 20; //pause value for each frame

    private final double width, height;

    // intakes the map's width and height
    public Frame(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public static void setCanvas() {
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize((int) X_SCALE, (int)Y_SCALE);
    }

    public static void setDefaultScale() {
        StdDraw.setXscale(0, X_SCALE);
        StdDraw.setYscale(Y_SCALE, 0);
    }

    public static void clear() {
        StdDraw.clear();
    }

    public static void show() {
        StdDraw.show();
    }

    public static void pause(int time) {
        StdDraw.pause(time);
    }

    public double[] setFrameCenter(double targetX, double targetY) {
        double frameX, frameY;
        if (targetX-X_SCALE/2<0) {
            StdDraw.setXscale(0, X_SCALE);
            frameX = X_SCALE/2;
        } else if (targetX+X_SCALE/2> width) {
            StdDraw.setXscale(width -X_SCALE, width);
            frameX = width -X_SCALE/2;
        } else {
            StdDraw.setXscale(targetX - Frame.X_SCALE / 2, targetX + Frame.X_SCALE / 2);
            frameX = targetX;
        }

        if (targetY-Y_SCALE/2<0) {
            StdDraw.setYscale(Y_SCALE, 0);
            frameY = Y_SCALE/2;
        } else {
            if (targetY+Y_SCALE/2> height) {
                StdDraw.setYscale(height, height -Y_SCALE);
                frameY = height -Y_SCALE/2;
            } else {
                StdDraw.setYscale(targetY + Frame.Y_SCALE / 2, targetY - Frame.Y_SCALE / 2);
                frameY = targetY;
            }
        }

        return new double[]{frameX, frameY};
    }

}

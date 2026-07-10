package helpers.utils;

import game.core.Frame;
import mapobjects.components.Box;

// a box, stays the same with respect to frame
public class FrameBox extends Box {

    public static final double CENTER_X = Frame.X_SCALE/2;
    public static final double CENTER_Y = Frame.Y_SCALE/2;
    public static double frameX, frameY;
    private final Box frameBox;

    public FrameBox(double centerX, double centerY, double width, double height) {
        super(centerX, centerY, width, height);
        frameBox = new Box(
            frameX - CENTER_X + getCenterX(),
            frameY - CENTER_Y + getCenterY(),
            getWidth(),
            getHeight()
        );
    }

    public static void updateCenter(double frameX, double frameY) {
        FrameBox.frameX = frameX;
        FrameBox.frameY = frameY;
    }

    public void update() {
        frameBox.setCenterX(frameX - CENTER_X + getCenterX());
        frameBox.setCenterY(frameY - CENTER_Y + getCenterY());
        frameBox.setWidth(getWidth());
        frameBox.setHeight(getHeight());
    }

    public Box getFrameBox() {
        return frameBox;
    }

}

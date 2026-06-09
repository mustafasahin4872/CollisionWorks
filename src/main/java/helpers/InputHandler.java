package helpers;

import lib.StdDraw;

import java.awt.event.KeyEvent;

public class InputHandler {

    public static class ArrowData {
        public int xDirection, yDirection;
        public boolean space;

        public ArrowData(int xDirection, int yDirection, boolean space) {
            this.xDirection = xDirection;
            this.yDirection = yDirection;
            this.space = space;
        }

        public ArrowData() {}
    }

    public static class MouseData {
        public double mouseX, mouseY;
        public boolean pressed;

        public MouseData(double mouseX, double mouseY, boolean pressed) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.pressed = pressed;
        }

        public MouseData() {}
    }

    private static final int[] RIGHT_CODES = {KeyEvent.VK_RIGHT, KeyEvent.VK_D};
    private static final int[] UP_CODES = {KeyEvent.VK_UP, KeyEvent.VK_W};
    private static final int[] LEFT_CODES = {KeyEvent.VK_LEFT, KeyEvent.VK_A};
    private static final int[] DOWN_CODES = {KeyEvent.VK_DOWN, KeyEvent.VK_S};
    private static final int[] SHOOT_CODES = {KeyEvent.VK_SPACE};

    private final ArrowData arrowData = new ArrowData();
    private final MouseData mouseData = new MouseData();

    public ArrowData getArrowData() {
        return arrowData;
    }

    public MouseData getMouseData() {
        return mouseData;
    }

    public void takeInput() {
        handleArrowInput();
        handleMouseInput();
    }

    private void handleArrowInput() {

        int xDirection = 0, yDirection = 0;
        boolean space = false;

        for (int RIGHT_CODE : RIGHT_CODES) {
            if (StdDraw.isKeyPressed(RIGHT_CODE)) {
                xDirection = 1;
                break;
            }
        }
        for (int LEFT_CODE : LEFT_CODES) {
            if (StdDraw.isKeyPressed(LEFT_CODE)) {
                xDirection = -1;
                break;
            }
        }
        for (int UP_CODE : UP_CODES) {
            if (StdDraw.isKeyPressed(UP_CODE)) {
                yDirection = -1;
                break;
            }
        }
        for (int DOWN_CODE : DOWN_CODES) {
            if (StdDraw.isKeyPressed(DOWN_CODE)) {
                yDirection = 1;
                break;
            }
        }
        for (int SHOOT_CODE : SHOOT_CODES) {
            if (StdDraw.isKeyPressed(SHOOT_CODE)) {
                space = true;
                break;
            }
        }

        arrowData.xDirection = xDirection;
        arrowData.yDirection = yDirection;
        arrowData.space = space;
    }

    private void handleMouseInput() {
        mouseData.pressed = StdDraw.isMousePressed();
        mouseData.mouseX = StdDraw.mouseX();
        mouseData.mouseY = StdDraw.mouseY();
    }

}

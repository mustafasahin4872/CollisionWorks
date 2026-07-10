package game.io;

import lib.StdDraw;

import java.awt.event.KeyEvent;

public class InputHandler {

    private final ArrowData arrowData = new ArrowData();
    private final MouseData mouseData = new MouseData();

    public ArrowData getArrowData() {
        return arrowData;
    }
    public MouseData getMouseData() {
        return mouseData;
    }

    public void takeInput() {
        arrowData.handleInput();
        mouseData.handleInput();
    }


    public static class ArrowData {

        private static final int[] RIGHT_CODES = {KeyEvent.VK_RIGHT, KeyEvent.VK_D};
        private static final int[] UP_CODES = {KeyEvent.VK_UP, KeyEvent.VK_W};
        private static final int[] LEFT_CODES = {KeyEvent.VK_LEFT, KeyEvent.VK_A};
        private static final int[] DOWN_CODES = {KeyEvent.VK_DOWN, KeyEvent.VK_S};
        private static final int[] SHOOT_CODES = {KeyEvent.VK_SPACE};

        public int xDirection, yDirection;
        public boolean space;

        private void handleInput() {

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

            this.xDirection = xDirection;
            this.yDirection = yDirection;
            this.space = space;
        }
    }

    public static class MouseData {
        public double mouseX, mouseY;
        public boolean clicked;
        private boolean pressed = false;

        private void handleInput() {

                clicked = pressed && !StdDraw.isMousePressed();
                pressed = StdDraw.isMousePressed();

                mouseX = StdDraw.mouseX();
                mouseY = StdDraw.mouseY();

        }
    }

}

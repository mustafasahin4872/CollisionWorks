package mapobjects;

import game.Player;
import lib.StdDraw;

import java.awt.*;
import game.Frame;

import static helperobjects.DrawMethods.*;

public class Sign extends MapObject {

    private final String[] messages;
    private final boolean displaySign;
    private boolean displayMessage;
    private static final double SPACE_ON_SIDE = 10, CHAR_WIDTH = 0.43*32, CHAR_HEIGHT=32;
    private final double displayHalfWidth, displayHalfHeight;
    private static final double[] DISPLAY_CENTER = new double[2];
    private final double[] displayCoordinates = new double[4];
    private static final Font FONT = new Font("Arial", Font.PLAIN, 32);
    private static final Color COLOR = new Color(103, 2, 9);

    public Sign(int worldIndex, int xNum, int yNum, String[] messages) {
        this(worldIndex, xNum, yNum, messages, true);
    }

    public Sign(int worldIndex, int xNum, int yNum, String[] messages, boolean displaySign) {
        super(worldIndex, xNum, yNum, "misc/signImages/sign.png");
        this.messages = messages;
        this.displaySign = displaySign;

        double[] dimensions = calculateDimensions(messages);
        this.displayHalfWidth = dimensions[0];
        this.displayHalfHeight = dimensions[1];
    }

    private static double[] calculateDimensions(String[] messages) {
        int maxLength = 0;
        for (String message : messages) {
            maxLength = Math.max(maxLength, message.length());
        }

        double halfWidth = SPACE_ON_SIDE + maxLength / 2.0 * CHAR_WIDTH;
        double halfHeight = SPACE_ON_SIDE + messages.length / 2.0 * CHAR_HEIGHT;

        return new double[]{halfWidth, halfHeight};
    }

    @Override
    public void draw() {
        if (displaySign) {
            super.draw();
        }
        if (displayMessage) {
            display();
            displayMessage = false;
        }
    }

    public void setDisplay() {
        displayMessage = true;
    }

    @Override
    public void playerIsOn(Player player) {
        setDisplay();
    }

    private void display() {
        updateDisplayCoordinates();
        StdDraw.setPenColor(COLOR);
        drawRectangle(displayCoordinates);
        StdDraw.setPenColor(StdDraw.BLACK);
        drawRectangleOutline(displayCoordinates);
        StdDraw.setFont(FONT);

        double baseHeight = DISPLAY_CENTER[1] - displayHalfHeight + SPACE_ON_SIDE + CHAR_HEIGHT / 2;
        for (int i = 0; i < messages.length; i++) {
            StdDraw.text(DISPLAY_CENTER[0], baseHeight + i * CHAR_HEIGHT, messages[i]);
        }
    }

    public static void updateDisplayCenter(double frameX, double frameY) {
        DISPLAY_CENTER[0] = frameX;
        DISPLAY_CENTER[1] = frameY - Frame.Y_SCALE * (.3);
    }

    private void updateDisplayCoordinates() {
        displayCoordinates[0] = DISPLAY_CENTER[0] - displayHalfWidth;
        displayCoordinates[1] = DISPLAY_CENTER[1] - displayHalfHeight;
        displayCoordinates[2] = DISPLAY_CENTER[0] + displayHalfWidth;
        displayCoordinates[3] = DISPLAY_CENTER[1] + displayHalfHeight;
    }

}
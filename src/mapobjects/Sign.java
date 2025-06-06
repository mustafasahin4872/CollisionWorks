package mapobjects;

import game.Player;
import game.Selection;
import lib.StdDraw;

import java.awt.*;
import game.Frame;

import static helperobjects.DrawMethods.*;

public class Sign extends MapObject {

    private final String[] messages;
    private final boolean displaySign;

    private boolean displayMessage;
    private static final double SPACE_ON_SIDE = 10, CHAR_WIDTH = 0.43*32, CHAR_HEIGHT=32;
    private final double halfWidth, halfHeight;
    private static double[] displayCenter = new double[2], displayCoordinates = new double[4];
    private static final Font FONT = new Font("Arial", Font.PLAIN, 32);
    private final Color color;

    public Sign(int worldIndex, int xNum, int yNum, String[] messages) {
        this(worldIndex, xNum, yNum, messages, true);
    }

    public Sign(int worldIndex, int xNum, int yNum, String[] messages, boolean displaySign) {
        super(worldIndex, xNum, yNum);
        this.messages = messages;
        this.displaySign = displaySign;

        int maxLength = 0;
        for (String message : messages) {
            maxLength = Math.max(maxLength, message.length());
        }

        halfWidth = SPACE_ON_SIDE + maxLength/2.0*CHAR_WIDTH;
        halfHeight = SPACE_ON_SIDE + messages.length/2.0*CHAR_HEIGHT;
        color = Selection.WORLD_COLORS[worldIndex];
    }

    @Override
    public void draw() {
        if (displaySign) {
            StdDraw.picture(centerCoordinates[0], centerCoordinates[1], "misc/signImages/sign.png", TILE_SIDE, TILE_SIDE);
        }
        if (!displayMessage) return;
        updateDisplayCoordinates();
        StdDraw.setPenColor(color);
        drawRectangle(displayCoordinates);
        StdDraw.setPenColor(StdDraw.BLACK);
        drawRectangleOutline(displayCoordinates);
        StdDraw.setFont(FONT);

        for (int i = 0; i < messages.length; i++) {
            double messageHeight = displayCenter[1] - halfHeight + SPACE_ON_SIDE + CHAR_HEIGHT/2 + i*CHAR_HEIGHT;
            StdDraw.text(displayCenter[0], messageHeight, messages[i]);
        }
        displayMessage = false;
    }

    @Override
    public void playerIsOn(Player player) {
        displayMessage = true;
    }

    public static void updateDisplayCenter(double frameX, double frameY) {
        displayCenter = new double[]{frameX, frameY- Frame.Y_SCALE*(.3)};
    }

    public void updateDisplayCoordinates() {
        displayCoordinates = new double[]{displayCenter[0]-halfWidth, displayCenter[1]-halfHeight, displayCenter[0]+halfWidth, displayCenter[1]+halfHeight};
    }


}

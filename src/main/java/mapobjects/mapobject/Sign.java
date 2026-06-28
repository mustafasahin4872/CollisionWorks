package mapobjects.mapobject;

import helpers.TextDisplay;
import lib.StdDraw;

import java.awt.*;
import game.Frame;
import mapobjects.component.Box;
import mapobjects.category.GridObject;
import mapobjects.category.OnEffector;

import static helpers.DrawMethods.*;

public class Sign extends GridObject implements OnEffector {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 32);
    private static final Color COLOR = new Color(103, 2, 9);

    private final Box effectBox;

    private final boolean displaySign;
    private boolean displayMessage;

    private final TextDisplay textDisplay;


    public Sign(int worldIndex, int xNum, int yNum, String[] messages) {
        this(worldIndex, xNum, yNum, messages, true);
    }

    public Sign(int worldIndex, int xNum, int yNum, String[] messages, boolean displaySign) {
        super(worldIndex, xNum, yNum);
        effectBox = positionBox.clone();

        this.displaySign = displaySign;

        double[] dimensions = calculateDimensions(messages);
        Box displayBox = new Box(Frame.X_SCALE / 2, 0.3 * Frame.Y_SCALE / 2, dimensions[0], dimensions[1]);
        textDisplay = new TextDisplay(displayBox, messages, StdDraw.BLACK, FONT);
    }


    private static double[] calculateDimensions(String[] messages) {
        double maxLength = 0;
        for (String message : messages) {
            maxLength = Math.max(maxLength, getTextWidth(message, FONT));
        }
        double charHeight = getFontHeight(FONT);

        double width = maxLength;
        double height = messages.length * charHeight;

        return new double[]{width, height};
    }

    public void setDisplay() {
        displayMessage = true;
    }

    @Override
    public Box getEffectBox() {
        return effectBox;
    }

    @Override
    public void checkPlayerIsOn(Player player) {
        checkPlayerCenterIsOn(player);
    }

    @Override
    public void call(Player player) {
        if (displayMessage) {
            textDisplay.update();
        }
    }

    @Override
    public void draw() {
        if (displaySign) {
            super.draw();
        }
        if (displayMessage) {
            drawRectWithOutline(textDisplay.getDisplayBox(), COLOR, StdDraw.BLACK, THICKNESS.DEFAULT);
            textDisplay.draw();
            displayMessage = false;
        }
    }

    @Override
    public void playerIsOn(Player player) {
        setDisplay();
    }

}

package mapobjects;

import game.Player;
import lib.StdDraw;

import java.awt.*;
import static helperobjects.DrawMethods.*;

public class Button extends MapObject {

    private boolean pressed = false;
    private Color unpressedColor = new Color(178, 23, 23);
    private static final Color PRESSED_COLOR = new Color(106, 192, 45), FRAME_COLOR = new Color(71, 21, 21);

    public Button(int xNum, int yNum) {
        this(xNum, yNum, false);
    }

    public Button(int xNum, int yNum, boolean isLittle) {
        super(xNum, yNum, (isLittle)?1:2, (isLittle)?1:2, !isLittle);
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setUnpressedColor(Color unpressedColor) {
        this.unpressedColor = unpressedColor;
    }

    @Override
    public void draw() {
        if (pressed) {
            StdDraw.setPenColor(PRESSED_COLOR);
        } else {
            StdDraw.setPenColor(unpressedColor);
        }
        drawRectangle(coordinates);
        StdDraw.setPenColor(FRAME_COLOR);
        drawRectangleOutline(coordinates);
    }

    @Override
    public void playerIsOn(Player player) {
        pressed = true;
    }

}

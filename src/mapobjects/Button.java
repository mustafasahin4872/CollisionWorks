package mapobjects;

import game.Player;
import lib.StdDraw;

import java.awt.*;
import static helperobjects.DrawMethods.*;

public abstract class Button extends MapObject {

    private boolean pressed;
    private Color unpressedColor = new Color(178, 23, 23); // Default color;
    private final Color pressedColor = new Color(106, 192, 45);
    private final Color frameColor = new Color(71, 21, 21);

    public Button(int worldIndex, int xNum, int yNum, double width, double height, boolean cornerAligned) {
        super(worldIndex, xNum, yNum, width, height, cornerAligned);
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setUnpressedColor(Color unpressedColor) {
        this.unpressedColor = unpressedColor;
    }

    public void press() {
        this.pressed = true;
    }

    @Override
    public void draw() {
        StdDraw.setPenColor(pressed ? pressedColor : unpressedColor);
        drawRectangle(coordinates);
        StdDraw.setPenColor(frameColor);
        drawRectangleOutline(coordinates);
    }

    @Override
    public void playerIsOn(Player player) {
        press();
    }

    public static class LittleButton extends Button {
        public LittleButton(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, 1, 1, false);
        }
    }

    public static class BigButton extends Button {
        public BigButton(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, 2, 2, true);
        }
    }
}

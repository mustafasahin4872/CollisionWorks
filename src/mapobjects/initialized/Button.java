package mapobjects.initialized;

import game.Player;
import lib.StdDraw;
import mapobjects.framework.Box;
import mapobjects.framework.OnEffector;
import mapobjects.framework.GridObject;

import java.awt.*;
import static helperobjects.DrawMethods.*;

public abstract class Button extends GridObject implements OnEffector {

    private final Box effectBox;
    private boolean pressed;
    private Color unpressedColor = new Color(178, 23, 23); // Default color;
    private Color color = unpressedColor;
    private static final Color PRESSED_COLOR = new Color(106, 192, 45),
            FRAME_COLOR = new Color(71, 21, 21);

    public Button(int worldIndex, int xNum, int yNum, double width, double height, boolean cornerAligned) {
        super(worldIndex, xNum, yNum, width, height, cornerAligned);
        effectBox = new Box(this);
    }

    public double[] getEffectBox() {
        return effectBox.getBox();
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setUnpressedColor(Color unpressedColor) {
        this.unpressedColor = unpressedColor;
    }

    public void press() {
        pressed = true;
        color = PRESSED_COLOR;
    }


    @Override
    public void call(Player player) {
        checkPlayerIsOn(player);
    }

    @Override
    public void playerIsOn(Player player) {
        press();
    }

    @Override
    public void draw() {
        StdDraw.setPenColor(color);
        drawRectangle(coordinates);
        StdDraw.setPenColor(FRAME_COLOR);
        drawRectangleOutline(coordinates);
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

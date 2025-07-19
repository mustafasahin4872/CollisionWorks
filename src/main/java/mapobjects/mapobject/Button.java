package mapobjects.mapobject;

import game.Player;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.category.OnEffector;
import mapobjects.category.GridObject;

import java.awt.*;
import static helperobjects.DrawMethods.*;

public abstract class Button extends GridObject implements OnEffector {

    private final Box effectBox;
    private boolean pressed;
    private Color unpressedColor = new Color(178, 23, 23); // Default color
    private Color color = unpressedColor;
    private static final Color PRESSED_COLOR = new Color(106, 192, 45),
            FRAME_COLOR = new Color(226, 125, 125);

    public Button(int worldIndex, int xNum, int yNum, double width, double height, boolean cornerAligned) {
        super(worldIndex, xNum, yNum, width, height, cornerAligned);
        effectBox = positionBox.clone();
    }

    @Override
    public Box getEffectBox() {
        return effectBox;
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
    public void checkPlayerIsOn(Player player) {
        checkPlayerCornerIsOn(player);
    }

    @Override
    public void playerIsOn(Player player) {
        press();
    }

    @Override
    public void draw() {
        StdDraw.setPenColor(color);
        drawRectangle(positionBox);
        StdDraw.setPenColor(FRAME_COLOR);
        drawRectangleOutline(positionBox);
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

package mapobjects;

import java.awt.*;
import java.util.Arrays;

import helperobjects.Alignment;
import lib.StdDraw;
import game.Player;
import game.Frame;
import static helperobjects.DrawMethods.drawRectangle;

public class Door extends MapObject {

    private final Alignment alignment;
    private final Button[] buttons;
    private boolean isOpen;
    private final double doorFloor;
    private final Color color = new Color((int) (Math.random() * 255), 0, (int) (Math.random() * 255));
    private static final double THICKNESS = 0.8, // in tiles
            SPEED = 2, DELTA = SPEED * Frame.DT; // in pixels


    public Door(int worldIndex, int xNum, int yNum, Alignment alignment) {
        this(worldIndex, xNum, yNum, alignment, 4, null);
    }

    public Door(int worldIndex, int xNum, int yNum, Alignment alignment, int length) {
        this(worldIndex, xNum, yNum, alignment, length, null);
    }

    public Door(int worldIndex, int xNum, int yNum, Alignment alignment, Button[] buttons) {
        this(worldIndex, xNum, yNum, alignment, 4, buttons);
    }

    public Door(int worldIndex, int xNum, int yNum, Alignment alignment, int length, Button[] buttons) {
        super(worldIndex, xNum, yNum, getWidth(alignment, length), getHeight(alignment, length), true);
        this.alignment = alignment;
        this.buttons = buttons;
        this.doorFloor = (alignment == Alignment.V) ? coordinates[1] : coordinates[0];

        if (buttons != null) {
            Arrays.stream(buttons).forEach(button -> button.setUnpressedColor(color));
        }
    }

    private static double getWidth(Alignment alignment, int length) {
        return (alignment == Alignment.H) ? length : THICKNESS;
    }

    private static double getHeight(Alignment alignment, int length) {
        return (alignment == Alignment.V) ? length : THICKNESS;
    }

    public void checkOpen() {
        if (buttons != null) {
            isOpen = Arrays.stream(buttons).allMatch(Button::isPressed);
        }
    }

    private void slideDoor() {
        if (!isOpen) return;
        adjustCoordinate();

    }

    private void adjustCoordinate() {
        int index = (alignment == Alignment.V) ? 3 : 2;
        coordinates[index] = Math.max(coordinates[index] - DELTA, doorFloor);
        collisionBox[index] = Math.max(collisionBox[index] - DELTA, doorFloor);
    }

    @Override
    public void draw() {
        slideDoor();
        StdDraw.setPenColor(color);
        drawRectangle(coordinates);
    }

    @Override
    public void playerIsOn(Player player) {} // not applicable
}

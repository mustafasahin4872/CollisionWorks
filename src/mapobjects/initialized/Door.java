package mapobjects.initialized;

import java.awt.*;
import java.util.Arrays;

import lib.StdDraw;
import game.Player;
import game.Frame;
import mapobjects.framework.Box;
import mapobjects.framework.Collidable;
import mapobjects.framework.GridObject;

import static helperobjects.DrawMethods.drawRectangle;

public class Door extends GridObject implements Collidable {

    private final Box collisionBox;
    private final char alignment;
    private Button[] buttons;
    private boolean isOpen;
    private final double doorFloor;
    private final Color color = new Color((int) (Math.random() * 255), 0, (int) (Math.random() * 255));
    private static final double
            THICKNESS = 0.8, SPACE_ON_SIDE = (1-THICKNESS)/2, SPEED = 2,  // in tiles
            DELTA = SPEED * Frame.DT; // in pixels


    public Door(int worldIndex, int xNum, int yNum, char alignment) {
        this(worldIndex, xNum, yNum, alignment, 4);
    }

    public Door(int worldIndex, int xNum, int yNum, char alignment, int length) {
        super(worldIndex, xNum, yNum, getWidth(alignment, length), getHeight(alignment, length), true);
        this.alignment = alignment;
        if (alignment == VERTICAL) {
            doorFloor = positionBox.getCorner(1);
            xShiftPosition(SPACE_ON_SIDE*TILE_SIDE);
        } else {
            doorFloor = positionBox.getCorner(0);
            yShiftPosition(SPACE_ON_SIDE*TILE_SIDE);
        }
        collisionBox = positionBox.clone();

    }

    private static double getWidth(char alignment, int length) {
        return (alignment == HORIZONTAL) ? length : THICKNESS;
    }

    private static double getHeight(char alignment, int length) {
        return (alignment == VERTICAL) ? length : THICKNESS;
    }

    @Override
    public void call(Player player) {
        checkOpen();
        slideDoor();
        if (fullyOpened()) {expire();}
    }

    public void setButtons(Button[] buttons) {
        this.buttons = buttons;
        if (buttons != null) {
            Arrays.stream(buttons).forEach(button -> button.setUnpressedColor(color));
        }
    }

    public void checkOpen() {
        if (buttons != null) {
            isOpen = Arrays.stream(buttons).allMatch(Button::isPressed);
        }
    }

    private boolean fullyOpened() {
        int index = (alignment == VERTICAL) ? 3 : 2;
        return (positionBox.getCorner(index) <= doorFloor);
    }

    private void slideDoor() {
        if (!isOpen) return;
        adjustCoordinate();
    }

    private void adjustCoordinate() {
        int index = (alignment == VERTICAL) ? 3 : 2;
        positionBox.setCorner(index, Math.max(positionBox.getCorner(index) - DELTA, doorFloor));
        collisionBox.setCorner(index, Math.max(collisionBox.getCorner(index) - DELTA, doorFloor));
    }

    @Override
    public Box getCollisionBox() {
        return collisionBox;
    }

    @Override
    public void draw() {
        StdDraw.setPenColor(color);
        drawRectangle(positionBox.getCorners());
    }

}

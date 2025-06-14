package mapobjects.initialized;

import java.awt.*;
import java.util.Arrays;

import lib.StdDraw;
import game.Player;
import game.Frame;
import mapobjects.framework.Box;
import mapobjects.framework.Collidable;
import mapobjects.framework.MapObject;

import static helperobjects.DrawMethods.drawRectangle;

public class Door extends MapObject implements Collidable {

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
            doorFloor = coordinates[1];
            xShiftPosition(SPACE_ON_SIDE*TILE_SIDE);
        } else {
            doorFloor = coordinates[0];
            yShiftPosition(SPACE_ON_SIDE*TILE_SIDE);
        }
        collisionBox = new Box(this);

    }

    private static double getWidth(char alignment, int length) {
        return (alignment == HORIZONTAL) ? length : THICKNESS;
    }

    private static double getHeight(char alignment, int length) {
        return (alignment == VERTICAL) ? length : THICKNESS;
    }

    @Override
    public void call(Player player) {
        checkCollision(player);
        checkOpen();
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

    private void slideDoor() {
        if (!isOpen) return;
        adjustCoordinate();

    }

    private void adjustCoordinate() {
        int index = (alignment == VERTICAL) ? 3 : 2;
        coordinates[index] = Math.max(coordinates[index] - DELTA, doorFloor);
        collisionBox.setBoxIndex(index, Math.max(collisionBox.getBoxIndex(index) - DELTA, doorFloor));
    }

    @Override
    public double[] getCollisionBox() {
        return collisionBox.getBox();
    }

    @Override
    public void draw() {
        slideDoor();
        StdDraw.setPenColor(color);
        drawRectangle(coordinates);
    }

}

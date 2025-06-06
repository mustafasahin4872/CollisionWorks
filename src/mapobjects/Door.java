package mapobjects;

import java.awt.*;
import lib.StdDraw;
import game.Player;
import game.Frame;
import static helperobjects.DrawMethods.drawRectangle;

public abstract class Door extends MapObject {

    public enum ALIGNMENT {V, H} // vertical or horizontal alignment for doors
    private final ALIGNMENT alignment;
    private final Button[] buttons;

    private boolean isOpen;
    private final double doorFloor;

    private final Color color = new Color((int) (Math.random()*255), 0, (int) (Math.random()*255));
    private static final double THICKNESS = 40, SPACE_ON_SIDE = (TILE_SIDE-THICKNESS)/2,
            SPEED = 2, DELTA = SPEED * Frame.DT;


    public Door(ALIGNMENT alignment, int xNum, int yNum) {
        this(alignment, xNum, yNum, 4, null);
    }

    // door without buttons, it is just an obstacle
    public Door(ALIGNMENT alignment, int xNum, int yNum, int length) {
        this(alignment, xNum, yNum, length, null);
    }

    public Door(ALIGNMENT alignment, int xNum, int yNum, Button[] buttons) {
        this(alignment, xNum, yNum, 4, buttons);
    }

    // door with buttons wired to it, defined by its alignment and the tiles it lies on.
    public Door(ALIGNMENT alignment, int xNum, int yNum, int length, Button[] buttons) {
        super(xNum, yNum);
        this.alignment = alignment;
        //length value in tiles
        this.buttons = buttons;

        if (alignment == ALIGNMENT.V) {
            coordinates[0] += SPACE_ON_SIDE;
            coordinates[2] -= SPACE_ON_SIDE;
            coordinates[3] += (length-1)*TILE_SIDE;
            doorFloor = coordinates[1];
        } else {
            coordinates[1] += SPACE_ON_SIDE;
            coordinates[3] -= SPACE_ON_SIDE;
            coordinates[2] += (length-1)*TILE_SIDE;
            doorFloor = coordinates[0];
        }
        collisionBox = coordinates;

        if (buttons != null) {
            for (Button button : buttons) {
                button.setUnpressedColor(color);
            }
        }
    }

    public void checkOpen() {
        if (buttons == null) return;

        boolean pressed = true;
        for (Button button : buttons) {
            if (!button.isPressed()) {
                pressed = false;
                break;
            }
        }
        isOpen = pressed;
    }

    //decreases the door's y coordinates by doorSpeed*timeStep if door is set to be open and is not fully opened(vertical)
    private void slideDoor() {
        if (!isOpen) return;

        if (alignment == ALIGNMENT.V && coordinates[3] > doorFloor) {
            coordinates[3] = Math.max(coordinates[3] - DELTA, doorFloor); // Ensure top >= bottom
            collisionBox[3] = Math.max(collisionBox[3] - DELTA, doorFloor);
        } else if (alignment == ALIGNMENT.H && coordinates[2] > doorFloor) {
            coordinates[2] = Math.max(coordinates[2] - DELTA, doorFloor); // Ensure right >= left
            collisionBox[2] = Math.max(collisionBox[2] - DELTA, doorFloor);
        }
    }


    @Override
    public void draw() {
        slideDoor();
        StdDraw.setPenColor(color);
        drawRectangle(coordinates);
    }

    @Override
    public void playerIsOn(Player player) {} //not possible


    public static class VerticalDoor extends Door {
        public VerticalDoor(int xNum, int yNum, int length, Button[] buttons) {
            super(ALIGNMENT.V, xNum, yNum, length, buttons);
        }
        public VerticalDoor(int xNum, int yNum, int length) {
            super(ALIGNMENT.V, xNum, yNum, length);
        }
        public VerticalDoor(int xNum, int yNum) {
            super(ALIGNMENT.V, xNum, yNum);
        }
    }

    public static class HorizontalDoor extends Door {
        public HorizontalDoor(int xNum, int yNum, int length, Button[] buttons) {
            super(ALIGNMENT.H, xNum, yNum, length, buttons);
        }
        public HorizontalDoor(int xNum, int yNum, int length) {
            super(ALIGNMENT.H, xNum, yNum, length);
        }
        public HorizontalDoor(int xNum, int yNum) {
            super(ALIGNMENT.H, xNum, yNum);
        }
    }
}

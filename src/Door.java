import java.awt.*;

public class Door implements Drawable{

    public enum ALIGNMENT {V, H} // vertical or horizontal alignment for doors

    private final ALIGNMENT alignment;
    private boolean isOpen = false;
    //private boolean reverse = false; //if the door is opens in reverse

    private final int xNum;
    private final int yNum;
    private final int length; //length value in tiles
    private final double[] coordinates;
    private final double doorFloor;

    private static final double TILE_SIDE = Tile.HALF_SIDE * 2;
    private static final double THICKNESS = 40;
    private static final double SPACE_ON_SIDE = (TILE_SIDE-THICKNESS)/2;
    private static final double SPEED = 2; //sliding speed of door

    private final Color color = new Color((int) (Math.random()*255), 0, (int) (Math.random()*255));
    private final Button[] buttons;



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
        this.alignment = alignment;
        this.xNum = xNum;
        this.yNum = yNum;
        this.length = length;
        this.buttons = buttons;
        {
            double x0, y0, x1, y1;
            if (alignment == ALIGNMENT.V) {
                x0 = (xNum-1) * TILE_SIDE + SPACE_ON_SIDE;
                x1 = xNum * TILE_SIDE - SPACE_ON_SIDE;
                y0 = Map.height - (yNum+length-1) * TILE_SIDE;
                y1 = Map.height - (yNum-1) * TILE_SIDE;
                doorFloor = y0;
            } else {
                y0 = Map.height - yNum * TILE_SIDE + SPACE_ON_SIDE;
                y1 = Map.height - (yNum - 1) * TILE_SIDE - SPACE_ON_SIDE;
                x0 = (xNum - 1) * TILE_SIDE;
                x1 = (xNum + length -1) * TILE_SIDE;
                doorFloor = x0;
            }
            coordinates = new double[]{x0, y0, x1, y1};
        }
        if (buttons != null) {
            for (Button button : buttons) {
                button.setUnpressedColor(color);
            }
        }
    }



    public double[] getCoordinates() {
        return coordinates;
    }

    public void checkOpen(Player player) {
        if (buttons == null) return;

        boolean pressed = true;
        for (Button button : buttons) {
            if (!button.isPressed()) {
                pressed = false;
            }
        }
        isOpen = pressed;
    }

    //decreases the door's y coordinates by doorSpeed*timeStep if door is set to be open and is not fully opened(vertical)
    private void slideDoor() {
        if (isOpen && alignment == ALIGNMENT.V && coordinates[3] > doorFloor) {
            coordinates[3]-= SPEED * Frame.DT;
        } else if (isOpen && alignment == ALIGNMENT.H && coordinates[2] > doorFloor) {
            coordinates[2]-= SPEED * Frame.DT;
        }
    }


    @Override
    public void draw() {
        slideDoor();
        StdDraw.setPenColor(color);
        Map.drawRectangle(coordinates);
    }



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

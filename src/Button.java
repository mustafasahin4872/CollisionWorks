import java.awt.*;

public class Button implements Passable, Drawable {

    private final int xNum;
    private final int yNum;
    private final double[] coordinates = new double[4];
    private boolean pressed = false;
    private Color unpressedColor = new Color(178, 23, 23);
    private static final Color PRESSED_COLOR = new Color(106, 192, 45);
    private static final Color FRAME_COLOR = new Color(71, 21, 21);
    private static final double FRAME_THICKNESS = 0.02;
    private static final double tileSide = Tile.HALF_SIDE*2;

    public Button(int xNum, int yNum) {
        this(xNum, yNum, false);
    }

    public Button(int xNum, int yNum, boolean isLittle) {
        this.xNum = xNum;
        this.yNum = yNum;
        if (isLittle) {
            setLittleCoordinates();
        } else {
            setCoordinates();
        }
    }

    @Override
    public double[] getCoordinates() {
        return coordinates;
    }

    public void setLittleCoordinates() {
        coordinates[0] = (xNum - 1) * tileSide;
        coordinates[2] = (xNum) * tileSide;
        coordinates[1] = Map.height - yNum * tileSide;
        coordinates[3] = Map.height - (yNum - 1) * tileSide;
    }

    public void setCoordinates() {
        coordinates[0] = (xNum - 1) * tileSide;
        coordinates[2] = (xNum + 1) * tileSide;
        coordinates[1] = Map.height - (yNum + 1) * tileSide;
        coordinates[3] = Map.height - (yNum - 1) * tileSide;
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
        Map.drawRectangle(coordinates);
        StdDraw.setPenColor(FRAME_COLOR);
        StdDraw.setPenRadius(FRAME_THICKNESS);
        Map.drawRectangleOutline(coordinates);
    }

    @Override
    public void playerIsOn(Player player) {
        pressed = true;
    }

}

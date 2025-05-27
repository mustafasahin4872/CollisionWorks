import java.awt.*;
import java.awt.event.KeyEvent;

public class Selection {

    private int worldIndex = 1;
    private int currentLevelIndex = 1;

    public static final Color[] WORLD_COLORS = {
            new Color(65, 192, 45),
            new Color(100, 100, 250),
            new Color(250, 150, 100)
    };
    public static final Color[] LEVEL_COLORS = {
            new Color(199, 107, 199, 255),
            new Color(100, 200, 250),
            new Color(150, 0, 0)
    };

    private static final String[] WORLD_NAMES = {
            "THE SPRING FESTIVAL", "INTO THE ICE CAVE", "TO THE TOP OF THE VOLCANO"
    };

    private final int buttonHalfSide = 15;
    private final double[] leftArrowBox = {30, Frame.Y_SCALE-60-buttonHalfSide*2, 30 + buttonHalfSide*2, Frame.Y_SCALE-60};
    private final double[] rightArrowBox = {Frame.X_SCALE-30-buttonHalfSide*2, Frame.Y_SCALE-60-buttonHalfSide*2, Frame.X_SCALE-30, Frame.Y_SCALE-60};

    //these two are in format {x, y}, the centers of buttons
    private final double[] leftArrowButton = {30 + buttonHalfSide, Frame.Y_SCALE-60- buttonHalfSide};
    private final double[] rightArrowButton = {Frame.X_SCALE-30- buttonHalfSide, Frame.Y_SCALE-60- buttonHalfSide};

    private final double[][] levelButtons;
    {
        double spaceOnSides = 150;
        double boxHalfLength = 40;
        double spaceInBetween = 50;
        double step = 2 * boxHalfLength + spaceInBetween;
        double spaceUp = Frame.Y_SCALE - 150;

        levelButtons = new double[][]{
                // Row 1 (Top row)
                {spaceOnSides + boxHalfLength, spaceUp - boxHalfLength},
                {spaceOnSides + step + boxHalfLength, spaceUp - boxHalfLength},
                {spaceOnSides + 2 * step + boxHalfLength, spaceUp - boxHalfLength},
                {spaceOnSides + 3 * step + boxHalfLength, spaceUp - boxHalfLength},
                {spaceOnSides + 4 * step + boxHalfLength, spaceUp - boxHalfLength},

                // Row 2 (Middle row)
                {spaceOnSides + boxHalfLength, spaceUp - step - boxHalfLength},
                {spaceOnSides + step + boxHalfLength, spaceUp - step - boxHalfLength},
                {spaceOnSides + 2 * step + boxHalfLength, spaceUp - step - boxHalfLength},
                {spaceOnSides + 3 * step + boxHalfLength, spaceUp - step - boxHalfLength},
                {spaceOnSides + 4 * step + boxHalfLength, spaceUp - step - boxHalfLength},

                // Row 3 (Bottom row)
                {spaceOnSides + boxHalfLength, spaceUp - 2 * step - boxHalfLength},
                {spaceOnSides + step + boxHalfLength, spaceUp - 2 * step - boxHalfLength},
                {spaceOnSides + 2 * step + boxHalfLength, spaceUp - 2 * step - boxHalfLength},
                {spaceOnSides + 3 * step + boxHalfLength, spaceUp - 2 * step - boxHalfLength},
                {spaceOnSides + 4 * step + boxHalfLength, spaceUp - 2 * step - boxHalfLength}
        };
    }

    public int[] chooseLevel() {
        StdDraw.setXscale(0, Frame.X_SCALE);
        StdDraw.setYscale(0, Frame.Y_SCALE);
        while (!StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
            handleInput();
            draw();
            StdDraw.show();
            StdDraw.pause(20*Frame.PAUSE);
        }
        return new int[]{worldIndex, currentLevelIndex};
    }

    private void handleInput() {

        if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) && currentLevelIndex+1<=levelButtons.length) {
            currentLevelIndex++;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) && currentLevelIndex-1>=1) {
            currentLevelIndex--;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN) && currentLevelIndex + 5 <= levelButtons.length) {
            currentLevelIndex+=5;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_UP) && currentLevelIndex - 5 >= 1) {
            currentLevelIndex-=5;
        }

        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();

        if (StdDraw.isMousePressed()) {
            if (Map.isIn(mouseX, mouseY, leftArrowBox)) {
                if (worldIndex-1>=1) {
                    worldIndex--;
                    currentLevelIndex=1;
                }
            }
            if (Map.isIn(mouseX, mouseY, rightArrowBox)) {
                if (worldIndex+1<=3) {
                    worldIndex++;
                    currentLevelIndex=1;
                }
            }
            for (int i = 0; i<levelButtons.length; i++) {
                if (levelButtons[i][0]-buttonHalfSide < mouseX &&
                        levelButtons[i][0] + buttonHalfSide > mouseX &&
                        levelButtons[i][1] - buttonHalfSide < mouseY &&
                        levelButtons[i][1] + buttonHalfSide > mouseY) {
                    currentLevelIndex = i+1;
                }
            }
        }

    }

    private void draw() {

        StdDraw.clear(WORLD_COLORS[worldIndex-1]); //background color

        //world name
        StdDraw.setFont(new Font("Arial", Font.BOLD, 26));
        StdDraw.text(Frame.X_SCALE/2.0, Frame.Y_SCALE-60- buttonHalfSide, WORLD_NAMES[worldIndex-1]);

        //right and left arrow buttons
        StdDraw.setPenRadius();
        StdDraw.square(leftArrowButton[0], leftArrowButton[1], buttonHalfSide);
        StdDraw.square(rightArrowButton[0], rightArrowButton[1], buttonHalfSide);

        StdDraw.setFont();
        StdDraw.text(leftArrowButton[0], leftArrowButton[1], "<");
        StdDraw.text(rightArrowButton[0], rightArrowButton[1], ">");

        //level buttons
        drawLevelButtons();
    }

    private void drawLevelButtons() {
        StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
        StdDraw.setPenRadius(0.01);
        for (int i = 0; i<levelButtons.length; i++) {
            double[] currentButton = levelButtons[i];
            StdDraw.setPenColor(LEVEL_COLORS[worldIndex-1]);
            StdDraw.filledSquare(currentButton[0], currentButton[1], buttonHalfSide*2);
            StdDraw.square(currentButton[0], currentButton[1], buttonHalfSide*2);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(currentButton[0], currentButton[1], ""+(i+1));
            if (i+1 == currentLevelIndex) {
                StdDraw.square(currentButton[0], currentButton[1], buttonHalfSide*2);
            }
        }
    }
}

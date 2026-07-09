package helpers;

import game.Frame;
import lib.StdDraw;
import mapobjects.component.Box;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static helpers.DrawMethods.*;

/// displays lines of texts, either left or center aligned to the box
/// two ways to use:
/// 1) provide array of strings, each string for one line
/// 2) provide a single line of text (dynamically creates new lines)
public class TextDisplay {

    private boolean leftAligned = false;

    private final double lineHeight;

    private final FrameBox box;
    private final String[] lines;

    private final Color color;
    private final Font font;

    public TextDisplay(Box box, String[] lines, Color color, Font font) {
        double xGap = Math.min(10, box.getWidth() / 10);
        double yGap = Math.min(5, box.getHeight() / 10);
        this.box = new FrameBox(box.getCenterX()+xGap, box.getCenterY()+yGap, box.getWidth()+2*xGap, box.getHeight()+2*yGap);
        this.box.update();
        this.color = color;
        this.font = font;
        this.lineHeight = getFontHeight(font);

        ArrayList<String> aligned = new ArrayList<>();
        for (String string : lines) {
            aligned.addAll(Arrays.asList(splitTextToFit(string, box.getWidth(), font)));
        }
        this.lines = aligned.toArray(new String[0]);
    }

    public TextDisplay(Box box, String text, Color color, Font font) {
        this(box, new String[]{text}, color, font);
    }

    public TextDisplay(Box box, String[] lines, Color color, Font font, boolean leftAligned) {
        this(box, lines, color, font);
        this.leftAligned = leftAligned;
    }

    public TextDisplay(Box box, String text, Color color, Font font, boolean leftAligned) {
        this(box, new String[]{text}, color, font);
        this.leftAligned = leftAligned;
    }

    public TextDisplay(Box box, String[] lines, Font font, boolean leftAligned) {
        this(box, lines, Color.BLACK, font, leftAligned);
    }

    public TextDisplay(Box box, String text, Font font, boolean leftAligned) {
        this(box, text, Color.BLACK, font, leftAligned);
    }

    public void update() {
        box.update();
    }

    public Box getDisplayBox() {
        return box.getFrameBox();
    }

    public void draw() {
        Box frameBox = box.getFrameBox();
        for (int i = 0; i<lines.length; i++) {
            if (lines[i] == null) return;
            Box subBox = new Box(frameBox.getCenterX(), frameBox.getCenterY() - frameBox.getHeight()/2 + i * lineHeight + lineHeight/2, frameBox.getWidth(), lineHeight);
            if (leftAligned) {
                leftAlignTextInsideBox(subBox, lines[i], color, font);
            } else {
                textInsideBox(subBox, lines[i], color, font);
            }
        }
    }

}

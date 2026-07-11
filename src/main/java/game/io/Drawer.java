package game.io;

import game.core.Main;
import lib.StdDraw;
import mapobjects.components.Box;

import java.awt.*;

import static game.core.Main.IMAGES_ROOT;
import static helpers.TextMethods.*;

public abstract class Drawer {

    protected final Box box;

    public Drawer(Box box) {
        this.box = box;
    }

    public double getX() {
        return box.getCenterX();
    }

    public double getY() {
        return box.getCenterY();
    }

    public void setX(double x) {
        box.setCenterX(x);
    }

    public void setY(double y) {
        box.setCenterY(y);
    }

    public double getWidth() {
        return box.getWidth();
    }

    public double getHeight() {
        return box.getHeight();
    }

    public void setWidth(double width) {
        box.setWidth(width);
    }

    public void setHeight(double height) {
        box.setHeight(height);
    }

    public void resize(double multiplier) {
        setWidth(getWidth()*multiplier);
        setHeight(getHeight()*multiplier);
    }

    public String getName() {
        return "";
    }

    public abstract void draw();

    /// All animated draws use the same current time and start time, so they are synced.
    /// Draws shrinking - enlarging animations using current time.
    public void drawAnimated() {
        double maxDiff = 0.1; // between 0 and 1 always!
        double period = 3000; // in milliseconds

        double ratio = ((System.currentTimeMillis() - Main.GAME_START) % period) / period;
        double multiplier;
        if (ratio > 0.5) {
            multiplier = 1 + maxDiff - 2 * maxDiff * ((ratio - 0.5) / 0.5);
        } else {
            multiplier = 1 - maxDiff + 2 * maxDiff * (ratio / 0.5);
        }
        // round to the second decimal, otherwise StdDraw goes insane (the image vibrated)
        multiplier = Math.round(multiplier * 100) / 100.0;

        double width = getWidth();
        double height = getHeight();
        resize(multiplier);
        draw();
        setWidth(width);
        setHeight(height);
    }

    public enum THICKNESS {
        THIN(0.01),
        DEFAULT(0.015),
        ;

        THICKNESS(double thickness) {
            this.thickness = thickness;
        }

        public final double thickness;

    }

    public static class TextDrawer extends Drawer {

        private String text;
        private Color textColor;
        private final Font font;
        private final boolean leftAligned;

        public TextDrawer(Box box, String text, Color textColor, Font font, boolean leftAligned) {
            super(box);
            this.text = text;
            this.textColor = textColor;
            this.font = font;
            this.leftAligned = leftAligned;
            if (leftAligned) leftAlign();
        }

        public TextDrawer(Box box, String text, Color textColor, Font font) {
            this(box, text, textColor, font, false);
        }

        public TextDrawer(Box box, String text, Font font) {
            this(box, text, Color.BLACK, font, false);
        }

        public TextDrawer(Box box, String text) {
            this(box, text, Color.BLACK, new Font("Arial", Font.PLAIN, 20), false);
        }

        public TextDrawer(Box box, Color textColor, Font font, boolean leftAligned) {
            this(box, "", textColor, font, leftAligned);
        }

        public TextDrawer(Box box, Color textColor, Font font) {
            this(box, "", textColor, font, false);
        }

        public TextDrawer(Box box, Font font) {
            this(box, "", Color.BLACK, font, false);
        }

        public TextDrawer(Box box) {
            this(box, "", Color.BLACK, new Font("Arial", Font.PLAIN, 20), false);
        }

        public void setText(String text) {
            this.text = text;
            if (leftAligned) leftAlign();
        }

        public void setTextColor(Color textColor) {
            this.textColor = textColor;
        }

        private void leftAlign() {
            double textWidth = getTextWidth(text, font);
            double newX = box.getCenterX() - box.getWidth()/2 + textWidth/2;
            setX(newX);
            setWidth(textWidth);
        }

        @Override
        public void draw() {
            StdDraw.setPenColor(textColor);
            StdDraw.setFont(font);
            double yOffset = 0.05 * box.getHeight();
            StdDraw.text(box.getCenterX(), box.getCenterY()+yOffset, text);
        }
    }

    public static class BoxDrawer extends Drawer {

        private Color boxColor;

        public BoxDrawer(Box box, Color boxColor) {
            super(box);
            this.boxColor = boxColor;
        }

        public void setBoxColor(Color boxColor) {
            this.boxColor = boxColor;
        }

        @Override
        public void draw() {
            StdDraw.setPenColor(boxColor);
            double[] region = box.getCorners();
            StdDraw.filledRectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
        }

    }

    public static class OutlineDrawer extends Drawer {

        private Color outlineColor;
        private final THICKNESS thickness;

        public OutlineDrawer(Box box, Color outlineColor, THICKNESS thickness) {
            super(box);
            this.outlineColor = outlineColor;
            this.thickness = thickness;
        }

        public OutlineDrawer(Box box, Color outlineColor) {
            this(box, outlineColor, THICKNESS.DEFAULT);
        }

        public OutlineDrawer(Box box) {
            this(box, Color.BLACK);
        }

        public void setOutlineColor(Color outlineColor) {
            this.outlineColor = outlineColor;
        }

        @Override
        public void draw() {
            StdDraw.setPenColor(outlineColor);
            StdDraw.setPenRadius(thickness.thickness);
            double[] region = box.getCorners();
            StdDraw.rectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
        }

    }

    public static class OutlinedBoxDrawer extends Drawer {

        private final BoxDrawer boxDrawer;
        private final OutlineDrawer outlineDrawer;

        public OutlinedBoxDrawer(Box box, Color boxColor, Color outlineColor, THICKNESS thickness) {
            super(box);
            this.boxDrawer = new BoxDrawer(box, boxColor);
            this.outlineDrawer = new OutlineDrawer(box, outlineColor, thickness);
        }

        public OutlinedBoxDrawer(Box box, Color boxColor, Color outlineColor) {
            super(box);
            this.boxDrawer = new BoxDrawer(box, boxColor);
            this.outlineDrawer = new OutlineDrawer(box, outlineColor);
        }

        public OutlinedBoxDrawer(Box box, Color boxColor) {
            this(box, boxColor, Color.BLACK);
        }

        public void setBoxColor(Color boxColor) {
            boxDrawer.setBoxColor(boxColor);
        }

        @Override
        public void draw() {
            boxDrawer.draw();
            outlineDrawer.draw();
        }

    }

    public static class ClassicButtonDrawer extends Drawer {

        private final BoxDrawer boxDrawer;
        private final OutlineDrawer outlineDrawer;
        private final TextDrawer textDrawer;

        public ClassicButtonDrawer(Box box, Color boxColor, Color outlineColor, THICKNESS thickness, String text, Color textColor, Font font) {
            super(box);
            this.boxDrawer = new BoxDrawer(box, boxColor);
            this.outlineDrawer = new OutlineDrawer(box, outlineColor, thickness);
            this.textDrawer = new TextDrawer(box, text, textColor, font);
        }

        public ClassicButtonDrawer(Box box, Color boxColor, Color outlineColor, String text, Color textColor, Font font) {
            this(box, boxColor, outlineColor, THICKNESS.DEFAULT, text, textColor, font);
        }

        public ClassicButtonDrawer(Box box, Color boxColor, String text, Color textColor, Font font) {
            this(box, boxColor, Color.BLACK, THICKNESS.DEFAULT, text, textColor, font);
        }

        public ClassicButtonDrawer(Box box, Color boxColor, String text) {
            super(box);
            this.boxDrawer = new BoxDrawer(box, boxColor);
            this.outlineDrawer = new OutlineDrawer(box);
            this.textDrawer = new TextDrawer(box, text);
        }

        public ClassicButtonDrawer(Box box, Color boxColor, Color outlineColor, THICKNESS thickness, Color textColor, Font font) {
            this(box, boxColor, outlineColor, thickness, "", textColor, font);
        }

        public ClassicButtonDrawer(Box box, Color boxColor, Color outlineColor, Color textColor, Font font) {
            this(box, boxColor, outlineColor, THICKNESS.DEFAULT, "", textColor, font);
        }

        public ClassicButtonDrawer(Box box, Color boxColor, Color textColor, Font font) {
            this(box, boxColor, Color.BLACK, THICKNESS.DEFAULT, "", textColor, font);
        }

        public ClassicButtonDrawer(Box box, Color boxColor) {
            this(box, boxColor, "");
        }

        public void setBoxColor(Color boxColor) {
            boxDrawer.setBoxColor(boxColor);
        }

        public void setText(String text) {
            textDrawer.setText(text);
        }


        @Override
        public void draw() {
            boxDrawer.draw();
            textDrawer.draw();
            outlineDrawer.draw();
        }

    }

    public static class PictureDrawer extends Drawer {

        public enum FILE_TYPE {png, jpg}

        private final String directory;
        private final FILE_TYPE fileType;
        private String name;
        private String fileName;
        private double degrees = 0;

        public PictureDrawer(Box box, String directory, String name, FILE_TYPE fileType) {
            super(box);
            this.directory = directory;
            this.fileType = fileType;
            setName(name);
        }

        public PictureDrawer(Box box, String directory, String name) {
            this(box, directory, name, FILE_TYPE.png);
        }

        public PictureDrawer(Box box, String directory, FILE_TYPE fileType) {
            this(box, directory, "0", fileType);
        }

        public PictureDrawer(Box box, String directory) {
            this(box, directory, "0", FILE_TYPE.png);
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
            fileName = IMAGES_ROOT + directory + name + "." + fileType.name();
        }

        public void setDegrees(double degrees) {
            this.degrees = degrees;
        }

        @Override
        public void draw() {
            StdDraw.picture(box.getCenterX(), box.getCenterY(), fileName, box.getWidth(), box.getHeight(), degrees);
        }

    }

    public static class CircleDrawer extends Drawer {

        private double radius;
        private final Color circleColor;
        private final THICKNESS thickness;

        public CircleDrawer(Box box, double radius, Color circleColor, THICKNESS thickness) {
            super(box);
            this.radius = radius;
            this.circleColor = circleColor;
            this.thickness = thickness;
        }

        public CircleDrawer(Box box, double radius, Color circleColor) {
            this(box, radius, circleColor, THICKNESS.THIN);
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        @Override
        public void draw() {
            StdDraw.setPenColor(circleColor);
            StdDraw.setPenRadius(thickness.thickness);
            StdDraw.circle(box.getCenterX(), box.getCenterY(), radius);
        }

    }

    public static class FilledCircleDrawer extends Drawer{

        private double radius;
        private final Color circleColor;

        public FilledCircleDrawer(Box box, double radius, Color circleColor) {
            super(box);
            this.radius = radius;
            this.circleColor = circleColor;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        @Override
        public void draw() {
            StdDraw.setPenColor(circleColor);
            StdDraw.filledCircle(box.getCenterX(), box.getCenterY(), radius);
        }

    }

}

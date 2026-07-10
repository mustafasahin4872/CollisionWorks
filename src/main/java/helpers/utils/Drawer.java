package helpers.utils;

import lib.StdDraw;
import mapobjects.components.Box;

import java.awt.*;

import static helpers.methods.TextMethods.*;

public abstract class Drawer {

    protected final Box box;

    public Drawer(Box box) {
        this.box = box;
    }

    public void updatePosition(double x, double y) {
        box.setCenterCoordinates(x, y);
    }

    public void setWidth(double width) {
        box.setWidth(width);
    }

    public void setHeight(double height) {
        box.setHeight(height);
    }

    public abstract void draw1();

    public enum THICKNESS {
        THIN(0.01),
        DEFAULT(0.015),
        THICK(0.025)
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
            updatePosition(newX, box.getCenterY());
            setWidth(textWidth);
        }

        @Override
        public void draw1() {
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
        public void draw1() {
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
        public void draw1() {
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
        public void draw1() {
            boxDrawer.draw1();
            outlineDrawer.draw1();
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
        public void draw1() {
            boxDrawer.draw1();
            textDrawer.draw1();
            outlineDrawer.draw1();
        }

    }

    public static class PictureDrawer extends Drawer {

        private String fileName;

        public PictureDrawer(Box box, String fileName) {
            super(box);
            this.fileName = fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void draw1() {
            StdDraw.picture(box.getCenterX(), box.getCenterY(), fileName, box.getWidth(), box.getHeight());
        }

    }

}

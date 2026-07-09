package helpers;

import lib.StdDraw;
import mapobjects.component.Box;

import java.awt.*;
import java.util.ArrayList;

//static methods for drawing
public class DrawMethods {

    public enum THICKNESS {
        THIN(0.01),
        DEFAULT(0.015),
        THICK(0.025)
        ;

        THICKNESS(double thickness) {
            this.thickness = thickness;
        }

        private final double thickness;

    }

    public static void drawRectangle(Box box) {
        double[] region = box.getCorners();
        StdDraw.filledRectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
    }

    public static void drawRectangleOutline(Box box, Color color, THICKNESS thickness) {
        StdDraw.setPenColor(color);
        StdDraw.setPenRadius(thickness.thickness);
        double[] region = box.getCorners();
        StdDraw.rectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
            (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
    }

    public static void drawRectWithOutline(Box box, Color boxColor, Color outColor, THICKNESS thickness) {
        StdDraw.setPenColor(outColor);
        drawRectangleOutline(box, outColor, thickness);
        StdDraw.setPenColor(boxColor);
        drawRectangle(box);
    }

    public static void drawRectWithOutline(Box box, Color boxColor, Color outColor) {
        drawRectWithOutline(box, boxColor, outColor, THICKNESS.DEFAULT);
    }

    public static void textInsideBox(Box box, String text) {
        Font font = new Font("Arial", Font.PLAIN, 16);
        Color color = StdDraw.BLACK;
        textInsideBox(box, text, color, font);
    }

    public static void textInsideBox(Box box, String text, Color color, Font font) {
        StdDraw.setPenColor(color);
        StdDraw.setFont(font);
        double yOffset = 0.05 * box.getHeight();
        StdDraw.text(box.getCenterX(), box.getCenterY()+yOffset, text);
    }

    public static void leftAlignTextInsideBox(Box box, String text, Color color, Font font)  {
        double textWidth = getTextWidth(text, font);
        double newX = box.getCenterX() - box.getWidth()/2 + textWidth/2;
        Box alignBox = new Box(newX, box.getCenterY(), textWidth, box.getHeight());
        textInsideBox(alignBox, text, color, font);
    }

    public static void drawText(String text, double x, double y, Font font, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.setFont(font);
        StdDraw.text(x, y, text);
    }

    public static double getFontHeight(Font font) {
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        return metrics.getHeight();
    }

    public static double getTextWidth(String text, Font font) {
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    public static int getFontSizeForHeight(int desiredHeight, Font font) {
        int size = font.getSize();
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        double height = metrics.getHeight();

        size = (int) (size * desiredHeight / height);
        return size;
    }

    public static String[] splitTextToFit(String text, double desiredWidth, Font font) {
           String[] words = text.split(" ");
           int wordCount = words.length;
           double[] widths = new double[wordCount];

           for (int i = 0; i<wordCount; i++) {
               widths[i] = getTextWidth(words[i], font);
           }

           ArrayList<String> aligned = new ArrayList<>();
           double totalWidthSoFar = 0;
           final double space = getTextWidth(" ", font);
           int i = 0;
           while (i<wordCount) {
               StringBuilder sb = new StringBuilder();
               while (i<wordCount && totalWidthSoFar + widths[i] <= desiredWidth) {
                   sb.append(words[i]);
                   sb.append(" ");
                   totalWidthSoFar += widths[i] + space;
                   i++;
               }
               if (sb.isEmpty()) {
                   sb.append(words[i]);
                   i++;
               }
               aligned.add(sb.toString());
               totalWidthSoFar = 0;
           }
           return aligned.toArray(new String[0]);
    }

}

package helpers;

import lib.StdDraw;
import mapobjects.component.Box;

import java.awt.*;

//static methods for drawing
public class DrawMethods {

    private static final double THICKNESS = 0.015;

    public static void drawRectangle(Box box) {
        double[] region = box.getCorners();
        StdDraw.filledRectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
    }

    public static void drawRectangle(double[] region) {
        StdDraw.filledRectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
    }

    public static void drawRectangleOutline(Box box) {
        StdDraw.setPenRadius(THICKNESS);
        double[] region = box.getCorners();
        StdDraw.rectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
    }

    public static void drawRectangleOutline(double[] region) {
        StdDraw.setPenRadius(THICKNESS);
        StdDraw.rectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
    }

    public static void drawRectWithOutline(double[] region, Color boxColor, Color outColor) {
        StdDraw.setPenColor(outColor);
        drawRectangleOutline(region);
        StdDraw.setPenColor(boxColor);
        drawRectangle(region);
    }

    public static void drawRectWithOutline(Box region, Color boxColor, Color outColor) {
        StdDraw.setPenColor(outColor);
        drawRectangleOutline(region);
        StdDraw.setPenColor(boxColor);
        drawRectangle(region);
    }

    public static void textInsideBox(Box box, String text, Color color, int fontSize) {
        StdDraw.setPenColor(color);
        Font font = new Font("Arial", Font.PLAIN, fontSize);
        StdDraw.setFont(font);
        double yOffset = fontSize * 0.15;
        StdDraw.text(box.getCenterX(), box.getCenterY()+yOffset, text);
    }

}

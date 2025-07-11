package helperobjects;

import lib.StdDraw;
import mapobjects.framework.Box;

//static methods for drawing
public class DrawMethods {

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
        drawRectangleOutline(box, 0.015);
    }

    public static void drawRectangleOutline(double[] region) {
        drawRectangleOutline(region, 0.015);
    }

    public static void drawRectangleOutline(Box box, double thickness) {
        StdDraw.setPenRadius(thickness);
        double[] region = box.getCorners();
        StdDraw.rectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
    }

    public static void drawRectangleOutline(double[] region, double thickness) {
        StdDraw.setPenRadius(thickness);
        StdDraw.rectangle((region[0] + region[2]) / 2.0, (region[1] + region[3]) / 2.0,
                (region[2] - region[0]) / 2.0, (region[3] - region[1]) / 2.0);
    }

}

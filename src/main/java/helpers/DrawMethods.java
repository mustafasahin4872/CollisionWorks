package helpers;

import game.Frame;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.component.HPBar;

import java.awt.*;

import static game.Main.IMAGES_ROOT;

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

    public static void textInsideBox(Box box, String text) {
        StdDraw.setFont(); StdDraw.setPenColor();
        double yOffset = 16 * 0.15;
        StdDraw.text(box.getCenterX(), box.getCenterY()+yOffset, text);
    }

    public static void textInsideBox(Box box, String text, Color color, int fontSize) {
        StdDraw.setPenColor(color);
        Font font = new Font("Arial", Font.PLAIN, fontSize);
        StdDraw.setFont(font);
        double yOffset = fontSize * 0.15;
        StdDraw.text(box.getCenterX(), box.getCenterY()+yOffset, text);
    }

//    // unused code, previous version of health bar mechanic.
//    private void drawHPBar(HPBar hpBar) {
//
//        //the outline of hp bar
//        double maxHp = hpBar.getMaxHP();
//        double hp = hpBar.getHP();
//        double thickness = 2;
//        double halfHeight = 12;
//        double height = 24;
//        int distance = 15;
//
//        FrameBox outBox = new FrameBox(
//            distance + (maxHp/2.0 + thickness),
//            distance + (halfHeight + thickness),
//            maxHp + 2*thickness, height + 2*thickness);
//        FrameBox inBox = new FrameBox(
//            distance + (hp/2.0 + thickness),
//            distance + (halfHeight + thickness),
//            hp, height
//        );
//
//        outBox.update();
//        inBox.update();
//
//        StdDraw.setPenColor(StdDraw.BLACK);
//        drawRectangle(outBox.getFrameBox());
//
//        //the hp inside the outline
//        StdDraw.setPenColor(StdDraw.GREEN);
//        drawRectangle(inBox.getFrameBox());
//
//
//    }

//    private void drawCoinAmount(double frameX, double frameY, int coinsCollected) {
//        StdDraw.picture(frameX + game.Frame.X_SCALE/2.0 - 80, frameY - game.Frame.Y_SCALE/2.0 + 30, IMAGES_ROOT+"coin/singlecoin/0.png", 40, 40);
//        StdDraw.setFont(); StdDraw.setPenColor();
//        StdDraw.text(frameX + game.Frame.X_SCALE/2.0 - 80, frameY - game.Frame.Y_SCALE/2.0 + 30, "%d".formatted(coinsCollected));
//    }

//    private void drawLifeAmount(double frameX, double frameY, HPBar hpBar) {
//        int side = 40;
//        StdDraw.picture(frameX + game.Frame.X_SCALE/2.0 - 130, frameY - game.Frame.Y_SCALE/2.0 + 30, IMAGES_ROOT+"misc/heart.png", side, side);
//        StdDraw.setFont(); StdDraw.setPenColor();
//        StdDraw.text(frameX + game.Frame.X_SCALE/2.0 - 130, frameY - Frame.Y_SCALE/2.0 + 30, "%d".formatted(hpBar.getLives()));
//    }

//    private void drawAmmo(double frameX, double frameY, HPBar hpBar, int ammo) {
//        double halfHeight = 7;
//        int distance = 15;
//        double baseX = frameX - Frame.X_SCALE/2.0 + distance + hpBar.getMaxHP() + 4*halfHeight;
//        double baseY = frameY - Frame.Y_SCALE / 2.0 + distance + 16;
//        for (int i = 0; i<ammo; i++) {
//            StdDraw.picture(baseX + halfHeight*4*i, baseY, IMAGES_ROOT+"projectile/regularprojectile/0.png", halfHeight*4, halfHeight*2, 45);
//        }
//    }

//    private void drawCriticalHealthEffect(double frameX, double frameY, HPBar hpBar) {
//        double halfThickness = 5;
//        int rectangleCount = (int) (10- hpBar.getRemainingHPPercentage()/3);
//
//        for (int i = 0; i < rectangleCount; i++) {
//            double fadeFactor = 1 - (i / (double) rectangleCount); // linear fade
//            int alpha = (int) (fadeFactor * 255);
//            StdDraw.setPenColor(new Color(123, 9, 9, alpha));
//
//            // LEFT side
//            StdDraw.filledRectangle(
//                frameX - game.Frame.X_SCALE / 2.0 + (2 * i + 1) * halfThickness,
//                frameY,
//                halfThickness,
//                game.Frame.Y_SCALE / 2.0
//            );
//
//            // RIGHT side
//            StdDraw.filledRectangle(
//                frameX + game.Frame.X_SCALE / 2.0 - (2 * i + 1) * halfThickness,
//                frameY,
//                halfThickness,
//                game.Frame.Y_SCALE / 2.0
//            );
//
//            // BOTTOM side
//            StdDraw.filledRectangle(
//                frameX,
//                frameY + game.Frame.Y_SCALE / 2.0 - (2 * i + 1) * halfThickness,
//                game.Frame.X_SCALE / 2.0,
//                halfThickness
//            );
//
//            // TOP side
//            StdDraw.filledRectangle(
//                frameX,
//                frameY - game.Frame.Y_SCALE / 2.0 + (2 * i + 1) * halfThickness,
//                Frame.X_SCALE / 2.0,
//                halfThickness
//            );
//        }
//    }
//

}

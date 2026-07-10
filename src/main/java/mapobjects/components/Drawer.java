package mapobjects.components;

import lib.StdDraw;

import java.awt.*;

import static helpers.methods.DrawMethods.drawRectangle;
import static helpers.methods.DrawMethods.textInsideBox;

public class Drawer {

    private final Box box = new Box(0, 0, 0, 0);
    private String root = null;
    private Color boxColor = null;
    private String text = null;
    private Color textColor = null;
    private Font font = null;

    public Drawer(String root) {
        this.root = root;
    }

    public Drawer(Color boxColor) {
        this.boxColor = boxColor;
    }

    public Drawer(String text, Color textColor) {
        this.text = text;
        this.textColor = textColor;
    }

    public Drawer(Color boxColor, String text, Color textColor) {
        this.boxColor = boxColor;
        this.text = text;
        this.textColor = textColor;
    }

    public void update(double x, double y) {
        box.setCenterCoordinates(x, y);
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public void setBoxColor(Color boxColor) {
        this.boxColor = boxColor;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void draw() {

        if (root != null) {
            StdDraw.picture(box.getCenterX(), box.getCenterY(), root, box.getWidth(), box.getHeight());
        } else {
            if (boxColor != null) {
                StdDraw.setPenColor(boxColor);
                drawRectangle(box);
            }
            if (text != null) {
                StdDraw.setPenColor(textColor);
                textInsideBox(box, text);
            }
        }

    }

}

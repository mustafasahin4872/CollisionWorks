package helpers;

import mapobjects.component.Box;

import java.awt.*;

import static helpers.DrawMethods.drawRectWithOutline;
import static helpers.DrawMethods.textInsideBox;

public class ShopInfo {

    private Font font;
    private Color displayColor, outlineColor, textColor;

    private final String[] infos;
    private Box display;

    private Box[] subBoxes;

    public ShopInfo(String[] infos) {
        this.infos = infos;
    }

    public void configure(Box display, Color displayColor, Color outlineColor, Color textColor) {

        this.display = display;
        this.displayColor = displayColor;
        this.outlineColor = outlineColor;
        this.textColor = textColor;

        int desiredHeight = (int) Math.min(25, display.getHeight() / infos.length);

        int size = 100;
        Font example = new Font("Arial", Font.PLAIN, size);
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(example);
        double height = metrics.getHeight();

        size = (int) (size *  desiredHeight / height);
        font = new Font("Arial", Font.PLAIN, size);

        subBoxes = new Box[infos.length];
        double x = display.getCenterX();
        double startY = display.getCenterY() - display.getHeight()/2 + desiredHeight/2.0;
        for (int i = 0; i<subBoxes.length; i++) {
            subBoxes[i] = new Box(x, startY + i * desiredHeight, display.getWidth(), desiredHeight);
        }
    }

    public void draw() {
        drawRectWithOutline(display, displayColor, outlineColor);
        for (int i = 0; i<infos.length; i++) {
            textInsideBox(subBoxes[i], infos[i], textColor, font);
        }
    }

}

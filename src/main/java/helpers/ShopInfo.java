package helpers;

import lib.StdDraw;
import mapobjects.component.Box;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static helpers.DrawMethods.*;

import mapobjects.category.MapObject;

public class ShopInfo {

    public enum modes {ZERO, DESCRIPTION, STATS};

    private modes mode = modes.ZERO;

    private static final int LINE_HEIGHT = 15;
    private String[] stats;
    private String[] descriptions;

    private String[] statsHolder;
    private final String description;

    private Font font;
    private Box[] subBoxes;

    private static boolean configured = false;

    public ShopInfo(MapObject item) {
        description = item.getDescription();
        statsHolder = item.getStats();
    }

    public void setMode(modes mode) {
        this.mode = mode;
    }

    public void configure(Box box) {

        final double GAP = 5;
        Box display = new Box(box.getCenterX(), box.getCenterY(), box.getWidth() - 2 * GAP, box.getHeight() - 2 * GAP);

        int lineNum = (int) (display.getHeight() / LINE_HEIGHT);
        stats = new String[lineNum];
        descriptions = new String[lineNum];

        int size = getFontSizeForHeight(LINE_HEIGHT, new Font("Arial", Font.PLAIN, 100));
        font = new Font("Arial", Font.PLAIN, size);

        String[] descriptionsHolder = splitTextToFit(description, display.getWidth(), font);

        ArrayList<String> aligned = new ArrayList<>();
        for (String string : statsHolder) {
            aligned.addAll(Arrays.asList(splitTextToFit(string, display.getWidth(), font)));
        }
        statsHolder = aligned.toArray(new String[0]);

        System.arraycopy(statsHolder, 0, stats, 0, Math.min(stats.length, statsHolder.length));
        System.arraycopy(descriptionsHolder, 0, descriptions, 0, Math.min(descriptions.length, descriptionsHolder.length));

        subBoxes = new Box[lineNum];
        double x = display.getCenterX();
        double startY = display.getCenterY() - display.getHeight()/2 + LINE_HEIGHT/2.0;
        for (int i = 0; i<subBoxes.length; i++) {
            subBoxes[i] = new Box(x, startY + i * LINE_HEIGHT, display.getWidth(), LINE_HEIGHT);
        }

        configured = true;
    }


    public void draw() {
        if (!configured) {
            System.out.println("DISPLAY IS NOT CONFIGURED, CANNOT DRAW!");
            return;
        }
        String[] infos = switch (mode) {
            case ZERO -> {
                System.out.println("ERROR: DRAW TRIGGERED WHEN MODE IS ZERO");
                yield descriptions;
            }
            case DESCRIPTION -> descriptions;
            case STATS -> stats;
        };

        for (int i = 0; i<infos.length; i++) {
            if (infos[i] == null) return;
            leftAlignTextInsideBox(subBoxes[i], infos[i], StdDraw.BLACK, font);
        }
    }

}

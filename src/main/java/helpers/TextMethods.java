package helpers;

import java.awt.*;
import java.util.ArrayList;

//static methods for texts
public class TextMethods {

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

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}

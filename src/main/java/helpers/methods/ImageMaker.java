package helpers.methods;

import mapobjects.data.PlayerDefaults;
import mapobjects.entities.Ghost;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import mapobjects.entities.Ghost.ghostTypes;

import static game.core.Main.IMAGES_ROOT;
import static game.core.Main.RESOURCES_ROOT;
import static helpers.methods.HelperMethods.getDirectionString;

// uses a framework txt file to create a png
// use once to create the files, not used as long as the created files are still intact
public class ImageMaker {

    public static void main(String[] args) {

        // player image creation
        for (PLAYERS player : PLAYERS.values()) {
            createAnimationFrames(player);
        }

        //ghost image creation
        for (ghostTypes ghostType : ghostTypes.values()) {
            createGhostImages(ghostType);
        }

    }


    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // PLAYER IMAGE CREATION
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    private static final int ANIMATION_NUMBER = 6;

    public enum PLAYERS {
        Mike(new Color[]{
            new Color(3, 196, 205),     // bodyColor -> _
            new Color(242, 242, 242),   // eyeWhite -> 0
            new Color(236, 125, 35),    // eyeOutline -> *
            new Color(242, 183, 13),    // eyelidColor -> *
            new Color(242, 112, 13),    // eyeSpark -> +
            new Color(242, 223, 56),    // pupilColor -> .
            new Color(0, 0, 0)          // pupilOutline -> X, =
        }),
        Sakura(new Color[]{
            new Color(222, 124, 222),   // bodyColor
            new Color(242, 242, 242),   // eyeWhite
            new Color(177, 45, 192),    // eyeOutline
            new Color(177, 45, 192),    // eyelidColor
            new Color(244, 157, 8),     // eyeSpark
            new Color(220, 84, 220),    // pupilColor
            new Color(124, 17, 131)     // pupilOutline
        });

        private final boolean isAnimated = PlayerDefaults.valueOf(name()).isAnimated();
        private final Color[] colors;

        PLAYERS(Color[] colors) {
            this.colors = colors;
        }

    }

    private static void createAnimationFrames(PLAYERS animatedPlayer) {
        int animationNumber = (animatedPlayer.isAnimated) ? (ANIMATION_NUMBER) : 1;
        for (int i = -1; i<2; i++) { // x direction
            for (int j = -1; j<2; j++) { // y direction
                for (int k = 0; k<animationNumber; k++) {
                    createCharacterFrame(animatedPlayer, i, j, k);
                }
            }
        }
    }

    public static void createCharacterFrame(PLAYERS animatedPlayer, int xDirection, int yDirection, int animationNumber) {
        String name = animatedPlayer.name();
        String root = RESOURCES_ROOT + "frameworks/" + name;
        String bodyFileName = root + "/body.txt";
        String eyeFileName = root + "/eye.txt";

        Color[] colors = animatedPlayer.colors;
        Color bodyColor = colors[0];
        Color eyeWhite = colors[1];
        Color eyeOutline = colors[2];
        Color eyelidColor = colors[3];
        Color eyeSpark = colors[4];
        Color pupilColor = colors[5];
        Color pupilOutline = colors[6];

        ArrayList<String> bodyLines = new ArrayList<>(), eyeLines = new ArrayList<>();

        // Read body
        try (BufferedReader reader = new BufferedReader(new FileReader(bodyFileName))) {
            String line;
            while ((line = reader.readLine()) != null) bodyLines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Read eye
        try (BufferedReader reader = new BufferedReader(new FileReader(eyeFileName))) {
            String line;
            while ((line = reader.readLine()) != null) eyeLines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int height = bodyLines.size();
        int width = bodyLines.getFirst().length();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // === Infer Eye Layout ===
        int eyeWidth = 0, eyeHeight = eyeLines.size(), eyeOutlineWidth = 1;
        int spaceBetweenEyes;

        for (String eyeLine : eyeLines) {
            if (eyeLine.charAt(0) == '_') continue;
            for (int i = 0; i < eyeLine.length(); i++) {
                char c = eyeLine.charAt(i);
                if (c == '_') {
                    eyeWidth = i;
                    break;
                }
            }
            break;
        }

        spaceBetweenEyes = eyeLines.get(0).length() - 2 * eyeWidth - 2 * eyeOutlineWidth;
        int leftEyeStartX = (width - spaceBetweenEyes - 4 * eyeOutlineWidth - 2 * eyeWidth) / 2;
        int rightEyeStartX = leftEyeStartX + eyeOutlineWidth + eyeWidth + eyeOutlineWidth + spaceBetweenEyes;

        // Estimate vertical eye start from first body row with '0' (outline)
        int eyeStartY = 0;
        for (int i = 0; i < height; i++) {
            if (bodyLines.get(i).contains("*")) {
                eyeStartY = i;
                break;
            }
        }

        // === Fill with base ===
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bufferedImage.setRGB(x, y, eyeWhite.getRGB());
            }
        }

        // === Eye direction offset ===
        int xDiff = (xDirection == 0 && yDirection == 0) || (xDirection != 0 && yDirection != 0) ? xDirection : xDirection * 2;
        int yDiff = (xDirection == 0 && yDirection == 0) || (xDirection != 0 && yDirection != 0) ? yDirection : yDirection * 2;

        int startX = leftEyeStartX + eyeOutlineWidth + xDiff;
        int startY = eyeStartY + eyeOutlineWidth + yDiff;

        // === Draw Eyes ===
        for (int row = 0; row < eyeHeight && row + startY < height; row++) {
            String eyeLine = eyeLines.get(row);
            for (int col = 0; col < eyeLine.length() && col + startX < width; col++) {
                char ch = eyeLine.charAt(col);
                Color pixelColor = switch (ch) {
                    case '0' -> eyeWhite;
                    case 'X' -> pupilOutline;
                    case '.' -> pupilColor;
                    case '+' -> eyeSpark;
                    default -> eyeWhite;
                };
                bufferedImage.setRGB(startX + col, startY + row, pixelColor.getRGB());
            }
        }

        // === Eyelid Animation ===
        int eyelidLength = 3 * animationNumber;
        drawCurvedEyelid(bufferedImage, leftEyeStartX + eyeOutlineWidth, eyeWidth, eyeStartY, eyeHeight, eyelidLength, eyelidColor, eyeOutline);
        drawCurvedEyelid(bufferedImage, rightEyeStartX + eyeOutlineWidth, eyeWidth, eyeStartY, eyeHeight, eyelidLength, eyelidColor, eyeOutline);

        // === Draw Body ===
        for (int y = 0; y < height; y++) {
            String line = bodyLines.get(y);
            for (int x = 0; x < width && x < line.length(); x++) {
                switch (line.charAt(x)) {
                    case '_' -> bufferedImage.setRGB(x, y, bodyColor.getRGB());
                    case '*' -> bufferedImage.setRGB(x, y, eyeOutline.getRGB());
                    case '=' -> bufferedImage.setRGB(x, y, pupilOutline.getRGB());
                }
            }
        }

        // === Save ===
        String direction = getDirectionString(xDirection, yDirection);
        String path;
        if (animatedPlayer.isAnimated) {
            path = IMAGES_ROOT + "player/regularplayer/" + name + "/" + direction + ".png";
            File output = new File(path);
            createPng(bufferedImage, output);
        } else {
            path = IMAGES_ROOT + "player/animatedplayer/" + name + "/" + direction + "_" + animationNumber + ".png";
            File output = new File(path);
            createJpg(bufferedImage, output);
        }

    }


    private static void drawCurvedEyelid(BufferedImage image, int startX, int eyeWidth, int startY, int eyeHeight, int eyelidLength, Color eyelidColor, Color eyelidOutline) {
        int midLimit = startY + eyelidLength;
        int sideLimit = midLimit - 1;

        int sideWidth = eyeWidth / 3;
        int midWidth = eyeWidth - 2*sideWidth;
        HashSet<Integer> midNums = new HashSet<>();
        for (int i = sideWidth; i<sideWidth+midWidth; i++) {
            midNums.add(i);
        }

        for (int row = startY; row < startY + eyeHeight + 2; row++) {  // was eyeWidth, now correctly eyeHeight
            if (row > midLimit) break;

            int count = 0;
            for (int col = startX; col < startX + eyeWidth; col++) {
                if (midNums.contains(count)) {
                    if (row < midLimit) {
                        image.setRGB(col, row, eyelidColor.getRGB());
                    } else if (row == midLimit) {
                        image.setRGB(col, row, eyelidOutline.getRGB());
                    }
                } else {
                    if (row < sideLimit) {
                        image.setRGB(col, row, eyelidColor.getRGB());
                    } else if (row == sideLimit) {
                        image.setRGB(col, row, eyelidOutline.getRGB());
                    }
                }
                count++;
            }
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // GHOST IMAGE CREATION
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    public static final Map<ghostTypes, Color[]> ghostColors = new HashMap<>();

    private static final int transparency = 20; //percentage
    private static final int alpha = (int) ((1-transparency/100.0) * 255);
    static {
        // Shared second colors (with alpha alpha)
        Color defaultEyeColor = new Color(10, 10, 80, alpha); // Dark Blue
        Color specialEyeColor = new Color(30, 30, 100, alpha); // Lighter Dark Blue

        ghostColors.put(Ghost.ghostTypes.DEMON, new Color[]{new Color(0, 0, 0, alpha),
                                                            new Color(255, 0, 0, alpha)});         // black + red
        ghostColors.put(Ghost.ghostTypes.ANGEL, new Color[]{new Color(0, 255, 255, alpha),
                                                            new Color(255, 255, 0, alpha)});   // cyan + yellow
        ghostColors.put(Ghost.ghostTypes.BLUE, new Color[]{new Color(173, 216, 230, alpha),
                                                           new Color(100, 149, 237, alpha)}); // light blue + slightly darker
        ghostColors.put(Ghost.ghostTypes.WHITE, new Color[]{new Color(255, 255, 255, alpha),
                                                            new Color(211, 211, 211, alpha)}); // white + light gray
        ghostColors.put(Ghost.ghostTypes.GOLD, new Color[]{new Color(255, 215, 0, alpha),
                                                           new Color(255, 165, 0, alpha)});     // gold + orange
        ghostColors.put(Ghost.ghostTypes.SAKURA, new Color[]{new Color(255, 182, 193, alpha),
                                                             new Color(216, 191, 216, alpha)}); // powder pink + light purple
        ghostColors.put(Ghost.ghostTypes.RANDOM, new Color[]{new Color((int) (Math.random()*255), (int) (Math.random()*255), (int) (Math.random()*255), alpha),
                                                             new Color((int) (Math.random()*255), (int) (Math.random()*255), (int) (Math.random()*255), alpha)}); // random

        // Defaults: custom color + shared default second color
        ghostColors.put(Ghost.ghostTypes.DEFAULT1, new Color[]{new Color(255, 105, 180, alpha), defaultEyeColor}); // pink
        ghostColors.put(Ghost.ghostTypes.DEFAULT2, new Color[]{new Color(173, 216, 230, alpha), defaultEyeColor}); // ice blue
        ghostColors.put(Ghost.ghostTypes.DEFAULT3, new Color[]{new Color(255, 69, 0, alpha), defaultEyeColor}); // lava orange
        ghostColors.put(Ghost.ghostTypes.DEFAULT4, new Color[]{new Color(64, 224, 208, alpha), defaultEyeColor}); // turquoise

        // Specials: custom color + shared special second color
        ghostColors.put(Ghost.ghostTypes.SPECIAL1, new Color[]{new Color(0, 128, 0, alpha), specialEyeColor}); // leaf green
        ghostColors.put(Ghost.ghostTypes.SPECIAL2, new Color[]{new Color(245, 245, 245, alpha),
                                                               specialEyeColor}); // snow white/gray
        ghostColors.put(Ghost.ghostTypes.SPECIAL3, new Color[]{new Color(139, 0, 0, alpha), specialEyeColor}); // dark red
        ghostColors.put(Ghost.ghostTypes.SPECIAL4, new Color[]{new Color(72, 209, 204, alpha),
                                                               specialEyeColor}); // darker crystal blue/green
    }

    private static void createGhostImages(ghostTypes ghostType) {
        createGhostImage(ghostType, 0, 0);
        createGhostImage(ghostType, 1, 0);
        createGhostImage(ghostType, -1, 0);
        createGhostImage(ghostType, 0, 1);
        createGhostImage(ghostType, 0, -1);
    }

    public static void createGhostImage(ghostTypes type, int xDirection, int yDirection) {
        // Step 1: Determine direction string
        String direction = getDirectionString(xDirection, yDirection);

        // Step 2: Load from txt file
        String pathName = RESOURCES_ROOT + "frameworks/ghost/" + direction + ".txt";
        File file = new File(pathName);

        Color[] colors = ghostColors.get(type);
        Color transparent = new Color(255, 255, 255, 0);
        Color eyeWhite = new Color(255, 255, 255, alpha);
        Color bodyColor = colors[0];
        Color eyeColor = colors[1];

        BufferedImage bufferedImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int y = 0;
            while ((line = reader.readLine()) != null && y < 32) {
                for (int x = 0; x < Math.min(line.length(), 32); x++) {
                    char ch = line.charAt(x);
                    Color pixelColor = switch (ch) {
                        case '0' -> bodyColor;
                        case '*' -> eyeWhite;
                        case '1' -> eyeColor;
                        case '_' -> transparent;
                        default -> transparent;
                    };
                    bufferedImage.setRGB(x, y, pixelColor.getRGB());
                }
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Step 3: Auto-generate file name

        // Step 4: Save the PNG
        File output = new File(IMAGES_ROOT + "ghost/" + type.name() + direction + ".png");
        createPng(bufferedImage, output);

    }


    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // HELPERS
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    public static void createPng(BufferedImage bufferedImage, File output) {
        try {
            ImageIO.write(bufferedImage, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createJpg(BufferedImage bufferedImage, File output) {
        // JPG doesn't support transparency, so we'll convert the image first
        BufferedImage rgbImage = new BufferedImage(
                bufferedImage.getWidth(),
                bufferedImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        // Fill with white background and draw the original image on top
        Graphics2D g = rgbImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();

        try {
            ImageIO.write(rgbImage, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

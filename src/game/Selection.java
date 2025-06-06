package game;

import lib.StdDraw;
import helperobjects.Drawable;
import mapobjects.MapObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;


public class Selection implements Drawable {

    private int totalCoin=0;
    private int currentWorldIndex = 0, currentLevelIndex = 1,
            currentAccessoryIndex = 0, currentSkinIndex = 0;

    private Player currentSkin;
    private Accessory currentAccessory;
    private static final int
            X_TILE = (int)(Frame.X_SCALE/ MapObject.TILE_SIDE),
            Y_TILE = (int)(Frame.Y_SCALE/MapObject.TILE_SIDE);

    private final ArrayList<Accessory> selectedAccessories = new ArrayList<>();

    //the selection screens
    private static final GameMap[] WORLDS = {
            new GameMap(1, 14, X_TILE, Y_TILE, new Player()),
            new GameMap(1, 13, X_TILE, Y_TILE, new Player()),
            new GameMap(2, 13, X_TILE, Y_TILE, new Player()),
            new GameMap(3, 13, X_TILE, Y_TILE, new Player()),
            new GameMap(4, 13, X_TILE, Y_TILE, new Player())
    };

    private static final Player[] SKINS = {
            new Player(),
            new Player("Zahit"),
    };

    private static final Accessory[] ACCESSORIES = {
            null,
            new Accessory.Hat("fedora", 65, 25, WORLDS[0].getPlayer()),
            new Accessory.Headpiece("coquette", 20, 20, WORLDS[0].getPlayer())
    };
    private static final boolean[] ACCESSORY_CHOSEN = new boolean[ACCESSORIES.length];

    public static final Color[] WORLD_COLORS = {
            new Color(1,1,1),
            new Color(119, 14, 155, 255),
            new Color(25, 127, 180),
            new Color(233, 116, 49),
            new Color(199, 193, 189)
    };

    private static final String[] WORLD_NAMES = {
            null, "THE SPRING FESTIVAL", "INTO THE ICE CAVE", "TO THE TOP OF THE VOLCANO", "CRYSTAL PALACE"
    };

    private static final double BUTTON_HALF_SIDE = 0.5;
    private static final double BUTTON_SIDE = BUTTON_HALF_SIDE*2;
    //centers of buttons

    private final double[][] leftArrowCoordinates = {
            null,
            {1 * BUTTON_SIDE, Y_TILE - 5 * BUTTON_SIDE},
            {1 * BUTTON_SIDE, Y_TILE - 5 * BUTTON_SIDE},
            {1 * BUTTON_SIDE, Y_TILE - 5 * BUTTON_SIDE},
            {1 * BUTTON_SIDE, Y_TILE - 8 * BUTTON_SIDE},
    };
    private final double[][] rightArrowCoordinates = {
            {X_TILE - 1 * BUTTON_SIDE, Y_TILE - 8 * BUTTON_SIDE},
            {X_TILE - 1 * BUTTON_SIDE, Y_TILE - 8 * BUTTON_SIDE},
            {X_TILE - 1 * BUTTON_SIDE, Y_TILE - 7 * BUTTON_SIDE},
            {X_TILE - 1 * BUTTON_SIDE, Y_TILE - 4 * BUTTON_SIDE},
            null
    };
    private final double[] accessoryLeftCoordinates = new double[]{
            4*BUTTON_SIDE+BUTTON_HALF_SIDE, Y_TILE-8-BUTTON_HALF_SIDE
    }, accessoryRightCoordinates = new double[]{
            X_TILE-2-4*BUTTON_SIDE-BUTTON_HALF_SIDE ,Y_TILE-8-BUTTON_HALF_SIDE
    }, accessoryChooseCoordinates = new double[]{
            (accessoryLeftCoordinates[0]+accessoryRightCoordinates[0])/2, Y_TILE-9-BUTTON_HALF_SIDE
    };

    private final double[][] leftArrowBox = new double[5][4], rightArrowBox = new double[5][4];
    private final double[][] levelBoxCoordinates = new double[12][2];
    private final double[] accessoryLeftBox = new double[4], accessoryRightBox = new double[4], accessoryChooseBox = new double[4];

    public Selection() {

        fillArrowBoxes();

        fillLevelBoxes();

        for (Player skin : SKINS) {
            skin.setSpawnPoint(WORLDS[0].getPlayer().getSpawnPoint());
        }

    }


    private boolean levelChosen(){
        return currentWorldIndex!=0 && StdDraw.isKeyPressed(KeyEvent.VK_SPACE);
    }

    public GameMap chooseLevel() {
        while (!levelChosen()) {
            handleInput();
            StdDraw.clear();
            draw();
            StdDraw.show();
            StdDraw.pause(10* Frame.PAUSE);
        }

        for (int i = 0; i<ACCESSORY_CHOSEN.length; i++) {
            if (ACCESSORY_CHOSEN[i]) {
                ACCESSORIES[i].setPlayer(currentSkin);
                selectedAccessories.add(ACCESSORIES[i]);
            }
        }

        return new GameMap(currentWorldIndex, currentLevelIndex, currentSkin, selectedAccessories.toArray(new Accessory[0]));
    }

    private void handleInput() {

        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();


        if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
            currentLevelIndex++;
            if (currentWorldIndex==0) {
                currentSkinIndex++;
            }
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
            currentLevelIndex--;
            if (currentWorldIndex==0) {
                currentSkinIndex--;
            }
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
            currentLevelIndex+=5;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
            currentLevelIndex-=5;
        }
        currentSkinIndex = (currentSkinIndex + SKINS.length) % SKINS.length;
        currentLevelIndex = ((currentLevelIndex - 1) % 12 + 12) % 12 + 1;


        if (StdDraw.isMousePressed()) {
            if (currentWorldIndex!=0) {
                if (GameMap.isIn(mouseX, mouseY, leftArrowBox[currentWorldIndex])) {
                    currentWorldIndex--;
                    currentLevelIndex=1;
                }
                for (int i = 0; i< levelBoxCoordinates.length; i++) {
                    if (levelBoxCoordinates[i][0]- BUTTON_HALF_SIDE < mouseX &&
                            levelBoxCoordinates[i][0] + BUTTON_HALF_SIDE > mouseX &&
                            levelBoxCoordinates[i][1] - BUTTON_HALF_SIDE < mouseY &&
                            levelBoxCoordinates[i][1] + BUTTON_HALF_SIDE > mouseY) {
                        currentLevelIndex = i+1;
                    }
                }
            } else {
                if (GameMap.isIn(mouseX, mouseY, accessoryLeftBox)) {
                    currentAccessoryIndex--;
                } else if (GameMap.isIn(mouseX, mouseY, accessoryRightBox)) {
                    currentAccessoryIndex++;
                }
                currentAccessoryIndex = (currentAccessoryIndex + ACCESSORIES.length) % ACCESSORIES.length;
                if (GameMap.isIn(mouseX, mouseY, accessoryChooseBox)) {
                    ACCESSORY_CHOSEN[currentAccessoryIndex] = !ACCESSORY_CHOSEN[currentAccessoryIndex];
                    StdDraw.pause(Frame.PAUSE*10);
                    if (ACCESSORY_CHOSEN[0]) {
                        Arrays.fill(ACCESSORY_CHOSEN, false);
                    }
                }

            }
            if (currentWorldIndex!=WORLDS.length-1){
                if (GameMap.isIn(mouseX, mouseY, rightArrowBox[currentWorldIndex])) {
                    currentWorldIndex++;
                    currentLevelIndex=1;
                }
            }

        }

    }

    @Override
    public void draw() {

        GameMap currentWorld = WORLDS[currentWorldIndex];
        currentSkin = SKINS[(currentSkinIndex)%SKINS.length];
        currentAccessory = ACCESSORIES[currentAccessoryIndex];
        StdDraw.setXscale(0, Frame.X_SCALE);
        StdDraw.setYscale(Frame.Y_SCALE, 0);
        currentWorld.draw();
        if (currentWorldIndex==0) {
            currentSkin.drawBig(2.5);
            for (int i = 0; i<ACCESSORY_CHOSEN.length; i++) {
                if (ACCESSORY_CHOSEN[i]) {
                    ACCESSORIES[i].drawBig(2.5);
                }
            }
            if (currentAccessory!=null) {
                currentAccessory.drawBig(2.5);
            }
        }
        StdDraw.setXscale(0, X_TILE);
        StdDraw.setYscale(0, Y_TILE);
        //world name
        StdDraw.setPenColor(WORLD_COLORS[currentWorldIndex]);

        if (currentWorldIndex == 0) {
            StdDraw.setFont(new Font("Monospaced", Font.BOLD, 50));
            StdDraw.text(X_TILE/2.0-1, Y_TILE-2-BUTTON_HALF_SIDE-0.15, currentSkin.getName());
            StdDraw.setFont(new Font("Monospaced", Font.BOLD, 30));
            if (currentAccessory!=null) {
                StdDraw.text(X_TILE/2.0-1, Y_TILE-8-BUTTON_HALF_SIDE, currentAccessory.getName());
            } else {
                StdDraw.text(X_TILE/2.0-1, Y_TILE-8-BUTTON_HALF_SIDE, "no accessory");
            }
            StdDraw.text(accessoryLeftCoordinates[0], accessoryLeftCoordinates[1], "<");
            StdDraw.text(accessoryRightCoordinates[0], accessoryRightCoordinates[1], ">");
            if (ACCESSORY_CHOSEN[currentAccessoryIndex]) {
                StdDraw.text(accessoryChooseCoordinates[0], accessoryChooseCoordinates[1], "✅");
            } else {
                StdDraw.text(accessoryChooseCoordinates[0], accessoryChooseCoordinates[1], "❎");
            }

        } else {
            StdDraw.setFont(new Font("Monospaced", Font.BOLD, 30));
            StdDraw.text(X_TILE/2.0, Y_TILE-2- BUTTON_HALF_SIDE, WORLD_NAMES[currentWorldIndex]);
        }


        //right and left arrow buttons
        StdDraw.setFont(new Font("Monospaced", Font.BOLD, 90));
        if (currentWorldIndex!=0) {
            StdDraw.text(leftArrowCoordinates[currentWorldIndex][0], leftArrowCoordinates[currentWorldIndex][1], "<");
        }
        if (currentWorldIndex!=4) {
            StdDraw.text(rightArrowCoordinates[currentWorldIndex][0], rightArrowCoordinates[currentWorldIndex][1], ">");
        }


        //level buttons
        if (currentWorldIndex!=0) {
            drawLevelButtons();
        }


    }

    private void drawLevelButtons() {
        StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
        StdDraw.setPenRadius(0.01);
        for (int i = 0; i< levelBoxCoordinates.length; i++) {
            double[] currentButton = levelBoxCoordinates[i];
            StdDraw.setPenColor();
            StdDraw.square(currentButton[0], currentButton[1], BUTTON_HALF_SIDE);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(currentButton[0]+ BUTTON_HALF_SIDE /2, currentButton[1]- BUTTON_HALF_SIDE /2, ""+(i+1));
            if (i+1 == currentLevelIndex) {
                StdDraw.square(currentButton[0], currentButton[1], BUTTON_HALF_SIDE);
            }
        }
    }

    private void fillLevelBoxes() {
        for (int i = 0; i<3; i++) {
            for (int j = 0; j<4; j++) {
                levelBoxCoordinates[i*4+j] = new double[]{
                        BUTTON_HALF_SIDE + 4*BUTTON_SIDE + j*3*BUTTON_SIDE,
                        Y_TILE - BUTTON_HALF_SIDE -4*BUTTON_SIDE-2*i*BUTTON_SIDE};
            }
        }
    }

    private void fillArrowBoxes() {
        for (int i = 0; i<5; i++) {
            if (leftArrowCoordinates[i]!=null) {
                fillBoxes(leftArrowCoordinates[i], leftArrowBox[i], BUTTON_SIDE);
            }
            if (rightArrowCoordinates[i] != null) {
                fillBoxes(rightArrowCoordinates[i], rightArrowBox[i], BUTTON_SIDE);
            }
        }

        fillBoxes(accessoryLeftCoordinates, accessoryLeftBox, BUTTON_HALF_SIDE);
        fillBoxes(accessoryRightCoordinates, accessoryRightBox, BUTTON_HALF_SIDE);
        fillBoxes(accessoryChooseCoordinates, accessoryChooseBox, BUTTON_HALF_SIDE);

    }

    private void fillBoxes(double[] coordinates, double[] fillBox, double side) {

        fillBox[0] = coordinates[0] - side;
        fillBox[1] = coordinates[1] - side;
        fillBox[2] = coordinates[0] + side;
        fillBox[3] = coordinates[1] + side;

    }
}

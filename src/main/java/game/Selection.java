package game;

import helpers.MapType;
import lib.StdDraw;
import mapobjects.category.GridObject;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import game.InputHandler.MouseData;
import game.InputHandler.ArrowData;
import game.GameState.STATE;

import static helpers.CollisionMethods.isIn;

public class Selection {

    private final GameState gameState;
    private final InputHandler inputHandler;

    private int currentWorldIndex = 0, currentLevelIndex = 1,
            currentAccessoryIndex = 0, currentSkinIndex = 0;

    private Player currentSkin;

    private final ArrayList<Accessory> selectedAccessories = new ArrayList<>();

    //the selection screens
    private static final GameMap[] WORLDS = {
            new GameMap(1, -1, new Player.RegularPlayer(), MapType.SELECTION),
            new GameMap(1, 0, new Player.RegularPlayer(), MapType.SELECTION),
            new GameMap(2, 0, new Player.RegularPlayer(), MapType.SELECTION),
            new GameMap(3, 0, new Player.RegularPlayer(), MapType.SELECTION),
            new GameMap(4, 0, new Player.RegularPlayer(), MapType.SELECTION)
    };

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

    private static final Player[] SKINS = {
        new Player.RegularPlayer(),
        new Player.AnimatedPlayer("Mike", 6),
        new Player.RegularPlayer("Zahit")
    };

    private static final Accessory[] ACCESSORIES = {
        null,
        new Accessory.Hat("fedora", 65, 25, WORLDS[0].getPlayer()),
        new Accessory.Headpiece("coquette", 20, 20, WORLDS[0].getPlayer())
    };

    private static final boolean[] ACCESSORY_CHOSEN = new boolean[ACCESSORIES.length];


    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------

    private static final int
        X_TILE = (int)(Frame.X_SCALE/ GridObject.TILE_SIDE),
        Y_TILE = (int)(Frame.Y_SCALE/ GridObject.TILE_SIDE);

    //centers of buttons
    private final double[][]
            leftArrowCoordinates = {null, {1, Y_TILE - 5}, {1, Y_TILE - 5},
                                    {1, Y_TILE - 5}, {1, Y_TILE - 8}},
            rightArrowCoordinates = {{X_TILE - 1, Y_TILE - 8}, {X_TILE - 1, Y_TILE - 8},
                                     {X_TILE - 1, Y_TILE - 7}, {X_TILE - 1, Y_TILE - 4}, null};

    private final double[]
            accessoryLeftCoordinates = new double[]{4+0.5, Y_TILE-6.5-0.5},
            accessoryRightCoordinates = new double[]{X_TILE-2-4-0.5 ,Y_TILE-6.5-0.5},
            accessoryChooseCoordinates = new double[]{(accessoryLeftCoordinates[0]+accessoryRightCoordinates[0])/2, Y_TILE-7.5-0.5},
            shopCoordinates = new double[]{8, 11};

    private final double[][]
            leftArrowBox = new double[5][4], rightArrowBox = new double[5][4],
            levelBoxCoordinates = new double[12][2];
    private final double[]
            accessoryLeftBox = new double[4], accessoryRightBox = new double[4],
            accessoryChooseBox = new double[4], shopBox = new double[4];

    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------


    // keep the current gameState
    public Selection(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;

        fillArrowBoxes();
        fillLevelBoxes();
    }

    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------

    public void selectionLoop() {

        for (Player skin : SKINS) {
            skin.setSpawnPoint(WORLDS[0].getSpawnPoint());
            skin.restart();
        }

        for (Accessory accessory : ACCESSORIES) {
            if (accessory!=null) accessory.update();
        }

        while (gameState.getState() == STATE.SELECTION) {
            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();
            ArrowData arrowData = inputHandler.getArrowData();
            processInput(mouseData, arrowData);
            draw();
        }

        wireAccessoriesWithPlayer();
        currentSkin.setWorldIndex(currentWorldIndex);

        gameState.worldIndex = currentWorldIndex;
        gameState.levelIndex = currentLevelIndex;
        gameState.player = currentSkin;
        gameState.setState(STATE.NEXT);

    }

    private void wireAccessoriesWithPlayer() {
        for (int i = 0; i<ACCESSORY_CHOSEN.length; i++) {
            if (ACCESSORY_CHOSEN[i]) {
                Accessory accessory = ACCESSORIES[i];
                accessory.setPlayer(currentSkin);
                selectedAccessories.add(ACCESSORIES[i]);
            }
        }
        currentSkin.setAccessories(selectedAccessories.toArray(new Accessory[0]));
    }

    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------


    private void processInput(MouseData mouseData, ArrowData arrowData) {

        double mouseX = mouseData.mouseX;
        double mouseY = mouseData.mouseY;
        boolean mousePressed = mouseData.pressed;
        boolean rightArrowPressed = arrowData.xDirection == 1;
        boolean leftArrowPressed = arrowData.xDirection == -1;
        boolean upArrowPressed = arrowData.yDirection == -1;
        boolean downArrowPressed = arrowData.yDirection == 1;
        boolean spacePressed = arrowData.space;

        if (currentWorldIndex!=0 && spacePressed) {
            gameState.setState(STATE.NEXT);
        }

        if (rightArrowPressed) {
            currentLevelIndex++;
            if (currentWorldIndex==0) {
                currentSkinIndex++;
            }
        }
        if (leftArrowPressed) {
            currentLevelIndex--;
            if (currentWorldIndex==0) {
                currentSkinIndex--;
            }
        }
        if (downArrowPressed) {
            currentLevelIndex+=4;
        }
        if (upArrowPressed) {
            currentLevelIndex-=4;
        }
        currentSkinIndex = (currentSkinIndex + SKINS.length) % SKINS.length;
        currentLevelIndex = ((currentLevelIndex - 1) % 12 + 12) % 12 + 1;

        if (mousePressed) {
            if (currentWorldIndex!=0) {
                if (isIn(mouseX, mouseY, leftArrowBox[currentWorldIndex])) {
                    currentWorldIndex--;
                    currentLevelIndex=1;
                }
                for (int i = 0; i< levelBoxCoordinates.length; i++) {
                    if (levelBoxCoordinates[i][0]- 0.5 < mouseX &&
                            levelBoxCoordinates[i][0] + 0.5 > mouseX &&
                            levelBoxCoordinates[i][1] - 0.5 < mouseY &&
                            levelBoxCoordinates[i][1] + 0.5 > mouseY) {
                        currentLevelIndex = i+1;
                    }
                }
            } else {
                if (isIn(mouseX, mouseY, accessoryLeftBox)) {
                    currentAccessoryIndex--;
                } else if (isIn(mouseX, mouseY, accessoryRightBox)) {
                    currentAccessoryIndex++;
                }
                currentAccessoryIndex = (currentAccessoryIndex + ACCESSORIES.length) % ACCESSORIES.length;
                if (isIn(mouseX, mouseY, accessoryChooseBox)) {
                    ACCESSORY_CHOSEN[currentAccessoryIndex] = !ACCESSORY_CHOSEN[currentAccessoryIndex];
                    StdDraw.pause(Frame.PAUSE*10);
                    if (ACCESSORY_CHOSEN[0]) {
                        Arrays.fill(ACCESSORY_CHOSEN, false);
                    }
                }

            }
            if (currentWorldIndex!=WORLDS.length-1){
                if (isIn(mouseX, mouseY, rightArrowBox[currentWorldIndex])) {
                    currentWorldIndex++;
                    currentLevelIndex=1;
                }
            }

        }

    }

    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------


    public void draw() {
        StdDraw.clear();

        GameMap currentWorld = WORLDS[currentWorldIndex];
        currentSkin = SKINS[(currentSkinIndex)%SKINS.length];
        Accessory currentAccessory = ACCESSORIES[currentAccessoryIndex];

        StdDraw.setXscale(0, Frame.X_SCALE);
        StdDraw.setYscale(Frame.Y_SCALE, 0);
        currentWorld.draw();
        if (currentWorldIndex==0) {
            double multiplier = 2.5;
            currentSkin.drawBig(multiplier);
            if (currentAccessory !=null) {
                currentAccessory.setPlayer(currentSkin);
                currentAccessory.drawBig(multiplier);
            }
            for (int i = 0; i<ACCESSORY_CHOSEN.length; i++) {
                if (ACCESSORY_CHOSEN[i]) {
                    ACCESSORIES[i].drawBig(multiplier);
                }
            }
        }
        StdDraw.setXscale(0, X_TILE);
        StdDraw.setYscale(0, Y_TILE);
        //world name
        StdDraw.setPenColor(WORLD_COLORS[currentWorldIndex]);

        if (currentWorldIndex == 0) {
            StdDraw.setFont(new Font("Monospaced", Font.BOLD, 50));
            StdDraw.text(X_TILE/2.0-1, Y_TILE-1.5-0.5-0.15, currentSkin.getPlayerName());
            StdDraw.setFont(new Font("Monospaced", Font.BOLD, 30));
            if (currentAccessory !=null) {
                StdDraw.text(X_TILE/2.0-1, Y_TILE-6.5-0.5, currentAccessory.getAccessoryName());
            } else {
                StdDraw.text(X_TILE/2.0-1, Y_TILE-6.5-0.5, "no accessory");
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
            StdDraw.text(X_TILE/2.0, Y_TILE-2- 0.5, WORLD_NAMES[currentWorldIndex]);
        }

        //level buttons
        if (currentWorldIndex!=0) {
            drawLevelButtons();
        }

        StdDraw.show();
        StdDraw.pause(10* Frame.PAUSE);
    }

    private void drawLevelButtons() {
        StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
        StdDraw.setPenRadius(0.01);
        for (int i = 0; i< levelBoxCoordinates.length; i++) {
            double[] currentButton = levelBoxCoordinates[i];
            StdDraw.setPenColor();
            StdDraw.square(currentButton[0], currentButton[1], 0.5);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(currentButton[0]+ 0.5 /2, currentButton[1]- 0.5 /2, ""+(i+1));
            if (i+1 == currentLevelIndex) {
                StdDraw.square(currentButton[0], currentButton[1], 0.5);
            }
        }
    }


    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------


    private void fillLevelBoxes() {
        for (int i = 0; i<3; i++) {
            for (int j = 0; j<4; j++) {
                levelBoxCoordinates[i*4+j] = new double[]{
                        0.5 + 4 + j*3,
                        Y_TILE - 0.5 - 4 -2*i};
            }
        }
        fillBoxes(shopCoordinates, shopBox, 2);
    }

    private void fillArrowBoxes() {
        for (int i = 0; i<5; i++) {
            if (leftArrowCoordinates[i]!=null) {
                fillBoxes(leftArrowCoordinates[i], leftArrowBox[i], 1);
            }
            if (rightArrowCoordinates[i] != null) {
                fillBoxes(rightArrowCoordinates[i], rightArrowBox[i], 1);
            }
        }

        fillBoxes(accessoryLeftCoordinates, accessoryLeftBox, 0.5);
        fillBoxes(accessoryRightCoordinates, accessoryRightBox, 0.5);
        fillBoxes(accessoryChooseCoordinates, accessoryChooseBox, 0.5);

    }

    private static void fillBoxes(double[] coordinates, double[] fillBox, double side) {

        fillBox[0] = coordinates[0] - side;
        fillBox[1] = coordinates[1] - side;
        fillBox[2] = coordinates[0] + side;
        fillBox[3] = coordinates[1] + side;

    }

}

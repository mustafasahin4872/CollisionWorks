package game;

import helpers.MapType;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import game.InputHandler.MouseData;
import game.InputHandler.ArrowData;
import game.GameState.STATE;

import static game.SelectionConstants.*;
import static helpers.CollisionMethods.isIn;

public class Selection {

    private final GameState gameState;
    private final InputHandler inputHandler;

    private int currentWorldIndex = 0;
    private int currentLevelIndex = 1;
    private int currentAccessoryIndex = 0;
    private int currentSkinIndex = 0;

    //the selection screens

    public static final GameMap[] WORLDS = {
        new GameMap(1, -1, new Player.RegularPlayer(), MapType.SELECTION),
        new GameMap(1, 0, new Player.RegularPlayer(), MapType.SELECTION),
        new GameMap(2, 0, new Player.RegularPlayer(), MapType.SELECTION),
        new GameMap(3, 0, new Player.RegularPlayer(), MapType.SELECTION),
        new GameMap(4, 0, new Player.RegularPlayer(), MapType.SELECTION)
    };

    public final Player[] skins = {
        new Player.RegularPlayer(),
        new Player.AnimatedPlayer("Mike", 6),
        new Player.RegularPlayer("Zahit")
    };

    public final Accessory[] accessories = {
        null,
        new Accessory.Hat("fedora", WORLDS[0].getPlayer()),
        new Accessory.Headpiece("coquette", WORLDS[0].getPlayer()),
        new Accessory.Pin("badge", WORLDS[0].getPlayer()),
        new Accessory.Pin("badge2", WORLDS[0].getPlayer()),
        new Accessory.Necklace("dollar", WORLDS[0].getPlayer()),
        new Accessory.Necklace("sorcerer", WORLDS[0].getPlayer()),
        new Accessory.Tie("tie", WORLDS[0].getPlayer())
    };

    private final boolean[] accessoryChosen = new boolean[accessories.length];


    //----------------------------------------------------------------------------------------------------------

    public Selection(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;
    }

    //----------------------------------------------------------------------------------------------------------

    public void selectionLoop() {

        for (Player skin : skins) {
            skin.setSpawnPoint(WORLDS[0].getSpawnPoint());
            skin.restart();
        }

        for (Accessory accessory : accessories) {
            if (accessory!=null) accessory.update();
        }

        while (gameState.getState() == STATE.SELECTION) {
            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();
            ArrowData arrowData = inputHandler.getArrowData();
            processInput(mouseData, arrowData);
            draw();
        }

        //TODO: ADD STATE TRACKING HERE
        //TODO: ADD SHOP SCREEN

        Player player = skins[currentSkinIndex];

        wireAccessoriesWithPlayer(player);
        player.setWorldIndex(currentWorldIndex);

        gameState.worldIndex = currentWorldIndex;
        gameState.levelIndex = currentLevelIndex;
        gameState.player = player;

    }

    private void wireAccessoriesWithPlayer(Player player) {

        ArrayList<Accessory> selectedAccessories = new ArrayList<>();

        for (int i = 0; i< accessoryChosen.length; i++) {
            if (accessoryChosen[i]) {
                Accessory accessory = accessories[i];
                accessory.setPlayer(player);
                selectedAccessories.add(accessories[i]);
            }
        }
        player.setAccessories(selectedAccessories.toArray(new Accessory[0]));
    }

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

        if (currentWorldIndex!=0) {
            if (spacePressed) gameState.setState(STATE.NEXT);
            if (rightArrowPressed) currentLevelIndex++;
            if (leftArrowPressed) currentLevelIndex--;
            if (downArrowPressed) currentLevelIndex+=4;
            if (upArrowPressed) currentLevelIndex-=4;
            if (mousePressed) {
                if (isIn(mouseX, mouseY, LEFT_ARROW_BOX[currentWorldIndex])) {
                    currentWorldIndex--;
                    currentLevelIndex=1;
                }
                for (int i = 0; i< LEVEL_BOX_COORDINATES.length; i++) {
                    Box box = new Box(LEVEL_BOX_COORDINATES[i][0], LEVEL_BOX_COORDINATES[i][1], 0.5, 0.5);
                    if (isIn(mouseX, mouseY, box)) {
                        currentLevelIndex = i+1;
                    }
                }
            }
        } else {
            if (rightArrowPressed) currentSkinIndex++;
            if (leftArrowPressed) currentSkinIndex--;
            if (mousePressed) {
                if (isIn(mouseX, mouseY, ACCESSORY_LEFT_BOX)) {
                    currentAccessoryIndex--;
                } else if (isIn(mouseX, mouseY, ACCESSORY_RIGHT_BOX)) {
                    currentAccessoryIndex++;
                }
                currentAccessoryIndex = (currentAccessoryIndex + accessories.length) % accessories.length;
                if (isIn(mouseX, mouseY, ACCESSORY_CHOOSE_BOX)) {
                    accessoryChosen[currentAccessoryIndex] = !accessoryChosen[currentAccessoryIndex];
                    StdDraw.pause(Frame.PAUSE*10);
                    if (accessoryChosen[0]) {
                        Arrays.fill(accessoryChosen, false);
                    }
                }
            }
        }
        if (currentWorldIndex!=WORLDS.length-1) {
            if (mousePressed) {
                if (isIn(mouseX, mouseY, RIGHT_ARROW_BOX[currentWorldIndex])) {
                    currentWorldIndex++;
                    currentLevelIndex = 1;
                }
            }
        }

        currentSkinIndex = (currentSkinIndex + skins.length) % skins.length;
        currentLevelIndex = ((currentLevelIndex - 1) % 12 + 12) % 12 + 1;

    }

    //----------------------------------------------------------------------------------------------------------

    public void draw() {
        StdDraw.clear();

        GameMap currentWorld = WORLDS[currentWorldIndex];
        Player currentSkin = skins[currentSkinIndex];
        Accessory currentAccessory = accessories[currentAccessoryIndex];

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
            for (int i = 0; i< accessoryChosen.length; i++) {
                if (accessoryChosen[i]) {
                    accessories[i].drawBig(multiplier);
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
            StdDraw.text(ACCESSORY_LEFT_COORDINATES[0], ACCESSORY_LEFT_COORDINATES[1], "<");
            StdDraw.text(ACCESSORY_RIGHT_COORDINATES[0], ACCESSORY_RIGHT_COORDINATES[1], ">");
            if (accessoryChosen[currentAccessoryIndex]) {
                StdDraw.text(ACCESSORY_CHOOSE_COORDINATES[0], ACCESSORY_CHOOSE_COORDINATES[1], "✅");
            } else {
                StdDraw.text(ACCESSORY_CHOOSE_COORDINATES[0], ACCESSORY_CHOOSE_COORDINATES[1], "❎");
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
        for (int i = 0; i< LEVEL_BOX_COORDINATES.length; i++) {
            double[] currentButton = LEVEL_BOX_COORDINATES[i];
            StdDraw.setPenColor();
            StdDraw.square(currentButton[0], currentButton[1], 0.5);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(currentButton[0]+ 0.5 /2, currentButton[1]- 0.5 /2, ""+(i+1));
            if (i+1 == currentLevelIndex) {
                StdDraw.square(currentButton[0], currentButton[1], 0.5);
            }
        }
    }

}

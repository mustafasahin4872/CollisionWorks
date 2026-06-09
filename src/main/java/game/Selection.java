package game;

import helpers.MapType;
import lib.StdDraw;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;

import java.util.ArrayList;

import game.InputHandler.MouseData;
import game.InputHandler.ArrowData;
import game.GameState.STATE;

public class Selection {

    private final GameState gameState;
    private final InputHandler inputHandler;

    private final SelectionScreen selectionScreen;

    public static final GameMap[] WORLDS = {
        new GameMap(new GameState(1, -1), MapType.SELECTION),
        new GameMap(new GameState(1, 0), MapType.SELECTION),
        new GameMap(new GameState(2, 0), MapType.SELECTION),
        new GameMap(new GameState(3, 0), MapType.SELECTION)
    };

    public static final Player[] skins = {
        new Player.RegularPlayer(),
        new Player.AnimatedPlayer("Mike", 6),
        new Player.RegularPlayer("Zahit")
    };

    public static final Accessory[] accessories = {
        null,
        new Accessory.Hat("fedora"),
        new Accessory.Tie("tie"),
        new Accessory.Headpiece("coquette"),
        new Accessory.Necklace("dollar"),
        new Accessory.Necklace("sorcerer"),
        new Accessory.Pin("star"),
        new Accessory.Pin("sheriff")
    };

    // ----------------------------------------------------------------------------------------------------------

    public Selection(InputHandler inputHandler, GameState gameState) {
        this.inputHandler = inputHandler;
        this.gameState = gameState;

        selectionScreen = new SelectionScreen(gameState);
    }

    // ----------------------------------------------------------------------------------------------------------

    public void selectionLoop() {

        for (Player skin : skins) {
            skin.setSpawnPoint(WORLDS[0].getSpawnPoint());
            skin.restart();
        }

        for (Accessory accessory : accessories) {
            if (accessory != null)
                accessory.update();
        }

        while (gameState.getState() == STATE.SELECTION || gameState.getState() == STATE.SHOP
                || gameState.getState() == STATE.GAME) {
            inputHandler.takeInput();
            MouseData mouseData = inputHandler.getMouseData();
            ArrowData arrowData = inputHandler.getArrowData();
            draw();
            selectionScreen.processInput(mouseData, arrowData);

        }

        Player player = skins[selectionScreen.getCurrentSkinIndex()];

        wireAccessoriesWithPlayer(player);
        player.setWorldIndex(selectionScreen.getCurrentWorldIndex());

        gameState.worldIndex = selectionScreen.getCurrentWorldIndex();
        gameState.levelIndex = selectionScreen.getCurrentLevelIndex();
        gameState.player = player;

    }

    private void wireAccessoriesWithPlayer(Player player) {

        ArrayList<Accessory> selectedAccessories = new ArrayList<>();
        boolean[] accessoryChosen = selectionScreen.getAccessoryChosen();

        for (int i = 0; i < accessoryChosen.length; i++) {
            if (accessoryChosen[i]) {
                Accessory accessory = accessories[i];
                accessory.setPlayer(player);
                selectedAccessories.add(accessories[i]);
            }
        }
        player.setAccessories(selectedAccessories.toArray(new Accessory[0]));
    }

    // ----------------------------------------------------------------------------------------------------------

    public void draw() {
        StdDraw.clear();
        StdDraw.setXscale(0, Frame.X_SCALE);
        StdDraw.setYscale(Frame.Y_SCALE, 0);

        int currentWorldIndex = (gameState.getState() == STATE.GAME) ? selectionScreen.getCurrentWorldIndex() : 0;
        int currentSkinIndex = selectionScreen.getCurrentSkinIndex();
        int currentAccessoryIndex = selectionScreen.getCurrentAccessoryIndex();

        GameMap currentWorld = WORLDS[currentWorldIndex];
        Player currentSkin = skins[currentSkinIndex];
        Accessory currentAccessory = accessories[currentAccessoryIndex];
        boolean[] accessoryChosen = selectionScreen.getAccessoryChosen();

        currentWorld.draw();
        if (currentWorldIndex == 0) {
            double multiplier = 2.5;
            currentSkin.drawBig(multiplier);
            if (currentAccessory != null) {
                currentAccessory.setPlayer(currentSkin);
                currentAccessory.drawBig(multiplier);
            }
            for (int i = 0; i < accessoryChosen.length; i++) {
                if (accessoryChosen[i]) {
                    accessories[i].drawBig(multiplier);
                }
            }
        }

        selectionScreen.draw();

        StdDraw.show();
        StdDraw.pause(8 * Frame.PAUSE); // allows time between clicks
    }


}

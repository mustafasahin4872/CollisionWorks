package game;

import helpers.MapType;
import mapobjects.category.GridObject;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;
import java.awt.*;

public final class SelectionConstants {

    public static final GameMap[] WORLDS = {
        new GameMap(new GameState(1, -1), MapType.SELECTION),
        new GameMap(new GameState(1, 0), MapType.SELECTION),
        new GameMap(new GameState(2, 0), MapType.SELECTION),
        new GameMap(new GameState(3, 0), MapType.SELECTION),
        // new GameMap(new GameState(4, 0), MapType.SELECTION) // world4 disabled
    };

    public static final Color[] WORLD_COLORS = {
        new Color(1,1,1),
        new Color(119, 14, 155, 255),
        new Color(25, 127, 180),
        new Color(233, 116, 49),
        new Color(199, 193, 189)
    };

    public static final String[] WORLD_NAMES = {
        null, "THE SPRING FESTIVAL", "INTO THE ICE CAVE", "TO THE TOP OF THE VOLCANO", // "CRYSTAL PALACE"
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


    public static final int
        X_TILE = (int)(Frame.X_SCALE / GridObject.TILE_SIDE),
        Y_TILE = (int)(Frame.Y_SCALE / GridObject.TILE_SIDE);

    //centers of buttons
    public static final double[][]
        LEFT_ARROW_COORDINATES = {null, {1, Y_TILE - 5}, {1, Y_TILE - 5},
                                {1, Y_TILE - 5}, {1, Y_TILE - 8}},
        RIGHT_ARROW_COORDINATES = {{X_TILE - 1, Y_TILE - 8}, {X_TILE - 1, Y_TILE - 8},
                                   {X_TILE - 1, Y_TILE - 7}, {X_TILE - 1, Y_TILE - 4}, null};

    public static final double[]
        ACCESSORY_LEFT_COORDINATES = new double[]{4+0.5, Y_TILE-6.5-0.5},
        ACCESSORY_RIGHT_COORDINATES = new double[]{X_TILE-2-4-0.5 , Y_TILE-6.5-0.5},
        ACCESSORY_CHOOSE_COORDINATES = new double[]{(ACCESSORY_LEFT_COORDINATES[0]+ ACCESSORY_RIGHT_COORDINATES[0])/2, Y_TILE-7.5-0.5},
        SHOP_COORDINATES = new double[]{8, 11};


    public static final double[][]
        LEFT_ARROW_BOX = new double[5][4], RIGHT_ARROW_BOX = new double[5][4],
        LEVEL_BOX_COORDINATES = new double[12][2];
    public static final double[]
        ACCESSORY_LEFT_BOX = new double[4], ACCESSORY_RIGHT_BOX = new double[4],
        ACCESSORY_CHOOSE_BOX = new double[4], SHOP_BOX = new double[4];


    static {
        for (int i = 0; i<3; i++) {
            for (int j = 0; j<4; j++) {
                LEVEL_BOX_COORDINATES[i*4+j] = new double[]{
                    0.5 + 4 + j*3,
                    Y_TILE - 0.5 - 4 -2*i};
            }
        }
        fillBoxes(SHOP_COORDINATES, SHOP_BOX, 2);
    }

    static {
        for (int i = 0; i<5; i++) {
            if (LEFT_ARROW_COORDINATES[i]!=null) {
                fillBoxes(LEFT_ARROW_COORDINATES[i], LEFT_ARROW_BOX[i], 1);
            }
            if (RIGHT_ARROW_COORDINATES[i] != null) {
                fillBoxes(RIGHT_ARROW_COORDINATES[i], RIGHT_ARROW_BOX[i], 1);
            }
        }

        fillBoxes(ACCESSORY_LEFT_COORDINATES, ACCESSORY_LEFT_BOX, 0.5);
        fillBoxes(ACCESSORY_RIGHT_COORDINATES, ACCESSORY_RIGHT_BOX, 0.5);
        fillBoxes(ACCESSORY_CHOOSE_COORDINATES, ACCESSORY_CHOOSE_BOX, 0.5);

    }

    private static void fillBoxes(double[] coordinates, double[] fillBox, double side) {

        fillBox[0] = coordinates[0] - side;
        fillBox[1] = coordinates[1] - side;
        fillBox[2] = coordinates[0] + side;
        fillBox[3] = coordinates[1] + side;

    }

}

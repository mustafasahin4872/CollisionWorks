package game;

import mapobjects.category.GridObject;

import java.awt.*;

public final class SelectionConstants {


    public static final Color[] WORLD_COLORS = {
        new Color(1,1,1),
        new Color(119, 14, 155, 255),
        new Color(25, 127, 180),
        new Color(233, 116, 49),
        new Color(199, 193, 189)
    };

    public static final String[] WORLD_NAMES = {
        null, "THE SPRING FESTIVAL", "INTO THE ICE CAVE", "TO THE TOP OF THE VOLCANO", "CRYSTAL PALACE"
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

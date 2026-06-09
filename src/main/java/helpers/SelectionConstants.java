package helpers;

import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;
import mapobjects.component.Box;
import java.awt.*;
import static mapobjects.category.GridObject.TILE_SIDE;
import static game.Frame.X_SCALE;
import static game.Frame.Y_SCALE;

// old, unused class
public final class SelectionConstants {

    public static final Color[] WORLD_COLORS = {
            new Color(1, 1, 1),
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

    public static final int X_TILE = (int) (X_SCALE / TILE_SIDE),
            Y_TILE = (int) (Y_SCALE / TILE_SIDE);

    public static final double PLAYER_NAME_Y_OFFSET = 2.15 * TILE_SIDE;
    public static final double WORLD_NAME_Y_OFFSET = 2.5 * TILE_SIDE;

    // box layout constants

    public static final double ACCESSORY_BOX_SIZE = 1.0 * TILE_SIDE;
    public static final double ACCESSORY_Y_OFFSET = 7.0 * TILE_SIDE;
    public static final double ACCESSORY_LEFT_X = 4.5 * TILE_SIDE;
    public static final double ACCESSORY_RIGHT_X_OFFSET = 6.5 * TILE_SIDE;
    public static final double ACCESSORY_CHOOSE_Y_OFFSET = 8.0 * TILE_SIDE;

    public static final Box ACCESSORY_LEFT_BOX = new Box(ACCESSORY_LEFT_X, ACCESSORY_Y_OFFSET,
            ACCESSORY_BOX_SIZE, ACCESSORY_BOX_SIZE);
    public static final Box ACCESSORY_RIGHT_BOX = new Box(X_SCALE - ACCESSORY_RIGHT_X_OFFSET,
            ACCESSORY_Y_OFFSET, ACCESSORY_BOX_SIZE, ACCESSORY_BOX_SIZE);
    public static final Box ACCESSORY_CHOOSE_BOX = new Box(
            (ACCESSORY_LEFT_X + (X_SCALE - ACCESSORY_RIGHT_X_OFFSET)) / 2,
            ACCESSORY_CHOOSE_Y_OFFSET, ACCESSORY_BOX_SIZE, ACCESSORY_BOX_SIZE);

    public static final Box SKIN_LEFT_BOX = new Box(ACCESSORY_LEFT_X, PLAYER_NAME_Y_OFFSET,
            ACCESSORY_BOX_SIZE, ACCESSORY_BOX_SIZE);
    public static final Box SKIN_RIGHT_BOX = new Box(X_SCALE - ACCESSORY_RIGHT_X_OFFSET, PLAYER_NAME_Y_OFFSET,
            ACCESSORY_BOX_SIZE, ACCESSORY_BOX_SIZE);

    public static final double SHOP_BOX_X = 8 * TILE_SIDE;
    public static final double SHOP_BOX_Y = 11 * TILE_SIDE;
    public static final double SHOP_BOX_SIZE = 4.0 * TILE_SIDE;

    public static final Box SHOP_BOX = new Box(SHOP_BOX_X, SHOP_BOX_Y, SHOP_BOX_SIZE, SHOP_BOX_SIZE);

    public static final double ARROW_BOX_SIZE = 2.0 * TILE_SIDE;

    public static final Box[] LEFT_ARROW_BOX = {
            null,
            new Box(1 * TILE_SIDE, 5 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(1 * TILE_SIDE, 5 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(1 * TILE_SIDE, 5 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(1 * TILE_SIDE, 8 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE)
    };

    public static final Box[] RIGHT_ARROW_BOX = {
            new Box(X_SCALE - 1 * TILE_SIDE, 8 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(X_SCALE - 1 * TILE_SIDE, 8 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(X_SCALE - 1 * TILE_SIDE, 7 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            new Box(X_SCALE - 1 * TILE_SIDE, 4 * TILE_SIDE, ARROW_BOX_SIZE, ARROW_BOX_SIZE),
            null
    };

    public static final double LEVEL_BOX_SIZE = 1.0 * TILE_SIDE;
    public static final double LEVEL_BOX_START_X = 4.5 * TILE_SIDE;
    public static final double LEVEL_BOX_X_GAP = 3.0 * TILE_SIDE;
    public static final double LEVEL_BOX_START_Y_OFFSET = 4.5 * TILE_SIDE;
    public static final double LEVEL_BOX_Y_GAP = 2.0 * TILE_SIDE;

    public static final Box[] LEVEL_BOXES = new Box[12];

    static {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                LEVEL_BOXES[i * 4 + j] = new Box(
                        LEVEL_BOX_START_X + j * LEVEL_BOX_X_GAP,
                        LEVEL_BOX_START_Y_OFFSET + i * LEVEL_BOX_Y_GAP,
                        LEVEL_BOX_SIZE, LEVEL_BOX_SIZE);
            }
        }
    }

}

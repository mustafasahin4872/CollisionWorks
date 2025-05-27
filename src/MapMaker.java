import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map;

//MapMaker class initializes Tile, Button, Chest, Door objects and holds them
//upon taking the world and level indexes from constructor, it reads the corresponding map file
//in the file, every tile is coded with 3 letters, and the B,C,D objects are coded with special codes as well
public class MapMaker {

    private final int worldIndex;
    private final int levelIndex;
    private final File mapFile;

    private final Tile[] tiles = new Tile[Tile.X_TILE * Tile.Y_TILE];
    private final Set<Button> buttons = new HashSet<>();
    private final Map<String, Button> buttonMap = new HashMap<>();
    private final Set<Chest> chests = new HashSet<>();
    private final Set<Door> doors = new HashSet<>();
    private final Set<Coin> coins = new HashSet<>();

    private static final Set<String> PASSABLE_CODES = new HashSet<>(Set.of("   ", "XXX", "MMM", "III", "DDD", "HHH"));
    private static final Set<String> IMPASSABLE_CODES = new HashSet<>(Set.of("WWW", "RRR", "###"));
    private static final Set<String> SPECIAL_CODES = new HashSet<>(Set.of("BBB", "BLB", "DVD", "DHD"));
    private static final Set<String> PASSABLE_COIN = new HashSet<>(Set.of(
            " O ", "XOX", "MOM", "IOI", "DOD", "HOH",
            " T ", "XTX", "MTM", "ITI", "DTD", "HTH",
            " B ", "XBX", "MBM", "IBI", "DBD", "HBH"
    ));

/*
WWW for walls, RRR for rivers, ### is for map edges
space and XXX is for space tiles (X is for visualization), MMM mud, III ice, DDD damage, HHH heal.

buttons start with B, chests C, doors D. there are special codes and a generic code for these
generic code is as X37: X being the object indicator(B, C, D) and 37 is the line number in which details reside
details are in format: on top tile; subtype; extra; extra; ... . examples:
C37: III; G; F, S is a gold chest on top of a ice tile with buffs fast and small.
D37: MMM; H; 12:12, 14:19; 5 is a horizontal door with a length of 5 tiles on a mud tile with 2 buttons in given coordinates

these are the quick codes for BCD(buttons, chests and doors):
BBB is big button on grass, BLB is small button on grass
CTB(chest-type-buff): CWB, CSB, CGB are for single buff chests on grass
DVD is default vertical door on grass without buttons, DHD is default horizontal

O is the symbol for single coins, T for triple and B for bag coins.
for passable tile codes, coins on top of them are displayed as: " O ", it must be in the middle.
for special tile codes, the identifier O in the first argument is enough: MOM; V; ...
*/


    //CONSTRUCTOR

    public MapMaker(int worldIndex, int levelIndex) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        mapFile = new File(("misc/maps/%d%d.txt").formatted(worldIndex, levelIndex));
    }


    //GETTERS

    public Tile[] getTiles() {
        return tiles;
    }

    public Set<Button> getButtons() {
        return buttons;
    }

    public Set<Chest> getChests() {
        return chests;
    }

    public Set<Door> getDoors() {
        return doors;
    }

    public Set<Coin> getCoins() {
        return coins;
    }

    //MAIN METHOD

    public void mapMaker() {
        String[][] mapData = new String[Tile.Y_TILE][Tile.X_TILE];
        ArrayList<String> objectDetails = new ArrayList<>();
        boolean[][] approachability = new boolean[Tile.Y_TILE][Tile.X_TILE];

        // fill the mapData and objectDetails storages
        extractMapData(mapData, objectDetails);

        // assign approachability to tiles
        extractApproachability(mapData, approachability);

        // initialize the tiles, chests, buttons and doors parsing through mapData.
        initializeObjects(mapData, approachability, objectDetails);
        // tiles, chests and doors are now ready to be processed
    }


    //INITIALIZERS

    //creates tile, button, chest, door objects by calling all other initializers
    private void initializeObjects(String[][] mapData, boolean[][] approachability, ArrayList<String> objectDetails) {

        //hold the D37 codes here with their locations to later wire them to buttons
        Map<String, int[]> doorsToWire = new HashMap<>();

        for (int y = 0; y < Tile.Y_TILE; y++) {
            for (int x = 0; x < Tile.X_TILE; x++) {
                String tileCode = mapData[y][x];
                boolean isApproachable = approachability[y][x];
                Tile tile;

                //initialize basic tiles
                if (PASSABLE_CODES.contains(tileCode)) {
                    tile = initializePassableTile(tileCode, x, y);
                } else if (IMPASSABLE_CODES.contains(tileCode)) {
                    tile = initializeImpassableTile(tileCode, x, y, isApproachable);
                } else if (PASSABLE_COIN.contains(tileCode)) {
                    tile = initializePassableCoinTile(tileCode, x, y);
                } else if (SPECIAL_CODES.contains(tileCode)) {
                    tile = initializeSpecialTile(tileCode, x, y); //initialize special object on top of space tile
                } else { //2 possibilities: X37 or CTB
                    char type = tileCode.charAt(0); // C, T or B
                    String next = tileCode.substring(1,3); // either a number or TB of CTB
                    try { // X37 types, try to convert String next to int
                         String[] items = objectDetails.get(Integer.parseInt(next) - 37).split("; ");
                         if (PASSABLE_CODES.contains(items[0])) {
                             tile = initializePassableTile(items[0], x, y);
                         } else { // if the object holds a coin on top
                             tile = initializePassableCoinTile(items[0], x, y);
                         }
                         switch (type) {
                             case 'B' -> {
                                 Button button = new Button(x+1, y+1, items[1].equals("L"));
                                 buttonMap.put("%d:%d".formatted(x+1, y+1), button);
                                 buttons.add(button);
                             }
                             case 'C' -> {
                                 String[] buffs = items[2].split(", ");
                                 initializeChest(items[1], x, y, buffs);
                             }
                             case 'D' -> doorsToWire.put(tileCode, new int[]{x, y});
                             default -> System.out.println("default message for X37, an error occurred.");
                         }
                    } catch (NumberFormatException e) { // CTB types, failed to convert to int
                        tile = initializePassableTile("   ", x, y);
                        char[] buffs = {next.charAt(1)};
                        initializeChest(next.charAt(0), x, y, buffs);
                    }
                }

                tiles[y * Tile.X_TILE + x] = tile;
            }
        }

        initializeD37(objectDetails, doorsToWire);

    }

    private void initializeD37(ArrayList<String> objectDetails, Map<String, int[]> doorsToWire) {
        //wire doors to buttons:
        for (Map.Entry<String, int[]> pair : doorsToWire.entrySet()) {
            String next = pair.getKey().substring(1,3);
            String[] items = objectDetails.get(Integer.parseInt(next) - 37).split("; ");
//items[0] is the tile on top, already initialized. items[1] is the type of door.
//items[2] is the door coordinates as x1:y1, x2:y2, ... and if there exists, items[3] is the length of the door
//careful! the button coordinates entered in txt file has starting index 1, while mapData has 0!

            //hold the buttons to wire to the door in here
            ArrayList<Button> buttonsToWire = new ArrayList<>();

            for (String s : items[2].split(", ")) {
                if (s.isEmpty()) continue;
                buttonsToWire.add(buttonMap.get(s));
            }

            // x-y coordinates in mapData
            int x = pair.getValue()[0];
            int y = pair.getValue()[1];

            int l = 4; //length of door is 4 by default
            if (items.length==4) {l = Integer.parseInt(items[3]);} //change length if specified

            //initialize door
            switch (items[1]) {
                case "V" -> doors.add(new Door.VerticalDoor(x+1, y+1, l, buttonsToWire.toArray(new Button[0])));
                case "H" -> doors.add(new Door.HorizontalDoor(x+1, y+1, l, buttonsToWire.toArray(new Button[0])));
                default -> System.out.println("default message for initializing door, an error occurred");
            }

        }
    }

    private Tile initializePassableTile(String tileCode, int x, int y) {
        return switch (tileCode) {
            case "   ", "XXX" -> new Tile.SpaceTile(x + 1, y + 1, worldIndex);
            case "MMM" -> new Tile.MudTile(x + 1, y + 1);
            case "III" -> new Tile.IceTile(x + 1, y + 1);
            case "DDD" -> new Tile.DamageTile(x + 1, y + 1, worldIndex);
            case "HHH" -> new Tile.HealTile(x + 1, y + 1, worldIndex);
            default -> {
                System.out.println("default message for passable tiles, an error occurred");
                yield new Tile.SpaceTile(x + 1, y + 1, worldIndex);
            }
        };
    }

    private Tile initializeImpassableTile(String tileCode, int x, int y, boolean isApproachable) {
        return switch (tileCode) {
            case "RRR" -> new Tile.RiverTile(x + 1, y + 1, isApproachable, worldIndex);
            case "WWW" -> new Tile.WallTile(x + 1, y + 1, isApproachable, worldIndex);
            default -> {
                System.out.println("default message for impassable tiles, an error occurred");
                yield new Tile.WallTile(x + 1, y + 1, isApproachable, worldIndex);
            }
        };
    }

    private Tile initializePassableCoinTile(String tileCode, int x, int y) {
        Coin coin = switch (tileCode.charAt(1)) {
            case 'O' -> new Coin.SingleCoin(x+1, y+1);
            case 'T' -> new Coin.TripleCoin(x+1, y+1);
            case 'B' -> new Coin.CoinBag(x+1, y+1);
            default -> {
                System.out.println("default message for passable coin tiles, an error occurred1");
                yield new Coin.SingleCoin(x+1, y+1);
            }
        };
        coins.add(coin);
        return switch (tileCode.charAt(0)) {
            case ' ', 'X' -> new Tile.SpaceTile(x + 1, y + 1, worldIndex);
            case 'M' -> new Tile.MudTile(x + 1, y + 1);
            case 'I' -> new Tile.IceTile(x + 1, y + 1);
            case 'D' -> new Tile.DamageTile(x + 1, y + 1, worldIndex);
            case 'H' -> new Tile.HealTile(x + 1, y + 1, worldIndex);
            default -> {
                System.out.println("default message for passable coin tiles, an error occurred2");
                yield new Tile.SpaceTile(x + 1, y + 1, worldIndex);
            }
        };
    }

    private Tile initializeSpecialTile(String tileCode, int x, int y) {
        Tile tile;
        tile = new Tile.SpaceTile(x + 1, y + 1, worldIndex);
        switch (tileCode) {
            case "BBB" -> {
                Button button = new Button(x + 1, y + 1);
                buttonMap.put("%d:%d".formatted(x+1, y+1), button);
                buttons.add(button);
            }
            case "BLB" -> {
                Button button = new Button(x + 1, y + 1, true);
                buttonMap.put("%d:%d".formatted(x+1, y+1), button);
                buttons.add(button);
            }
            case "DVD" -> doors.add(new Door.VerticalDoor(x+1, y+1));
            case "DHD" -> doors.add(new Door.HorizontalDoor(x+1, y+1));
            default -> System.out.println("default message for special tiles, an error occurred");
        }
        return tile;
    }

    private void initializeChest(String type, int x, int y, String[] buffs) {
        char[] charBuffs = toCharArray(buffs);
        switch (type) {
            case "W" -> chests.add(new Chest.WoodenChest(x+1, y+1, charBuffs));
            case "S" -> chests.add(new Chest.SilverChest(x+1, y+1, charBuffs));
            case "G" -> chests.add(new Chest.GoldenChest(x+1, y+1, charBuffs));
            default -> System.out.println("default message for initializing chest, an error occurred");
        }
    }

    private void initializeChest(char type, int x, int y, char[] buffs) {
        switch (type) {
            case 'W' -> chests.add(new Chest.WoodenChest(x+1, y+1, buffs));
            case 'S' -> chests.add(new Chest.SilverChest(x+1, y+1, buffs));
            case 'G' -> chests.add(new Chest.GoldenChest(x+1, y+1, buffs));
            default -> System.out.println("default message for initializing chest, an error occurred");
        }
    }


    //EXTRACT

    private void extractApproachability(String[][] mapData, boolean[][] approachability) {
        for (int y = 0; y < Tile.Y_TILE; y++) {
            for (int x = 0; x < Tile.X_TILE; x++) {
                String tileCode = mapData[y][x];

                if (isPassable(tileCode)) {
                    approachability[y][x] = true;
                } else {
                    String up = y > 0 ? mapData[y - 1][x] : "###";
                    String down = y < Tile.Y_TILE - 1 ? mapData[y + 1][x] : "###";
                    String left = x > 0 ? mapData[y][x - 1] : "###";
                    String right = x < Tile.X_TILE - 1 ? mapData[y][x + 1] : "###";

                    approachability[y][x] = isApproachable(up, down, left, right);
                }
            }
        }
    }

    private void extractMapData(String[][] mapData, ArrayList<String> objectDetails) {
        try (Scanner scanner = new Scanner(mapFile)) {
            int row = 0;
            while (scanner.hasNextLine() && row < Tile.Y_TILE) {
                String line = scanner.nextLine();
                for (int col = 0; col < Tile.X_TILE; col++) {
                    if (col*3 < line.length()) {
                        mapData[row][col] = line.substring(col*3, (col+1)*3);
                    } else {
                        mapData[row][col] = "   "; // Default to empty/passable if line is short
                    }
                }
                row++;
            }
            while (scanner.hasNextLine()) {
                objectDetails.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File couldn't be opened: " + mapFile.getAbsolutePath());
        }
    }


    //MINI HELPERS

    private boolean isApproachable(String charUp, String charDown, String charLeft, String charRight) {
        return isPassable(charUp) || isPassable(charDown) || isPassable(charLeft) || isPassable(charRight);
    }

    private boolean isPassable(String tileCode) {
        return !IMPASSABLE_CODES.contains(tileCode);
    }

    private static char[] toCharArray(String[] arr) {
        char[] charArray = new char[arr.length];
        for (int i = 0; i < arr.length; i++) {
            charArray[i] = arr[i].charAt(0);
        }
        return charArray;
    }
}

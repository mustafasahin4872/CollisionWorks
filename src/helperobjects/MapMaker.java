package helperobjects;

import mapobjects.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map;

public class MapMaker {

    private final int worldIndex, levelIndex;
    private final File mapFile;
    private final int xTile, yTile;

    private final Tile[] tiles;
    private final Set<Button> buttons = new HashSet<>();
    private final Set<Chest> chests = new HashSet<>();
    private final Set<Door> doors = new HashSet<>();
    private final Set<Sign> signs = new HashSet<>();
    private final Set<Coin> coins = new HashSet<>();
    private final Set<Mine> mines = new HashSet<>();
    private final Set<Mortar> mortars = new HashSet<>();
    private final Set<Shooter> shooters = new HashSet<>();
    private final Set<Point.WinPoint> winPoints = new HashSet<>();
    private Point.CheckPoint[] checkPoints = {new Point.SpawnPoint(0,0,0),
                                              new Point.CheckPoint(0,0,0,1),
                                              new Point.CheckPoint(0,0,0,2),
                                              new Point.CheckPoint(0,0,0,3),
                                              new Point.CheckPoint(0,0,0,4),
                                              };

    private static final Set<String> IMPASSABLE_CODES = new HashSet<>(Set.of("XXX", "###", "%%%"));
    private static final Set<Character>
            BASIC_CHARACTERS = new HashSet<>(Set.of( //tile symbols
                    ' ', '_', // space
                    'X', '%', '#', // wall/boundary/river
                    'w', // mud
                    '-', '+', // damage/heal
                    '?' // special
            )), //special characters must not clash with basic characters
            SPECIAL_CHARACTERS = new HashSet<>(Set.of(

                    '|', '—', // alignment information
                    '^', 'v', '<', '>', // direction information

                    'o', '*', '$', // single/triple/bag coins
                    ':', '.', // big/little button
                    '@', '%', // mine and mortar

                    '0', '1', '2', '3', '4', // spawn/check points
                    '5', '6', '7', '8', '9', // win points
                    '~', '≈', '=', // wooden/silver/golden chest

                    'C', 'D', 'E', 'S', //chest, door, enemy, sign
                    'a', 'b', 'c', 'd' // subtype symbols for all objects, 4 kinds
            ));


/*
MapMaker class initializes tiles and other objects and holds them
upon taking the world and level indexes from constructor, it reads the corresponding map data file
in the file, every 3 letters are 1 tileCode, and tileCodes are used to initialize objects

every tile code must have a tile below it. if the tile code is 3 of the same characters, it represents a tile.
if it consists of different characters, it initializes an object. there are multiple formats of tileCodes for object initialization

general object initializer A37: A represents the class, 37 is the line in which details are written
details contain multiple information, the information is divided by ";",
if the information consists of multiple elements, they are divided by ","
the first information is always on tile type. the following information are unique to each class.
the objects that can be initialized with A37 are: chests(C), doors(D), signs(S)
C37: !!!; =; F, S // on tile type; chest type; buffs
D37: vvv; —; 12:12, 14:19; 5 // on tile type; alignment; coordinates of buttons to wire
S37: ###; '; text 1, text 2 // on tile type; single/multiple line indicator; messages or message lines

quick code formats:
points, coins, buttons, mines, mortars, non-moving enemies are always called with quick codes.
1) ABA where A is the tile under, B is the object type
coins, buttons, mines, mortars, non-moving enemies are always called with this.
doors and signs cannot be initialized this way, chests initialized cant have buffs.

2) ABC where B is the object type, C is one property of that object
chests can have 1 buff, doors have alignment indicator,
points can have the indicator B for big displays, special to the selection screen

*/

    //CONSTRUCTOR

    public MapMaker(int worldIndex, int levelIndex, int xTile, int yTile) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.xTile = xTile;
        this.yTile = yTile;
        tiles = new Tile[xTile*yTile];
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

    public Set<Sign> getSigns() {
        return signs;
    }

    public Set<Mine> getMines() {
        return mines;
    }

    public Set<Mortar> getMortars() {
        return mortars;
    }

    public Set<Shooter> getShooters() {
        return shooters;
    }

    public Set<Point.WinPoint> getWinPoints() {
        return winPoints;
    }

    public Point.CheckPoint[] getCheckPoints() {
        return checkPoints;
    }

    //MAIN METHOD

    public void mapMaker() {
        String[][] mapData = new String[yTile][xTile];
        ArrayList<String> objectDetails = new ArrayList<>();
        boolean[][] approachability = new boolean[yTile][xTile];

        // fill the mapData and objectDetails storages
        extractMapData(mapData, objectDetails);

        // assign approachability to tiles
        extractApproachability(mapData, approachability);

        // initialize all objects parsing through mapData and objectDetails
        initializeObjects(mapData, approachability, objectDetails);

        // all objects are ready to be processed. use getters to access them
    }


    //INITIALIZERS

    //creates all objects
    private void initializeObjects(String[][] mapData, boolean[][] approachability, ArrayList<String> objectDetails) {

        // hold the D37 codes here with their locations to later wire them to buttons
        Map<Integer, int[]> doorsToWire = new HashMap<>();
        Map<String, Button> buttonMap = new HashMap<>();

        for (int y = 1; y <= yTile; y++) {
            for (int x = 1; x <= xTile; x++) {

                boolean isApproachable = approachability[y - 1][x - 1];
                String tileCode = mapData[y - 1][x - 1];
                Tile tile;
                char char0 = tileCode.charAt(0);

                if (BASIC_CHARACTERS.contains(char0)) { // quick codes

                    char char1 = tileCode.charAt(1), char2 = tileCode.charAt(2);
                    tile = initializeBasicTile(char0, x, y, isApproachable);

                    switch (char1) {
                        case 'o' -> coins.add(new Coin.SingleCoin(worldIndex, x, y));
                        case '*' -> coins.add(new Coin.TripleCoin(worldIndex, x, y));
                        case '$' -> coins.add(new Coin.CoinBag(worldIndex, x, y));
                        case '@' -> mines.add(new Mine(worldIndex, x, y));
                        case '%' -> mortars.add(new Mortar(worldIndex, x, y, tiles, xTile));
                        case ':' -> {
                            Button button = new Button.BigButton(worldIndex, x, y);
                            buttonMap.put("%d:%d".formatted(x, y), button);
                            buttons.add(button);
                        }
                        case '.' -> {
                            Button button = new Button.LittleButton(worldIndex, x, y);
                            buttonMap.put("%d:%d".formatted(x, y), button);
                            buttons.add(button);
                        }
                        case '0' -> checkPoints[0] = new Point.SpawnPoint(worldIndex, x, y, char2 == 'B');
                        case '1', '2', '3', '4' -> checkPoints[char1 - '0'] = new Point.CheckPoint(worldIndex, x, y, char1 - '0', char2 == 'B');
                        case '5', '6', '7', '8', '9' -> winPoints.add(new Point.WinPoint(worldIndex, x, y, char1 - '5', char2 == 'B'));
                        case '~' -> chests.add(new Chest.WoodenChest(worldIndex, x, y, new char[]{char2}));
                        case '≈' -> chests.add(new Chest.SilverChest(worldIndex, x, y, new char[]{char2}));
                        case '=' -> chests.add(new Chest.GoldenChest(worldIndex, x, y, new char[]{char2}));
                        case 'D' -> {
                            Alignment alignment = (char2 == '|') ? Alignment.V : Alignment.H;
                            doors.add(new Door(worldIndex, x, y, alignment));
                        }
                    }

                } else { // A37 initializer, there can be coins on top!

                    int lineIndex = Integer.parseInt(tileCode.substring(1, 3)) - 37;
                    String[] details = objectDetails.get(lineIndex).split("; ");

                    String onTileCode = details[0];

                    tile = initializeBasicTile(onTileCode.charAt(0), x, y, isApproachable);

                    switch (onTileCode.charAt(1)) { // check if there is a coin on top
                        case 'o' -> coins.add(new Coin.SingleCoin(worldIndex, x, y));
                        case '*' -> coins.add(new Coin.TripleCoin(worldIndex, x, y));
                        case '$' -> coins.add(new Coin.CoinBag(worldIndex, x, y));
                    }

                    switch (char0) {
                        case 'C' -> {
                            String[] buffs = details[2].split(", ");
                            switch (details[1].charAt(0)) {
                                case '~' -> chests.add(new Chest.WoodenChest(worldIndex, x, y, toCharArray(buffs)));
                                case '≈' -> chests.add(new Chest.SilverChest(worldIndex, x, y, toCharArray(buffs)));
                                case '=' -> chests.add(new Chest.GoldenChest(worldIndex, x, y, toCharArray(buffs)));
                            }
                        }
                        case 'D' -> doorsToWire.put(lineIndex, new int[]{x - 1, y - 1});
                        case 'S' -> {
                            String[] messages = details[2].split(", ");
                            Sign sign = switch (details[1].charAt(0)) {
                                case '\'' -> new Sign(worldIndex, x, y, messages);
                                case '"' -> {
                                    for (int i = 0; i < messages.length; i++) {
                                        messages[i] = objectDetails.get(Integer.parseInt(messages[i]) - 37);
                                    }
                                    yield new Sign(worldIndex, x, y, messages);
                                }
                                default -> {
                                    System.out.println("default message for sign initialization, an error occurred");
                                    yield new Sign(worldIndex, x, y, new String[0]);
                                }
                            };
                            signs.add(sign);
                        }
                        default -> System.out.println("default message for A37, an error occurred.");
                    }
                }

                tiles[(y - 1) * xTile + (x - 1)] = tile;
            }
        }

        wireDoorsToButtons(objectDetails, doorsToWire, buttonMap);
        setPrevToCheckPoints();
    }

    private void setPrevToCheckPoints() {
        for (int i = 0; i<5; i++) {
            Point.CheckPoint currentCheckPoint = checkPoints[i];
            if (i!=0) {
                currentCheckPoint.setPrev(checkPoints[i-1]);
            }
        }
    }

    private void wireDoorsToButtons(ArrayList<String> objectDetails, Map<Integer, int[]> doorsToWire, Map<String, Button> buttonMap) {
        //wire doors to buttons:
        for (Map.Entry<Integer, int[]> pair : doorsToWire.entrySet()) {
            String[] items = objectDetails.get(pair.getKey()).split("; ");
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
            switch (items[1].charAt(0)) {
                case '|' -> doors.add(new Door(worldIndex, x+1, y+1, Alignment.V, l, buttonsToWire.toArray(new Button[0])));
                case '—' -> doors.add(new Door(worldIndex, x+1, y+1, Alignment.H, l, buttonsToWire.toArray(new Button[0])));
                default -> System.out.println("default message for initializing door, an error occurred");
            }
        }
    }

    private Tile initializeBasicTile(char char0, int x, int y, boolean isApproachable) {
        return switch (char0) {
            case ' ', '_' -> new Tile.SpaceTile(worldIndex, x, y);
            case 'w' -> new Tile.SlowTile(worldIndex, x, y);
            case '!' -> new Tile.SpecialTile(worldIndex, x, y);
            case '-' -> new Tile.DamageTile(worldIndex, x, y);
            case '+' -> new Tile.HealTile(worldIndex, x, y);
            case 'X' -> new Tile.WallTile(worldIndex, x, y, isApproachable);
            case '#' -> new Tile.RiverTile(worldIndex, x, y, isApproachable);
            default -> {
                System.out.println("default message for basic tiles, an error occurred");
                yield new Tile.SpaceTile(x, y, worldIndex);
            }
        };
    }


    //EXTRACT

    private void extractApproachability(String[][] mapData, boolean[][] approachability) {
        for (int y = 0; y < yTile; y++) {
            for (int x = 0; x < xTile; x++) {
                String tileCode = mapData[y][x];

                if (isPassable(tileCode)) {
                    approachability[y][x] = true;
                } else {
                    String up = y > 0 ? mapData[y - 1][x] : "%%%";
                    String down = y < yTile - 1 ? mapData[y + 1][x] : "%%%";
                    String left = x > 0 ? mapData[y][x - 1] : "%%%";
                    String right = x < xTile - 1 ? mapData[y][x + 1] : "%%%";

                    approachability[y][x] = isApproachable(up, down, left, right);
                }
            }
        }
    }

    private void extractMapData(String[][] mapData, ArrayList<String> objectDetails) {
        try (Scanner scanner = new Scanner(mapFile)) {
            int row = 0;
            while (scanner.hasNextLine() && row < yTile) {
                String line = scanner.nextLine();
                for (int col = 0; col < xTile; col++) {
                    if (col*3 < line.length()) {
                        mapData[row][col] = line.substring(col*3, (col+1)*3);
                    } else {
                        mapData[row][col] = "   ";
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

    private char[] toCharArray(String[] stringArray) {

        int l = stringArray.length;
        char[] returnArray = new char[l];

        for (int i = 0; i<l; i++) {
            returnArray[i] = stringArray[i].charAt(0);
        }
        return returnArray;

    }

}

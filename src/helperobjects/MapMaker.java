package helperobjects;

import game.Player;
import mapobjects.framework.Blueprint;
import mapobjects.framework.MapObject;
import mapobjects.framework.SpawnComponent;
import mapobjects.initialized.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map;

public class MapMaker {

    private final int worldIndex, levelIndex;
    private final File mapFile;
    private final int xTile, yTile;
    private final Player player;

    private final MapObject[][][] layers;
    private final Tile[][] tiles;
    private final MapObject[][] mapObjects;
    private final Coin[][] coins;

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

    public MapMaker(int worldIndex, int levelIndex, int xTile, int yTile, Player player) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.xTile = xTile;
        this.yTile = yTile;
        this.player = player;
        tiles = new Tile[yTile][xTile];
        mapObjects = new MapObject[yTile][xTile];
        coins = new Coin[yTile][xTile];
        layers = new MapObject[][][]{tiles, mapObjects, coins};
        mapFile = new File(("misc/maps/%d%d.txt").formatted(worldIndex, levelIndex));
    }

    //GETTERS

    public MapObject[][][] getLayers() {
        return layers;
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
        Map<Door, Integer> doorsToWire = new HashMap<>();
        Map<String, Button> buttonMap = new HashMap<>();

        for (int y = 1; y <= yTile; y++) {
            for (int x = 1; x <= xTile; x++) {

                Tile initializedTile;
                MapObject initializedMapObject;
                Coin initializedCoin;

                boolean isApproachable = approachability[y - 1][x - 1];
                String tileCode = mapData[y - 1][x - 1];
                char char0 = tileCode.charAt(0);

                Blueprint blueprint = new Blueprint(worldIndex, x, y);

                if (BASIC_CHARACTERS.contains(char0)) { // quick codes

                    char char1 = tileCode.charAt(1), char2 = tileCode.charAt(2);
                    initializedTile = blueprint.mutateToTile(char0, isApproachable);

                    initializedCoin = initializeCoin(char1, blueprint);

                    initializedMapObject = switch (char1) {
                        case '@' -> blueprint.mutateToMine();
                        case '%' -> blueprint.mutateToMortar(tiles, xTile);
                        case ':' -> {
                            Button button = blueprint.mutateToBigButton();
                            buttonMap.put("%d:%d".formatted(x, y), button);
                            yield button;
                        }
                        case '.' -> {
                            Button button = blueprint.mutateToLittleButton();
                            buttonMap.put("%d:%d".formatted(x, y), button);
                            yield button;
                        }
                        case '0' -> {
                            Point.SpawnPoint spawnPoint = blueprint.mutateToSpawnPoint(char2 == 'B');
                            checkPoints[0] = spawnPoint;
                            player.setSpawnPoint(spawnPoint.getCenterCoordinates());
                            yield spawnPoint;
                        }
                        case '1', '2', '3', '4' -> {
                            Point.CheckPoint checkPoint = blueprint.mutateToCheckPoint(char1 - '0', char2 == 'B');
                            checkPoints[char1 - '0'] = checkPoint;
                            yield checkPoint;
                        }
                        case '5', '6', '7', '8', '9' -> blueprint.mutateToWinPoint(char1 - '5', char2 == 'B');
                        case '~' -> blueprint.mutateToWoodenChest(new char[]{char2});
                        case '≈' -> blueprint.mutateToSilverChest(new char[]{char2});
                        case '=' -> blueprint.mutateToGoldenChest(new char[]{char2});
                        case 'D' -> blueprint.mutateToDoor(char2);
                        default -> null;
                    };

                } else { // A37 initializer, coins on top too

                    int lineIndex = Integer.parseInt(tileCode.substring(1, 3)) - 37;
                    String[] details = objectDetails.get(lineIndex).split("; ");

                    String onTileCode = details[0];

                    initializedTile = blueprint.mutateToTile(onTileCode.charAt(0), isApproachable);

                    initializedCoin = initializeCoin(onTileCode.charAt(1), blueprint);

                    initializedMapObject = switch (char0) {

                        case 'C' -> {
                            String[] buffs = details[2].split(", ");
                            yield switch (details[1].charAt(0)) {
                                case '~' -> blueprint.mutateToWoodenChest(toCharArray(buffs));
                                case '≈' -> blueprint.mutateToSilverChest(toCharArray(buffs));
                                case '=' -> blueprint.mutateToGoldenChest(toCharArray(buffs));
                                default -> {
                                    System.out.println("error at C37");
                                    yield null;
                                }
                            };
                        }
                        case 'D' -> {
                            int length = (details.length == 4) ? Integer.parseInt(details[3]) : 4;
                            Door door = blueprint.mutateToDoor(details[1].charAt(0), length);
                            doorsToWire.put(door, lineIndex);
                            yield door;
                        }
                        case 'S' -> {
                            String[] messages = details[2].split(", ");
                            yield switch (details[1].charAt(0)) {
                                case '\'' -> blueprint.mutateToSign(messages);
                                case '"' -> {
                                    for (int i = 0; i < messages.length; i++) {
                                        messages[i] = objectDetails.get(Integer.parseInt(messages[i]) - 37);
                                    }
                                    yield blueprint.mutateToSign(messages);
                                }
                                default -> {
                                    System.out.println("error at S37");
                                    yield null;
                                }
                            };
                        }
                        default -> {
                            System.out.println("default message for A37, an error occurred.");
                            yield null;
                        }
                    };
                }

                tiles[y-1][x-1] = initializedTile;
                mapObjects[y-1][x-1] = initializedMapObject;
                coins[y-1][x-1] = initializedCoin;
            }
        }

        wireDoorsToButtons(objectDetails, doorsToWire, buttonMap);
        setPrevToCheckPoints();
    }


    private Coin initializeCoin(char type, Blueprint blueprint) {
        return switch (type) {
            case 'o' -> blueprint.mutateToSingleCoin();
            case '*' -> blueprint.mutateToTripleCoin();
            case '$' -> blueprint.mutateToCoinBag();
            default -> null;
        };
    }

    private void setPrevToCheckPoints() {
        for (int i = 0; i<5; i++) {
            Point.CheckPoint currentCheckPoint = checkPoints[i];
            if (i!=0) {
                currentCheckPoint.setPrev(checkPoints[i-1]);
            }
        }
    }

    private void wireDoorsToButtons(ArrayList<String> objectDetails, Map<Door, Integer> doorsToWire, Map<String, Button> buttonMap) {
        //wire doors to buttons:
        for (Map.Entry<Door, Integer> pair : doorsToWire.entrySet()) {
            Door door = pair.getKey();
            String[] items = objectDetails.get(pair.getValue()).split("; ");
//items[0] is the tile on top, already initialized. items[1] is the type of door.
//items[2] is the door coordinates as x1:y1, x2:y2, ... and if there exists, items[3] is the length of the door
//careful! the button coordinates entered in txt file has starting index 1, while mapData has 0!

            //hold the buttons to wire to the door in here
            ArrayList<Button> buttonsToWire = new ArrayList<>();

            for (String s : items[2].split(", ")) {
                if (s.isEmpty()) continue;
                buttonsToWire.add(buttonMap.get(s));
            }

            int l = 4; //length of door is 4 by default
            if (items.length==4) {l = Integer.parseInt(items[3]);} //change length if specified

            door.setButtons(buttonsToWire.toArray(new Button[0]));
        }
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

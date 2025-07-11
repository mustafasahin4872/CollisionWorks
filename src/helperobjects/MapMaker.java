package helperobjects;

import game.Player;
import mapobjects.framework.Collidable;
import mapobjects.framework.GridObject;
import mapobjects.initialized.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map;

//creates all the map objects from map file
public class MapMaker {

    private final int worldIndex;
    private final File mapFile;
    private final int xTile, yTile;
    private final Player player;

    private final GridObject[][][] layers;
    private final Tile[][] tiles;
    private final GridObject[][] gridObjects;
    private final Coin[][] coins;

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
        this(worldIndex, levelIndex, xTile, yTile, player, false);
    }

    public MapMaker(int worldIndex, int levelIndex, int xTile, int yTile, Player player, boolean isSelectionMap) {
        this.worldIndex = worldIndex;
        this.xTile = xTile;
        this.yTile = yTile;
        this.player = player;
        tiles = new Tile[yTile][xTile];
        gridObjects = new GridObject[yTile][xTile];
        coins = new Coin[yTile][xTile];
        layers = new GridObject[][][]{tiles, gridObjects, coins};
        if (isSelectionMap) {
            mapFile = new File(("misc/maps/selectionMaps/%d%d.txt").formatted(worldIndex, levelIndex));
        } else {
            mapFile = new File(("misc/maps/gameMaps/%d%d.txt").formatted(worldIndex, levelIndex));
        }

    }

    //GETTERS

    public GridObject[][][] getLayers() {
        return layers;
    }


    //MAIN METHOD

    public void mapMaker() {
        String[][] mapData = new String[yTile][xTile];
        ArrayList<String> objectDetails = new ArrayList<>();

        // fill the mapData and objectDetails storages
        extractMapData(mapData, objectDetails);

        // initialize all objects parsing through mapData and objectDetails
        initializeObjects(mapData, objectDetails);

        // all objects are ready to be processed, stored in layers
    }


    //INITIALIZERS

    //creates all objects
    private void initializeObjects(String[][] mapData, ArrayList<String> objectDetails) {

        // hold the D37 codes here with their locations to later wire them to buttons
        Map<Door, Integer> doorsToWire = new HashMap<>();
        Map<String, Button> buttonMap = new HashMap<>();
        Point.CheckPoint[] checkPointsToSetPrev = {
                new Point.SpawnPoint(0,0,0),
                new Point.CheckPoint(0,0,0,1),
                new Point.CheckPoint(0,0,0,2),
                new Point.CheckPoint(0,0,0,3),
                new Point.CheckPoint(0,0,0,4),
                };

        for (int y = 0; y < yTile; y++) {
            for (int x = 0; x < xTile; x++) {

                int xNum = x+1, yNum = y+1;
                Blueprint blueprint = new Blueprint(worldIndex, xNum, yNum);

                Tile initializedTile;
                GridObject initializedGridObject;
                Coin initializedCoin;

                String tileCode = mapData[y][x];

                char char0 = tileCode.charAt(0);
                if (BASIC_CHARACTERS.contains(char0)) { // quick codes

                    char char1 = tileCode.charAt(1), char2 = tileCode.charAt(2);

                    initializedTile = blueprint.mutateToTile(char0);
                    initializedCoin = initializeCoin(char1, blueprint);
                    initializedGridObject = switch (char1) {
                        case '@' -> blueprint.mutateToMine();
                        case '%' -> blueprint.mutateToMortar(layers);
                        case ':' -> {
                            Button button = blueprint.mutateToBigButton();
                            buttonMap.put("%d:%d".formatted(xNum, yNum), button);
                            yield button;
                        }
                        case '.' -> {
                            Button button = blueprint.mutateToLittleButton();
                            buttonMap.put("%d:%d".formatted(xNum, yNum), button);
                            yield button;
                        }
                        case '0' -> {
                            Point.SpawnPoint spawnPoint = blueprint.mutateToSpawnPoint(char2 == 'B');
                            checkPointsToSetPrev[0] = spawnPoint;
                            yield spawnPoint;
                        }
                        case '1', '2', '3', '4' -> {
                            Point.CheckPoint checkPoint = blueprint.mutateToCheckPoint(char1 - '0', char2 == 'B');
                            checkPointsToSetPrev[char1 - '0'] = checkPoint;
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

                    initializedTile = blueprint.mutateToTile(onTileCode.charAt(0));
                    initializedCoin = initializeCoin(onTileCode.charAt(1), blueprint);
                    initializedGridObject = switch (char0) {
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
                addEmptyGridObjects(initializedGridObject, gridObjects);
                tiles[y][x] = initializedTile;
                if (initializedGridObject != null) {
                    gridObjects[y][x] = initializedGridObject;
                }
                coins[y][x] = initializedCoin;
            }
        }
        wireDoorsToButtons(objectDetails, doorsToWire, buttonMap);
        setPrevToCheckPoints(checkPointsToSetPrev);
    }


    private Coin initializeCoin(char type, Blueprint blueprint) {
        return switch (type) {
            case 'o' -> blueprint.mutateToSingleCoin();
            case '*' -> blueprint.mutateToTripleCoin();
            case '$' -> blueprint.mutateToCoinBag();
            default -> null;
        };
    }


    //LAST WIRING METHODS

    private void setPrevToCheckPoints(Point.CheckPoint[] checkPoints) {
        for (int i = 0; i<5; i++) {
            Point.CheckPoint currentCheckPoint = checkPoints[i];
            if (i!=0) {
                currentCheckPoint.setPrev(checkPoints[i-1]);
            } else {
                player.setSpawnPoint(currentCheckPoint.getCenterCoordinates());
                player.resetLastCheckPointIndex();
                player.respawn();
            }
        }
    }

    private void wireDoorsToButtons(ArrayList<String> objectDetails, Map<Door, Integer> doorsToWire, Map<String, Button> buttonMap) {
        //wire doors to buttons:
        for (Map.Entry<Door, Integer> pair : doorsToWire.entrySet()) {
            Door door = pair.getKey();
            String[] items = objectDetails.get(pair.getValue()).split("; ");

            //hold the buttons to wire to the door in here
            ArrayList<Button> buttonsToWire = new ArrayList<>();

            for (String s : items[2].split(", ")) {
                if (s.isEmpty()) continue;
                buttonsToWire.add(buttonMap.get(s));
            }

            door.setButtons(buttonsToWire.toArray(new Button[0]));
        }
    }

    private void addEmptyGridObjects(GridObject gridObject, GridObject[][] gridObjects) {
        if (gridObject == null) return;
        int xNum = gridObject.getXNum();
        int yNum = gridObject.getYNum();
        int x = xNum - 1, y = yNum - 1;
        double tileSide = GridObject.TILE_SIDE;

        if (gridObject.isCornerAligned()) {
            int xSpan = (int)Math.ceil(gridObject.getWidth() / tileSide);
            int ySpan = (int)Math.ceil(gridObject.getHeight() / tileSide);
            for (int i = 0; i < ySpan; i++) {
                for (int j = 0; j < xSpan; j++) {
                    gridObjects[y + i][x + j] = new EmptyGridObject(worldIndex, xNum + j, yNum + i, gridObject);
                }
            }
        } else {
            int xSpan = (int)Math.ceil((gridObject.getWidth() / tileSide - 1) / 2);
            int ySpan = (int)Math.ceil((gridObject.getHeight() / tileSide - 1) / 2);
            for (int i = -ySpan; i <= ySpan; i++) {
                for (int j = -xSpan; j <= xSpan; j++) {
                    gridObjects[y + i][x + j] = new EmptyGridObject(worldIndex, xNum + j, yNum + i, gridObject);
                }
            }
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

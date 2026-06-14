package game;

import helpers.ShopEntry;
import mapobjects.category.MapObject;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Buff;
import mapobjects.mapobject.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class GameState {

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // BASIC FIELDS
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    public enum STATE {
        SELECTION, GAME, DEAD, PASSED, NEXT, ALTERNATE1, ALTERNATE2, SHOP, PAUSE, QUIT
    }
    private STATE state = STATE.SELECTION;

    private int worldIndex;
    private int levelIndex;
    private Player player;

    private int coinAmount = 0;
    private int gemAmount = 0;

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // SHOP ENTRIES
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    private final List<ShopEntry<Player>> buyableSkins = List.of(
        new ShopEntry<>(new Player.AnimatedPlayer("Mike"), 0, true),
        new ShopEntry<>(new Player.AnimatedPlayer("Sakura"), 0, true)
    );

    private final List<ShopEntry<Accessory>> buyableAccessories = List.of(
        new ShopEntry<>(new Accessory.Hat("fedora"), 0, true),
        new ShopEntry<>(new Accessory.Tie("tie"), 100, true),
        new ShopEntry<>(new Accessory.Headpiece("coquette"), 0, true),
        new ShopEntry<>(new Accessory.Necklace("dollar"), 200, true),
        new ShopEntry<>(new Accessory.Necklace("sorcerer"), 250, true),
        new ShopEntry<>(new Accessory.Pin("star"), 50000, true),
        new ShopEntry<>(new Accessory.Pin("sheriff"), 1075, true)
    );

    private final List<ShopEntry<Buff>> buyableBuffs = List.of(
        new ShopEntry<>(new Buff.SpeedBuff(0, 0), 0, false),
        new ShopEntry<>(new Buff.ShieldBuff(0, 0), 0, false),
        new ShopEntry<>(new Buff.ShrinkBuff(0, 0), 0, false),
        new ShopEntry<>(new Buff.MagnetBuff(0, 0), 0, false),
        new ShopEntry<>(new Buff.VisionBuff(0, 0), 0, false)
    );


    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // OWNED ITEMS
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    private final ArrayList<Player> skins = new ArrayList<>(List.of(new Player.RegularPlayer()));
    private final ArrayList<Accessory> accessories = new ArrayList<>();

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------


    public GameState() {
        this.player = new Player.RegularPlayer();
        accessories.add(null);
    }

    public GameState(int worldIndex, int levelIndex) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.player = new Player.RegularPlayer();
        accessories.add(null);

    }

    public GameState(int worldIndex, int levelIndex, Player player) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.player = player;
        accessories.add(null);

    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // GETTERS AND SETTERS - SMALL FUNCTIONS
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------


    public int getWorldIndex() {
        return worldIndex;
    }

    public void setWorldIndex(int worldIndex) {
        this.worldIndex = worldIndex;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getCoinAmount() {
        return coinAmount;
    }

    public void addCoin(int added) {
        coinAmount += added;
    }

    public void spendCoin(int spent) {coinAmount -= spent;}

    public int getGemAmount() {
        return gemAmount;
    }

    public void addGem(int added) {
        gemAmount += added;
    }

    public void spendGem(int spent) {gemAmount -= spent;}

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public void addOwnedSkin(Player skin) {
        skins.add(skin);
    }

    public void addOwnedAccessory(Accessory accessory) {
        accessories.add(accessory);
    }

    public List<Player> getSkins() {
        return skins;
    }

    public List<Accessory> getAccessories() {
        return accessories;
    }

    public List<ShopEntry<Player>> getBuyableSkins() {
        return buyableSkins;
    }

    public List<ShopEntry<Accessory>> getBuyableAccessories() {
        return buyableAccessories;
    }

    public List<ShopEntry<Buff>> getBuyableBuffs() {
        return buyableBuffs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // STATE MUTATION
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    public void restart() {
        state = STATE.NEXT;
        player.restart();
    }

    public void nextLevel() {

        if (levelIndex == 12) {

            if (worldIndex == 4)
                return; // no levels left

            worldIndex++;
            levelIndex = 1;

        } else {
            levelIndex++;
        }

    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // BUY LOGIC
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    public boolean canAfford(ShopEntry shopEntry) {
        int cost = shopEntry.getCost();
        if (shopEntry.isCosmetic()) return cost <= gemAmount;
        else return cost <= coinAmount;
    }

    public void buy(ShopEntry shopEntry) {
        shopEntry.sell();
        int cost = shopEntry.getCost();
        MapObject item = shopEntry.getItem();
        if (shopEntry.isCosmetic()) {
            spendGem(shopEntry.getCost());
            if (item instanceof Player s) {
                addOwnedSkin(s);
            } else if (item instanceof Accessory a) {
                addOwnedAccessory(a);
            }
        } else {
            spendCoin(cost);
            if (item instanceof Buff b) {
                b.playerIsOn(player);
            }
        }
    }



}

package game.core;

import game.data.ShopEntry;
import mapobjects.traits.Equippable;
import mapobjects.traits.MapObject;
import mapobjects.entities.Accessory;
import mapobjects.entities.Buff;
import mapobjects.entities.Gun;
import mapobjects.entities.Player;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // BASIC FIELDS
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    public enum STATE {
        SELECTION, ACCESSORY, GAME, DEAD, PASSED, NEXT, SHOP, ALTERNATE1, ALTERNATE2, PAUSE, QUIT
    }
    private STATE state = STATE.SELECTION;

    private int worldIndex;
    private int levelIndex;
    private Player player;

    private int coinAmount = 0;
    private int gemAmount = 0;

    private int collectedCoins = 0;
    private int collectedGems = 0;

    private Accessory[] equipped = new Accessory[3];

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // OWNED ITEMS
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    private final ArrayList<Player> skins = new ArrayList<>(List.of(new Player()));
    private final ArrayList<Accessory> accessories = new ArrayList<>();
    private final ArrayList<Buff> permanentBuffs = new ArrayList<>();
    private final  ArrayList<Gun> guns = new ArrayList<>(List.of(new Gun.Pacifist(), new Gun.Handgun(), new Gun.MachineGun()));

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------


    public GameState() {
        this.player = new Player();
        accessories.add(null);
    }

    public GameState(int worldIndex, int levelIndex) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.player = new Player();
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

    public int getCollectedCoins() {
        return collectedCoins;
    }

    public void collectCoin(int added) {
        collectedCoins += added;
    }

    public void spendCoin(int spent) {coinAmount -= spent;}

    public int getGemAmount() {
        return gemAmount;
    }

    public int getCollectedGems() {
        return collectedGems;
    }

    public void collectGem(int added) {
        collectedGems += added;
    }

    public void spendGem(int spent) {gemAmount -= spent;}

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public Accessory[] getEquipped() {
        return equipped;
    }

    public void setEquipped(Accessory[] equipped) {
        this.equipped = equipped;
    }

    public void addOwnedSkin(Player skin) {
        skins.add(skin);
    }

    public void addOwnedAccessory(Accessory accessory) {
        accessories.add(accessory);
    }

    public void addOwnedBuff(Buff buff) {permanentBuffs.add(buff);}

    public void addOwnedGun(Gun gun) {guns.add(gun);}

    public List<Player> getSkins() {
        return skins;
    }

    public List<Accessory> getAccessories() {
        return accessories;
    }

    public List<Buff> getPermanentBuffs() {
        return permanentBuffs;
    }

    public ArrayList<Gun> getGuns() {
        return guns;
    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // STATE MUTATION
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    public void continueGame() {
        state = STATE.GAME;
    }

    public void exitGame() {
        collectedCoins = 0;
        collectedGems = 0;
        state = STATE.SELECTION;
    }

    public void restartGame() {
        collectedCoins = 0;
        collectedGems = 0;
        state = STATE.NEXT;
        player.restart();
    }

    public void nextLevel() {

        coinAmount += collectedCoins;
        gemAmount += collectedGems;
        collectedCoins = 0;
        collectedGems = 0;

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
        int coinCost = shopEntry.getCoinCost();
        int gemCost = shopEntry.getGemCost();
        return (coinCost<=coinAmount && gemCost<=gemAmount);
    }

    public void buy(ShopEntry shopEntry) {
        shopEntry.sell();

        Equippable item = shopEntry.getItem();
        spendGem(shopEntry.getCoinCost());
        spendCoin(shopEntry.getGemCost());

        if (item instanceof Player s) {
            addOwnedSkin(s);
        } else if (item instanceof Accessory a) {
            addOwnedAccessory(a);
        } else if (item instanceof Buff b) {
            b.expire(); // stops the animation
            addOwnedBuff(b);
        } else if (item instanceof Gun g) {
            addOwnedGun(g);
        }

    }

}

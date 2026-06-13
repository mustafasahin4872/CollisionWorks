package game;

import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Player;

import java.util.ArrayList;

public class GameState {

    public enum STATE {
        SELECTION, GAME, DEAD, PASSED, NEXT, ALTERNATE1, ALTERNATE2, SHOP, PAUSE, QUIT
    }

    private STATE state = STATE.SELECTION;

    public int worldIndex, levelIndex;
    public Player player;
    public ArrayList<Player> boughtSkins = new ArrayList<>();
    public ArrayList<Accessory> boughtAccessories = new ArrayList<>();

    public GameState() {
        this.player = new Player.RegularPlayer();
        boughtSkins.add(player);
        boughtAccessories.add(new Accessory.Hat("fedora"));
    }

    public GameState(int worldIndex, int levelIndex) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.player = new Player.RegularPlayer();
        boughtSkins.add(player);
        boughtAccessories.add(new Accessory.Hat("fedora"));
    }

    public GameState(int worldIndex, int levelIndex, Player player) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.player = player;
        boughtSkins.add(player);
        boughtAccessories.add(new Accessory.Hat("fedora"));
    }

    public STATE getState() {
        return state;
    }

    public void resetState() {
        setState(STATE.GAME);
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public void restart() {
        state = STATE.NEXT;
        player.restart();
    }

    public void nextLevel() {

        if (levelIndex == 12) {

            if (worldIndex == 4) return; // no levels left

            worldIndex++;
            levelIndex = 1;

        } else {
            levelIndex++;
        }

    }

    public void buySkin(Player skin) {
        boughtSkins.add(skin);
    }

    public void buyAccessory(Accessory accessory) {
        boughtAccessories.add(accessory);
    }

}

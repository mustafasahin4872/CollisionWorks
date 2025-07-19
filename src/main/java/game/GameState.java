package game;

import mapobjects.mapobject.Player;

public class GameState {

    public enum STATE {
        SELECTION, GAME, DEAD, PASSED, NEXT, ALTERNATE1, ALTERNATE2, SHOP, PAUSE, QUIT
    }

    private STATE state = STATE.SELECTION;

    public int worldIndex, levelIndex;
    public Player player;

    public GameState() {}

    public GameState(int worldIndex, int levelIndex, Player player) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
        this.player = player;
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

        setState(STATE.NEXT);

    }

}

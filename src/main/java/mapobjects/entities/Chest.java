package mapobjects.entities;

import game.io.Drawer;
import game.io.Drawer.PictureDrawer;
import mapobjects.components.Trigger;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.triggerables.PlayerOnTriggerable;

import static game.core.GameState.gameState;

public class Chest extends GridObject implements Drawable, PlayerOnTriggerable {

    public enum ChestType {
        WoodenChest(5, 0), SilverChest(10, 1), GoldenChest(20, 3);

        ChestType(int coinNum, int gemNum) {
            this.coinNum = coinNum;
            this.gemNum = gemNum;
        }

        private final int coinNum;
        private final int gemNum;
    }

    private boolean isOpen;
    private final int coinNum;
    private final int gemNum;
    private final String type;
    private final PictureDrawer drawer;
    private final Trigger<Player> playerTrigger;

    public Chest(int worldIndex, int xNum, int yNum, ChestType chestType) {
        super(worldIndex, xNum, yNum, 2, 2, true);
        this.coinNum = chestType.coinNum;
        this.gemNum = chestType.gemNum;
        this.type = chestType.name();
        drawer = new PictureDrawer(positionBox, getDirectory1(), type+"/0");
        playerTrigger = new Trigger<>(positionBox, this::triggerOpen);
    }

    private void triggerOpen(Player player) {
        openChest();
    }

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }

    @Override
    public Trigger<Player> getPlayerOnTrigger() {
        return playerTrigger;
    }

    public void openChest() {
        if (isOpen) return;
        isOpen = true;
        drawer.setName(type + "/1");
        gameState.collectCoin(coinNum);
        gameState.collectGem(gemNum);
    }

}

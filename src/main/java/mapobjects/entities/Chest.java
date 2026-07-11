package mapobjects.entities;

import game.io.Drawer.PictureDrawer;
import mapobjects.components.Box;
import mapobjects.traits.Drawable;
import mapobjects.traits.GridObject;
import mapobjects.traits.OnEffector;

import static game.core.GameState.gameState;

public class Chest extends GridObject implements OnEffector, Drawable {

    public enum ChestType {
        WoodenChest(5, 0), SilverChest(10, 1), GoldenChest(20, 3);

        ChestType(int coinNum, int gemNum) {
            this.coinNum = coinNum;
            this.gemNum = gemNum;
        }

        private final int coinNum;
        private final int gemNum;
    }

    private final Box effectBox;
    private boolean isOpen;
    private final int coinNum;
    private final int gemNum;
    private final String type;
    private final PictureDrawer drawer;

    public Chest(int worldIndex, int xNum, int yNum, ChestType chestType) {
        super(worldIndex, xNum, yNum, 2, 2, true);
        this.coinNum = chestType.coinNum;
        this.gemNum = chestType.gemNum;
        this.type = chestType.name();
        effectBox = positionBox.clone();
        effectBox.setCorners(new double[]{
                positionBox.getCorner(0), (positionBox.getCorner(1) + positionBox.getCorner(3)) / 2,
                positionBox.getCorner(2), positionBox.getCorner(3)
        });
        drawer = new PictureDrawer(positionBox, getDirectory1(), type+"/0");
    }

    @Override
    public PictureDrawer getDrawer() {
        return drawer;
    }

    @Override
    public Box getEffectBox() {
        return effectBox;
    }

    @Override
    public void checkPlayerIsOn(Player player) {
        checkPlayerCornerIsOn(player);
    }

    @Override
    public void playerIsOn(Player player) {
        openChest();
    }


    public void openChest() {
        if (isOpen) return;
        isOpen = true;
        drawer.setName(type + "/1");
        gameState.collectCoin(coinNum);
        gameState.collectGem(gemNum);
    }

}

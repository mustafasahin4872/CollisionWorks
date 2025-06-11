package helperobjects;

import mapobjects.*;

public class MapObjectMaker {
    private final int worldIndex;
    private final int xNum;
    private final int yNum;

    public MapObjectMaker(int worldIndex, int xNum, int yNum) {
        this.worldIndex = worldIndex;
        this.xNum = xNum;
        this.yNum = yNum;
    }

    public Coin.SingleCoin createSingleCoin() {
        return new Coin.SingleCoin(worldIndex, xNum, yNum);
    }

    public Coin.TripleCoin createTripleCoin() {
        return new Coin.TripleCoin(worldIndex, xNum, yNum);
    }

    public Coin.CoinBag createCoinBag() {
        return new Coin.CoinBag(worldIndex, xNum, yNum);
    }

    public Mine createMine() {
        return new Mine(worldIndex, xNum, yNum);
    }

    public Mortar createMortar(Tile[] tiles, int xTile) {
        return new Mortar(worldIndex, xNum, yNum, tiles, xTile);
    }

    public Button.BigButton createBigButton() {
        return new Button.BigButton(worldIndex, xNum, yNum);
    }

    public Button.LittleButton createLittleButton() {
        return new Button.LittleButton(worldIndex, xNum, yNum);
    }

    public Point.SpawnPoint createSpawnPoint(boolean isBlue) {
        return new Point.SpawnPoint(worldIndex, xNum, yNum, isBlue);
    }

    public Point.CheckPoint createCheckPoint(int index, boolean isBlue) {
        return new Point.CheckPoint(worldIndex, xNum, yNum, index, isBlue);
    }

    public Point.WinPoint createWinPoint(int index, boolean isBlue) {
        return new Point.WinPoint(worldIndex, xNum, yNum, index, isBlue);
    }

    public Chest.WoodenChest createWoodenChest(char[] buffs) {
        return new Chest.WoodenChest(worldIndex, xNum, yNum, buffs);
    }

    public Chest.SilverChest createSilverChest(char[] buffs) {
        return new Chest.SilverChest(worldIndex, xNum, yNum, buffs);
    }

    public Chest.GoldenChest createGoldenChest(char[] buffs) {
        return new Chest.GoldenChest(worldIndex, xNum, yNum, buffs);
    }

    public Door createDoor(Alignment alignment) {
        return new Door(worldIndex, xNum, yNum, alignment);
    }

    public Sign createSign(String[] messages) {
        return new Sign(worldIndex, xNum, yNum, messages);
    }
}

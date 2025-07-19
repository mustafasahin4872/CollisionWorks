package helpers;

public enum MapType {

    NORMAL(64, 36),
    IN_BETWEEN(32, 16),
    SELECTION(18, 12);

    public final int xTile;
    public final int yTile;

    MapType(int xTile, int yTile) {
        this.xTile = xTile;
        this.yTile = yTile;
    }
}

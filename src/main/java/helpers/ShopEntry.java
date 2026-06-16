package helpers;

import mapobjects.category.MapObject;

public class ShopEntry {
    private final MapObject item;
    private final int cost;
    private final boolean isCosmetic;
    private boolean isSold;

    public ShopEntry(MapObject item, int cost, boolean isCosmetic) {
        this.item = item;
        this.cost = cost;
        this.isCosmetic = isCosmetic;
    }

    public boolean isSold() {
        return isSold;
    }

    public void sell() {
        isSold = true;
    }

    public int getCost() {
        return cost;
    }

    public boolean isCosmetic() {
        return isCosmetic;
    }

    public MapObject getItem() {
        return item;
    }

    public String getName() {
        return item.getName();
    }

}

package helpers;

import mapobjects.category.MapObject;

public class ShopEntry<T extends MapObject> {
    private final T item;
    private final int cost;
    private final boolean isCosmetic;
    private boolean isSold;

    public ShopEntry(T item, int cost, boolean isCosmetic) {
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

    public T getItem() {
        return item;
    }

    public String getName() {
        return item.getName();
    }

    public void drawBig(double multiplier) {
        item.drawBig(multiplier);
    }

}

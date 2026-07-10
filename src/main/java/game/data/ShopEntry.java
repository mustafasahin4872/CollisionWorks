package game.data;

import mapobjects.traits.MapObject;

public class ShopEntry {

    private final MapObject item;
    private final int coinCost;
    private final int gemCost;
    private boolean isSold;

    public ShopEntry(MapObject item, int coinCost, int gemCost) {
        this.item = item;
        this.coinCost = coinCost;
        this.gemCost = gemCost;
    }

    public boolean isSold() {
        return isSold;
    }

    public void sell() {
        isSold = true;
    }

    public int getCoinCost() {
        return coinCost;
    }

    public int getGemCost() {
        return gemCost;
    }

    public MapObject getItem() {
        return item;
    }

    public String getName() {
        return item.getName();
    }

}

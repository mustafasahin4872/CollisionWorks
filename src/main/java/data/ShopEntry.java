package data;

import mapobjects.traits.Equippable;

public class ShopEntry {

    private final Equippable item;
    private final int coinCost;
    private final int gemCost;
    private boolean isSold;

    public ShopEntry(Equippable item, int coinCost, int gemCost) {
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

    public Equippable getItem() {
        return item;
    }

    public String getName() {
        return item.getDrawer().getName();
    }

}

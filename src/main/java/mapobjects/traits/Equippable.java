package mapobjects.traits;

import java.awt.*;

public abstract class Equippable extends MapObject {

    public enum RARITY {
        RARE(new Color(111, 217, 110)),
        EPIC(new Color(51, 216, 187)),
        MYTHIC(new Color(185, 0, 0)),
        LEGENDARY(new Color(241, 241, 241))
        ;

        private final Color color;

        RARITY(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

    }

    private final RARITY rarity;


    public Equippable(RARITY rarity) {
        this.rarity = rarity;
    }

    public Equippable(int worldIndex, double x, double y, double width, double height, RARITY rarity) {
        super(worldIndex, x, y, width, height);
        this.rarity = rarity;
    }

    public Equippable(int worldIndex, double x, double y, double width, double height, String name, RARITY rarity) {
        super(worldIndex, x, y, width, height, name);
        this.rarity = rarity;
    }

    public Equippable(int worldIndex, double x, double y, double width, double height, String name, String imageType, RARITY rarity) {
        super(worldIndex, x, y, width, height, name, imageType);
        this.rarity = rarity;
    }

    public RARITY getRarity() {
        return rarity;
    }

}

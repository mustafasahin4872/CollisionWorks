package mapobjects.traits;

import game.core.Main;

import java.awt.*;
import java.io.*;

public abstract class Equippable extends MapObject implements Drawable {

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

    public abstract String[] getStats();

    public String getDescription() {
        String pathname = Main.RESOURCES_ROOT + "infos/"  + directory + name.split("/")[0] + ".txt";
        File infoFile = new File(pathname);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(infoFile));
            String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                System.out.println("info file is empty");
                return "no description";
            }
            return line;
        } catch (FileNotFoundException e) {
            System.out.println("info file not found: " + pathname);
            return "no description";
        } catch (IOException e) {
            System.out.println("I/O exception");
            return "no description";
        }
    }

}

package mapobjects.entities;
import mapobjects.traits.MapObject;

import java.awt.*;

import static helpers.methods.HelperMethods.getDirectionString;
import static mapobjects.traits.GridObject.TILE_SIDE;

/// different accessories are placed on different locations on player.
/// therefore, we created many subclasses, each having their unique setCoordinates() function
/// also, each accessory has 9 images, and their names change depending on the player's direction.
/// there are 3 main accessory interfaces: Hat, Necklace and Pin. these interfaces categorize the different subclasses.
public abstract class Accessory extends MapObject {

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
    protected Player player;
    private final String accessoryName;
    private final double defaultWidth, defaultHeight;
    private boolean alone;

    public Accessory(String accessoryName, double defaultWidth, double defaultHeight, RARITY rarity) {
        super(0, 0, 0, defaultWidth, defaultHeight, accessoryName+"/0");
        this.player = new Player(); // replaced later
        this.accessoryName = accessoryName;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.rarity = rarity;
        update();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getAccessoryName() {
        return accessoryName;
    }

    public RARITY getRarity() {
        return rarity;
    }

    public void resetSize() {
        double scale = player.getBaseSide() / TILE_SIDE;
        setWidth(defaultWidth * scale);
        setHeight(defaultHeight * scale);
    }

    public void setAlone(boolean alone) {
        this.alone = alone;
    }

    //------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------

    public void update() {
        setCoordinates();
        changeName();
    }

    //sets coordinates using player's position. different coordinates for each accessory type
    protected abstract void setCoordinates();

    private void changeName() {
        String direction = getDirectionString(player.getXDirection(), player.getYDirection());
        setName(accessoryName + "/" + direction);
    }

    @Override
    public void drawBig(double multiplier) {
        resize(multiplier);
        player.resize(multiplier);
        if (!alone) update();
        draw();
        resetSize();
        player.resetSize();
    }

    @Override
    public void drawBigAt(double x, double y, double multiplier) {
        if (alone) {
            super.drawBigAt(x, y, multiplier);
        } else { // still, draw ONLY ACCESSORY, update it accordingly.
            double oldX = getX();
            double oldY = getY();
            double playerX = player.getX();
            double playerY = player.getY();
            player.setCenterCoordinates(x, y);

            resize(multiplier);
            player.resize(multiplier);
            update();

            draw();

            resetSize();
            player.resetSize();

            player.setCenterCoordinates(playerX, playerY);
            setCenterCoordinates(oldX, oldY);
            // do not call update!!!
            // if it was on the player, put it back to player
            // if not, put it back to where it is
        }
    }

    @Override
    public String[] getStats() {
        return super.getStats();
    }

    //------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------

    public abstract static class Headwear extends Accessory{
        public Headwear(String accessoryName, double defaultWidth, double defaultHeight, RARITY rarity) {
            super(accessoryName, defaultWidth, defaultHeight, rarity);
        }
    }
    public abstract static class Brooch extends Accessory {
        public Brooch(String accessoryName, double defaultWidth, double defaultHeight, RARITY rarity) {
            super(accessoryName, defaultWidth, defaultHeight, rarity);
        }
    }
    public abstract static class Neckwear extends Accessory {
        public Neckwear(String accessoryName, double defaultWidth, double defaultHeight, RARITY rarity) {
            super(accessoryName, defaultWidth, defaultHeight, rarity);
        }
    }

    //Stays on the middle top of the player, does not change as player moves.
    public static class Hat extends Headwear {
        public Hat(String name, RARITY rarity) {
            super(name, 65, 25, rarity);
        }

        @Override
        protected void setCoordinates() {
            double x = player.getX();
            double y = player.getY()-(2*player.getHeight()/5)-getHeight()/2;

            setX(x);
            setY(y);
        }

    }

    //Stays in the center of the player, does not change as player moves.
    public static class Necklace extends Neckwear {

        public Necklace(String name, RARITY rarity) {
            super(name, 50, 25, rarity);
        }

        @Override
        protected void setCoordinates() {
            double x = player.getX();

            double baseY = player.getY()+player.getHeight()/3;
            double yShift = player.getHeight() / 8;
            int yDir = player.getYDirection();
            double y = baseY + yDir*yShift;

            setX(x);
            setY(y);
        }

    }

    //sits on lower half of player, moves to the direction player moves to (stays right below the eyes)
    public static class Tie extends Neckwear {

        public Tie(String name, RARITY rarity) {
            super(name, 50, 30, rarity);
        }

        @Override
        protected void setCoordinates() {
            double baseY = player.getY()+player.getHeight()/2;
            double baseX = player.getX();
            double xShift = player.getWidth()/2 - player.getWidth()*2/5;
            double yShift = player.getHeight() / 8;

            int xDir = player.getXDirection();
            int yDir = player.getYDirection();

            double x = baseX + xDir*xShift;
            double y = baseY + yDir*yShift;

            setX(x);
            setY(y);
        }

    }

    //Stays on top of the player, moves to the left side when going right.
    public static class Pin extends Brooch {

        public Pin(String name, RARITY rarity) {
            super(name, 10, 10, rarity);
        }

        @Override
        protected void setCoordinates() {

            double baseX = player.getX();
            double xShift = player.getWidth() / 2 - player.getWidth() / 5;
            int multiplier = (player.getXDirection() == -1) ? 1 : -1;
            double x = baseX + multiplier*xShift;

            double y = player.getY() + player.getHeight() / 4;

            setX(x);
            setY(y);
        }

    }

    //Stays on top of the player, moves to the left side when going right.
    public static class Headpiece extends Headwear {

        public Headpiece(String name, RARITY rarity) {
            super(name, 20, 20, rarity);
        }

        @Override
        protected void setCoordinates() {
            double baseX = player.getX();
            double xShift = player.getWidth() / 2;
            int multiplier = (player.getXDirection() == 1) ? -1 : 1;
            double x = baseX + multiplier * xShift;
            double y = player.getY() - 2 * player.getHeight() / 5;

            setX(x);
            setY(y);
        }

    }

}

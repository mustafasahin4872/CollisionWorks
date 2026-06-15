package mapobjects.mapobject;

import mapobjects.category.MapObject;

import static helpers.HelperMethods.getDirectionString;
import static mapobjects.category.GridObject.TILE_SIDE;

// different accessories are placed on different locations on player.
// therefore, we created many subclasses, each having their unique setCoordinates() function
// also, each accessory has 9 images, and their names change depending on the player's direction.
public abstract class Accessory extends MapObject {

    protected Player player;
    private final String accessoryName;
    private final double defaultWidth, defaultHeight;
    private boolean alone;

    public Accessory(String accessoryName, double defaultWidth, double defaultHeight) {
        super(0, 0, 0, defaultWidth, defaultHeight, accessoryName+"/0");
        this.player = new Player(); // replaced later
        this.accessoryName = accessoryName;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
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

    //------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------

    //Stays on the middle top of the player, does not change as player moves.
    public static class Hat extends Accessory {
        public Hat(String name) {
            super(name, 65, 25);
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
    public static class Necklace extends Accessory {

        public Necklace(String name) {
            super(name, 50, 25);
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
    public static class Tie extends Accessory {

        public Tie(String name) {
            super(name, 50, 30);
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
    public static class Pin extends Accessory {

        public Pin(String name) {
            super(name, 10, 10);
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
    public static class Headpiece extends Accessory {

        public Headpiece(String name) {
            super(name, 20, 20);
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

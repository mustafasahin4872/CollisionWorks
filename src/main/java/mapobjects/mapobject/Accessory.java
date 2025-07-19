package mapobjects.mapobject;

import mapobjects.category.MapObject;

import static mapobjects.category.GridObject.TILE_SIDE;

public abstract class Accessory extends MapObject {

    protected Player player;
    protected String accessoryName;

    //there can exist 9 different coordinates depending on the player's direction.
    //some accessories have 3 coordinates depending on left, right, stationary; some have only 1.
    //we cannot enter all coordinates in the constructor, so there should be some default alignments
    //of accessories in the form of subclasses
    //add new alignments as needed(as subclasses)
    protected final double[][] coordinates = new double[9][2]; //with respect to player

    protected final double defaultWidth, defaultHeight;


    public Accessory(String accessoryName, Player player) {
        this(accessoryName, player.getWidth(), player.getHeight(), player);
    }

    public Accessory(String accessoryName, double defaultWidth, double defaultHeight, Player player) {
        super(player.getWorldIndex(), player.getX(), player.getY(), defaultWidth, defaultHeight, accessoryName+"/0");
        this.player = player;
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

    public void resetSize() {
        setWidth(defaultWidth);
        setHeight(defaultHeight);
    }

    public String getAccessoryName() {
        return accessoryName;
    }


    //sets coordinates using player's position. different coordinates for each accessory type
    protected abstract void setCoordinates();

    //updates position using coordinates. Calls setCoordinates and updates the fileName
    public abstract void update();


    public void drawBig(double multiplier) {
        resize(multiplier);
        resize(player.getDefaultSide()/(TILE_SIDE));
        player.resize(multiplier);
        update();
        draw();
        resetSize();
        player.resetSize();
    }


    //classes that have specific placements on the player

    //Stays on the middle top of the player. Have only 1 image, does not change as player moves.
    public static class Hat extends Accessory {
        public Hat(String name, double defaultWidth, double defaultHeight, Player player) {
            super(name, defaultWidth, defaultHeight, player);
        }
        public Hat(String name, Player player) {
            super(name, player);
        }

        @Override
        protected void setCoordinates() {
            coordinates[0] = new double[]{player.getX(), player.getY()-(2*player.getHeight()/5)-getHeight()/2};
        }

        @Override
        public void update() {
            setCoordinates();
            setX(coordinates[0][0]);
            setY(coordinates[0][1]);
        }

    }

    //Stays in the center of the player. Have only 1 image, does not change as player moves.
    public static class Necklace extends Accessory {
        public Necklace(String name, Player player) {
            super(name, player);
        }

        public Necklace(String name, double defaultWidth, double defaultHeight, Player player) {
            super(name, defaultWidth, defaultHeight, player);
        }

        @Override
        protected void setCoordinates() {
            coordinates[0] = new double[]{player.getX(), player.getY()};
        }

        @Override
        public void update() {
            setCoordinates();
            setX(coordinates[0][0]);
            setY(coordinates[0][1]);
        }

    }


    //Stays on top of the player. Have 3 images, moves to left side when going right.
    public static class Headpiece extends Accessory {
        public Headpiece(String name, Player player) {
            super(name, player);
        }

        public Headpiece(String name, double defaultWidth, double defaultHeight, Player player) {
            super(name, defaultWidth, defaultHeight, player);
        }


        @Override
        protected void setCoordinates() {
            coordinates[0] = new double[]{player.getX()+player.getWidth()/2, player.getY()-2*player.getHeight()/5};
            coordinates[1] = new double[]{player.getX()+player.getWidth()/2, player.getY()-2*player.getHeight()/5};
            coordinates[2] = new double[]{player.getX()-player.getWidth()/2, player.getY()-2*player.getHeight()/5};
        }

        @Override
        public void update() {
            setCoordinates();
            int index = player.getXDirection() + 1;
            setX(coordinates[index][0]);
            setY(coordinates[index][1]);
            setName(accessoryName + "/" + switch (index) {
                case 0 -> "L";
                case 1 -> "0";
                default -> "R";
            });
        }
    }


    //Stays in the center of the player. Have 3 images, moves to left side when going right.
    public static class Pin extends Accessory {
        public Pin(String name, Player player) {
            super(name, player);
        }

        public Pin(String name, double defaultWidth, double defaultHeight, Player player) {
            super(name, defaultWidth, defaultHeight, player);
        }

        @Override
        protected void setCoordinates() {
            coordinates[0] = new double[]{player.getX()+player.getWidth()/2, player.getY()};
            coordinates[1] = new double[]{player.getX()+player.getWidth()/2, player.getY()};
            coordinates[2] = new double[]{player.getX()-player.getWidth()/2, player.getY()};
        }

        @Override
        public void update() {
            setCoordinates();
            int index = player.getXDirection() + 1;
            setX(coordinates[index][0]);
            setY(coordinates[index][1]);
            setName(accessoryName + "/" + switch (index) {
                case 0 -> "L";
                case 1 -> "0";
                default -> "R";
            });
        }


    }
}

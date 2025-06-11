package game;

import lib.StdDraw;

import mapobjects.initialized.Tile;

public abstract class Accessory {

    protected final String name;
    protected final String[] fileNames = new String[9];
    protected Player player;

    //there can exist 9 different coordinates depending on the player's direction.
    //some accessories have 3 coordinates depending on left, right, stationary some have only 1.
    //we cannot enter all coordinates in the constructor, so there should be some default alignments of accessories
    //add new alignments as needed(as subclasses)

    protected final double[][] coordinates = new double[9][2]; //with respect to player!
    protected final double defaultWidth, defaultHeight;
    protected double width, height;


    public Accessory(String name, Player player) {
        this(name, player.getSide(), player.getSide(), player);
    }

    public Accessory(String name, double defaultWidth, double defaultHeight, Player player) {
        this.name = name;
        this.player = player;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        width = defaultWidth;
        height = defaultHeight;
        setCoordinates();
        setFileNames();
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    protected abstract void setCoordinates();

    protected abstract void setFileNames();
    
    public abstract void draw();

    public void drawBig(double multiplier) {
        resize(multiplier);
        resize(player.getDefaultSide()/(Tile.HALF_SIDE*2));
        player.resize(multiplier);
        setCoordinates();
        draw();
        resetSize();
        player.resetSide();
    }

    public void resize(double multiplier) {
        width*=multiplier;
        height*=multiplier;
    }

    public void resetSize() {
        width = defaultWidth;
        height = defaultHeight;
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

        protected void setFileNames() {
            fileNames[0] = "misc/accessoryImages/Hats/%s/U.png".formatted(name);
        }

        @Override
        protected void setCoordinates() {
            coordinates[0] = new double[]{player.getX(), player.getY()-(2*player.getSide()/5)-height/2};
        }

        @Override
        public void draw() {
            StdDraw.picture(coordinates[0][0], coordinates[0][1], fileNames[0], width, height);
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

        protected void setFileNames() {
            fileNames[0] = "misc/accessoryImages/Necklaces/%s/U.png".formatted(name);
        }

        @Override
        protected void setCoordinates() {
            coordinates[0] = new double[]{player.getX(), player.getY()};
        }

        @Override
        public void draw() {
            StdDraw.picture(coordinates[0][0], coordinates[0][1], fileNames[0], width, height);
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

        protected void setFileNames() {
            fileNames[0] = "misc/accessoryImages/Headpieces/%s/UL.png".formatted(name);
            fileNames[1] = "misc/accessoryImages/Headpieces/%s/U.png".formatted(name);
            fileNames[2] = "misc/accessoryImages/Headpieces/%s/UR.png".formatted(name);
        }


        @Override
        protected void setCoordinates() {
            coordinates[0] = new double[]{player.getX()+player.getSide()/2, player.getY()-2*player.getSide()/5};
            coordinates[1] = new double[]{player.getX()+player.getSide()/2, player.getY()-2*player.getSide()/5};
            coordinates[2] = new double[]{player.getX()-player.getSide()/2, player.getY()-2*player.getSide()/5};
        }

        @Override
        public void draw() {
            int i = switch (player.getXDirection()) {
                case -1 -> 0;
                case 0 -> 1;
                case 1 -> 2;
                default -> {
                    System.out.println("default error message for drawing Headpiece");
                    yield 1;
                }
            };
            StdDraw.picture(coordinates[i][0], coordinates[i][1], fileNames[i], width, height);
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

        protected void setFileNames() {
            fileNames[0] = "misc/accessoryImages/Pins/%s/L.png".formatted(name);
            fileNames[1] = "misc/accessoryImages/Pins/%s/0.png".formatted(name);
            fileNames[2] = "misc/accessoryImages/Pins/%s/R.png".formatted(name);
        }


        @Override
        protected void setCoordinates() {
            coordinates[0] = new double[]{player.getX()+player.getSide()/2, player.getY()};
            coordinates[1] = new double[]{player.getX()+player.getSide()/2, player.getY()};
            coordinates[2] = new double[]{player.getX()-player.getSide()/2, player.getY()};
        }

        @Override
        public void draw() {
            int i = switch (player.getXDirection()) {
                case -1 -> 0;
                case 0 -> 1;
                case 1 -> 2;
                default -> {
                    System.out.println("default error message for drawing Pin");
                    yield 1;
                }
            };
            StdDraw.picture(coordinates[i][0], coordinates[i][1], fileNames[i], width, height);
        }


    }
}

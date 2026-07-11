package mapobjects.traits;

import game.io.Drawer;

public interface Drawable {

    Drawer getDrawer();

    default void draw() {getDrawer().draw();}

    default void drawBigAt(double x, double y, double multiplier) {
        double oldX = getDrawer().getX();
        double oldY = getDrawer().getY();

        getDrawer().setX(x);
        getDrawer().setY(y);

        double width = getDrawer().getWidth();
        double height = getDrawer().getHeight();
        getDrawer().resize(multiplier);
        draw();
        getDrawer().setWidth(width);
        getDrawer().setHeight(height);

        getDrawer().setX(oldX);
        getDrawer().setY(oldY);
    }

    default String getDirectory1() {
        String fullName = this.getClass().getName();  // e.g. mapobjects.entities.Chest.WoodenChest
        // Remove the package prefix first
        String prefix = "mapobjects.entities.";
        if (!fullName.startsWith(prefix)) {
            throw new IllegalStateException("Unexpected package: " + fullName);
        }
        String remainder = fullName.substring(prefix.length()).toLowerCase();  // e.g. "Chest.WoodenChest"
        String directory;
        if (remainder.contains("$")) {
            String[] parts = remainder.split("\\$"); // split on dot
            directory = parts[0] + "/" + parts[1] + "/";
        } else {
            // No subclass, just classname only
            directory = remainder + "/";
        }
        return directory;
    }

}

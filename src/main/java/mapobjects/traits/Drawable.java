package mapobjects.traits;

import mapobjects.components.Drawer;

public interface Drawable {

    Drawer getDrawer();

    default void draw() {getDrawer().draw();}

}

package mapobjects.traits;

import helpers.utils.Drawer;

public interface Drawable {

    Drawer getDrawer();

    default void draw1() {getDrawer().draw1();}

}

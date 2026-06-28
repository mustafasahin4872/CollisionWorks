package mapobjects.category;

import mapobjects.component.Drawer;

public interface Drawable {

    Drawer getDrawer();

    default void draw() {getDrawer().draw();}

}

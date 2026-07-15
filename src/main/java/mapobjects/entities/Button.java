package mapobjects.entities;

import game.io.Drawer;
import game.io.Drawer.OutlinedBoxDrawer;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.Moving;
import mapobjects.traits.triggerables.OnTriggerable;

import java.awt.*;
import java.util.Set;

public abstract class Button extends GridObject implements OnTriggerable, Drawable {

    private boolean pressed;
    private Set<Moving> triggerers;

    private static final Color PRESSED_COLOR = new Color(106, 192, 45),
            FRAME_COLOR = new Color(226, 125, 125);

    private final OutlinedBoxDrawer drawer = new OutlinedBoxDrawer(positionBox, Color.BLACK, FRAME_COLOR);

    public Button(int worldIndex, int xNum, int yNum, double width, double height, boolean cornerAligned) {
        super(worldIndex, xNum, yNum, width, height, cornerAligned);
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setUnpressedColor(Color unpressedColor) {
        drawer.setBoxColor(unpressedColor);
    }

    public void press() {
        pressed = true;
        drawer.setBoxColor(PRESSED_COLOR);
    }

    @Override
    public Set<Moving> getTriggerers() {
        return triggerers;
    }

    @Override
    public void setTriggerers(Set<Moving> triggerers) {
        this.triggerers = triggerers;
    }

    @Override
    public void action(Moving moving) {
        press();
    }

    @Override
    public Drawer getDrawer() {
        return drawer;
    }


    public static class LittleButton extends Button {
        public LittleButton(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, 1, 1, false);
        }
    }

    public static class BigButton extends Button {
        public BigButton(int worldIndex, int xNum, int yNum) {
            super(worldIndex, xNum, yNum, 2, 2, true);
        }
    }

}

package mapobjects.entities;

import game.io.Drawer;
import game.io.Drawer.OutlinedBoxDrawer;
import mapobjects.traits.collisions.Movable;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.collisions.Moving;
import mapobjects.traits.triggerables.MovedOverTriggerable;

import mapobjects.components.Trigger;
import java.awt.*;
import java.util.Set;

public abstract class Button extends GridObject implements MovedOverTriggerable, Drawable {

    private boolean pressed;
    private final Trigger<Movable> pressTrigger;

    private static final Color PRESSED_COLOR = new Color(106, 192, 45),
            FRAME_COLOR = new Color(226, 125, 125);

    private final OutlinedBoxDrawer drawer = new OutlinedBoxDrawer(positionBox, Color.BLACK, FRAME_COLOR);

    public Button(int worldIndex, int xNum, int yNum, double width, double height, boolean cornerAligned) {
        super(worldIndex, xNum, yNum, width, height, cornerAligned);
        pressTrigger = new Trigger<>(positionBox, this::triggerPress);
    }

    private void triggerPress(Movable movable) {
        press();
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
    public Trigger<Movable> getMovedOverTrigger() {
        return pressTrigger;
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

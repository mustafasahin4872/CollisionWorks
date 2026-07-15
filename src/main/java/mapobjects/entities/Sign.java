package mapobjects.entities;

import game.io.Drawer.PictureDrawer;
import game.ui.components.TextDisplay;
import java.awt.*;
import java.util.Set;

import game.io.Frame;
import mapobjects.components.Box;
import game.io.Drawer.OutlinedBoxDrawer;
import mapobjects.traits.schemas.Drawable;
import mapobjects.traits.schemas.GridObject;
import mapobjects.traits.Moving;
import mapobjects.traits.triggerables.OnTriggerable;

import static helpers.TextMethods.*;

public class Sign extends GridObject implements OnTriggerable, Drawable {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 32);
    private static final Color COLOR = new Color(103, 2, 9);

    private final boolean displaySign;
    private boolean displayMessage;

    private final TextDisplay textDisplay;
    private final OutlinedBoxDrawer displayDrawer;
    private final PictureDrawer drawer;

    private Set<Moving> triggerers;

    public Sign(int worldIndex, int xNum, int yNum, String[] messages) {
        this(worldIndex, xNum, yNum, messages, true);
    }

    public Sign(int worldIndex, int xNum, int yNum, String[] messages, boolean displaySign) {
        super(worldIndex, xNum, yNum);

        this.displaySign = displaySign;

        double[] dimensions = calculateDimensions(messages);
        Box displayBox = new Box(Frame.X_SCALE / 2, 0.3 * Frame.Y_SCALE / 2, dimensions[0], dimensions[1]);
        textDisplay = new TextDisplay(displayBox, messages, Color.BLACK, FONT);

        displayDrawer = new OutlinedBoxDrawer(textDisplay.getDisplayBox(), COLOR);
        drawer = new PictureDrawer(positionBox, getDirectory1());

    }


    private static double[] calculateDimensions(String[] messages) {
        double maxLength = 0;
        for (String message : messages) {
            maxLength = Math.max(maxLength, getTextWidth(message, FONT));
        }
        double charHeight = getFontHeight(FONT);

        double width = maxLength;
        double height = messages.length * charHeight;

        return new double[]{width, height};
    }

    public void setDisplay() {
        displayMessage = true;
    }

    @Override
    public void call(Player player) {
        if (displayMessage) {
            textDisplay.update();
        }
    }

    // unused
    @Override
    public PictureDrawer getDrawer() {
        return new PictureDrawer(new Box(0, 0, 0, 0), "");
    }

    @Override
    public void draw() {
        if (displaySign) {
            drawer.draw();
        }
        if (displayMessage) {
            displayDrawer.draw();
            textDisplay.draw();
            displayMessage = false;
        }
    }


    @Override
    public Set<Moving> getTriggerers() {
        return triggerers;
    }

    @Override
    public void setTriggerers(Set<Moving> triggerers) {
        boolean safe = true;
        if (triggerers.size() != 1) {
            System.out.println("ONE PLAYER CAN TRIGGER SIGNS PER DEVICE");
            safe = false;
        } else {
            for (Moving moving : triggerers) {
                if (!(moving instanceof Player)) {
                    System.out.println("NON-PLAYER ENTITY CANNOT TRIGGER SIGN");
                    safe = false;
                }
            }
        }
        if (safe) {
            this.triggerers = triggerers;
        } else {
            System.out.println("UNSAFE CONFIG: WILL CRASH");
        }
    }

    @Override
    public void action(Moving moving) {
        setDisplay();
    }

}

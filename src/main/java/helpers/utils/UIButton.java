package helpers.utils;

import game.core.GameState;
import mapobjects.components.Box;
import game.io.InputHandler.*;
import mapobjects.components.Timer;

import java.util.HashSet;
import java.util.Set;

import static helpers.methods.CollisionMethods.isIn;

public abstract class UIButton {

    protected abstract boolean triggered(MouseData mouseData, ArrowData arrowData);
    protected abstract void action();

    public void processInput(MouseData mouseData, ArrowData arrowData) {

        if (triggered(mouseData, arrowData)) {
            action();
        }
    }

    public static class StateButton extends UIButton {

        private final Box BOX;
        private final GameState.STATE nextState;
        private final GameState gameState;

        public StateButton(Box BOX, GameState gameState, GameState.STATE nextState) {
            this.BOX = BOX;
            this.gameState = gameState;
            this.nextState = nextState;
        }

        @Override
        public boolean triggered(MouseData mouseData, ArrowData arrowData) {
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean clicked = mouseData.clicked;

            return clicked && isIn(mouseX, mouseY, BOX);
        }

        @Override
        public void action() {
            gameState.setState(nextState);
        }
    }

    public static class IndexButton extends UIButton {

        public enum TYPE {INCREMENT, SELECT, DECREMENT}

        private final Box BOX;
        private final Index index;
        private final TYPE type;

        public IndexButton(Box BOX, Index index, TYPE type) {
            this.BOX = BOX;
            this.index = index;
            this.type = type;
        }

        @Override
        public boolean triggered(MouseData mouseData, ArrowData arrowData) {
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean clicked = mouseData.clicked;

            return clicked && isIn(mouseX, mouseY, BOX);
        }

        @Override
        public void action() {
            switch (type) {
                case DECREMENT -> index.decrement();
                case INCREMENT -> index.increment();
                case SELECT -> index.select();
            }
        }

    }

    /// wire keys with index shifts for an Index object
    public static class GenericIndexKey extends UIButton {

        public enum GenericKey {
            RIGHT_ARROW,
            LEFT_ARROW,
            DOWN_ARROW,
            UP_ARROW,
            SPACE
        }

        private final GenericKey key;
        private final Index index;
        private final int indexShift; // can be positive or negative!

        private static final double COOLDOWN = 200; // in milliseconds
        private final Timer cooldown = new Timer(Long.MAX_VALUE, COOLDOWN);

        public GenericIndexKey(GenericKey key, Index index, int indexShift) {
            this.key = key;
            this.index = index;
            this.indexShift = indexShift;
            cooldown.activate();
        }

        @Override
        protected boolean triggered(MouseData mouseData, ArrowData arrowData) {

            if (cooldown.inCooldown()) {
                cooldown.tick();
                return false;
            }

            boolean result = switch (key) {
                case RIGHT_ARROW -> arrowData.xDirection == Direction.RIGHT;
                case LEFT_ARROW -> arrowData.xDirection == Direction.LEFT;
                case UP_ARROW -> arrowData.yDirection == Direction.UP;
                case DOWN_ARROW -> arrowData.yDirection == Direction.DOWN;
                case SPACE -> arrowData.space;
                case null, default -> false;
            };

            if (result) cooldown.startCooldown();
            return result;

        }

        @Override
        protected void action() {
            index.add(indexShift);
        }

    }

    public static class BooleanButton extends UIButton {
        private final Box BOX;
        private boolean pressed = false;
        private final Set<BooleanButton> linkedButtons = new HashSet<>();

        public BooleanButton(Box BOX) {
            this.BOX = BOX;
        }

        public void linkButton(BooleanButton button) {
            linkedButtons.add(button);
        }

        @Override
        public boolean triggered(MouseData mouseData, ArrowData arrowData) {
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean clicked = mouseData.clicked;

            return clicked && isIn(mouseX, mouseY, BOX);
        }

        @Override
        public void action() {
            pressed = !pressed;
            for (BooleanButton button : linkedButtons) button.reset();
        }

        public boolean isPressed() {
            return pressed;
        }

        public void reset() {
            pressed = false;
        }

    }

}

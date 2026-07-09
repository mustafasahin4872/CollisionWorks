package helpers;

import game.GameState;
import mapobjects.component.Box;
import helpers.InputHandler.*;
import mapobjects.component.Timer;

import java.util.HashSet;
import java.util.Set;

import static helpers.CollisionMethods.isIn;

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

    public static class Index {

        private int i = 0;
        private int select = 0;
        private int N;

        public Index(int N) {
            this.N = N;
        }

        public void setN(int n) {
            N = n;
        }

        public void increment(int n) {
            if (n < 0) {
                System.out.println("INCREMENT OPERATION USED WITH NEGATIVE VALUE");
                return;
            }
            i+=n;
            i%=N;
        }

        public void increment() {
            increment(1);
        }

        public void decrement(int n) {
            if (n < 0) {
                System.out.println("DECREMENT OPERATION USED WITH NEGATIVE VALUE");
                return;
            }
            i-=n;
            i%=N;
            i+=N;
            i%=N;
        }

        public void decrement() {
            decrement(1);
        }

        public void setValue(int i) {
            this.i = i;
        }

        public int getCurrent() {
            return i;
        }

        public int getSelect() {
            return select;
        }

        public void select() {
            if (select == i) select = 0;
            else select = i;
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

    public static class ArrowKey extends UIButton {

        public enum TYPE {INCREMENT, DECREMENT}

        private final Index index;
        private final TYPE type;

        private static final double COOLDOWN = 200; // in milliseconds
        private final Timer arrowCooldown = new Timer(Long.MAX_VALUE, COOLDOWN);

        public ArrowKey(Index index, TYPE type) {
            this.index = index;
            this.type = type;
            arrowCooldown.activate();
        }

        @Override
        public boolean triggered(MouseData mouseData, ArrowData arrowData) {

            if (arrowCooldown.inCooldown()) {
                arrowCooldown.tick();
                return false;
            }

            int dir = arrowData.xDirection;
            boolean result = switch (type) {
                case INCREMENT -> dir == 1;
                case DECREMENT -> dir == -1;
                case null -> {
                    System.out.println("ERROR: ARROWKEY TYPE IS NULL");
                    yield  false;
                }
            };
            if (result) arrowCooldown.startCooldown();
            return result;
        }

        @Override
        public void action() {
            switch (type) {
                case INCREMENT -> index.increment();
                case DECREMENT -> index.decrement();
            }
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

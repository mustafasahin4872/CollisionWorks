package game;

import helpers.InputHandler.MouseData;
import game.GameState.STATE;
import helpers.FrameBox;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.component.HPBar;
import mapobjects.mapobject.Player;

import java.awt.*;

import static game.Main.IMAGES_ROOT;
import static helpers.CollisionMethods.isIn;
import static helpers.DrawMethods.*;
import static helpers.FrameBox.*;

// the drawing and input handling of buttons and stats on in-game screen
public class GameScreen {

    private final GameState gameState;
    private final PauseButton pauseButton = new PauseButton();
    private final PauseScreen pauseScreen = new PauseScreen();
    private final DeadScreen deadScreen = new DeadScreen();
    private final HealthBar healthBar = new HealthBar();
    private final CoinAmount coinAmount = new CoinAmount();
    private final GemAmount gemAmount = new GemAmount();
    private final LifeAmount lifeAmount = new LifeAmount();
    private final AmmoBar ammoBar = new AmmoBar();
    private final CriticalHealthEffect criticalHealthEffect = new CriticalHealthEffect();

    public GameScreen(GameState gameState) {
        this.gameState = gameState;
    }

    // processes all input that is needed for in-game ui
    public void processInput(MouseData mouseData) {
        pauseButton.processInput(mouseData);
        pauseScreen.processInput(mouseData);
        deadScreen.processInput(mouseData);
    }

    // updates all the values needed in draw functions.
    public void updateValues(double frameX, double frameY) {
        FrameBox.updateCenter(frameX, frameY);

        Player player = gameState.getPlayer();
        HPBar hpBar = player.getHealthBar();
        int coinsCollected = gameState.getCollectedCoins();
        int gemsCollected = gameState.getCollectedGems();

        pauseButton.update();
        pauseScreen.update();
        deadScreen.update();

        healthBar.update(hpBar.getMaxHP(), hpBar.getHP());
        coinAmount.update(coinsCollected);
        gemAmount.update(gemsCollected);
        lifeAmount.update(hpBar.getLives());
        ammoBar.update(hpBar.getMaxHP(), player.getAmmo());
        criticalHealthEffect.update(hpBar.getRemainingHPPercentage());

    }

    // DRAW
    public void draw() {
        STATE state = gameState.getState();

        criticalHealthEffect.draw();
        healthBar.draw();
        coinAmount.draw();
        gemAmount.draw();
        lifeAmount.draw();
        ammoBar.draw();
        pauseButton.draw();

        if (state == STATE.PAUSE) {
            pauseScreen.draw();
        }

        if (gameState.getState() == STATE.DEAD) {
            deadScreen.draw();
        }
    }

    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    // UI CLASSES - INPUT TAKING
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------

    private class PauseButton {

        private static final double DISTANCE = 30;
        private static final double SIDE = 40;

        private static final FrameBox PAUSE_BUTTON = new FrameBox(2*CENTER_X - DISTANCE, DISTANCE, SIDE, SIDE);

        private void update() {
            PAUSE_BUTTON.update();
        }

        private void processInput(MouseData mouseData) {
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean pressed = mouseData.pressed;

            if (!pressed) return;

            if (gameState.getState() == STATE.GAME) {
                // check for pause button clicks
                if (isIn(mouseX, mouseY, PAUSE_BUTTON.getFrameBox())) {
                    gameState.setState(STATE.PAUSE);
                }
            }

        }

        private void draw() {
            Box pauseBox = PAUSE_BUTTON.getFrameBox();
            StdDraw.picture(pauseBox.getCenterX(), pauseBox.getCenterY(), IMAGES_ROOT+"ui/pause.png", pauseBox.getWidth(), pauseBox.getHeight());
        }

    }

    private class PauseScreen {
        private static final double DISTANCE = 30;
        private static final double BUTTON_HEIGHT = 60;
        private static final double SCREEN_SIDE = 3*BUTTON_HEIGHT + 4*DISTANCE;

        private static final FrameBox PAUSE_SCREEN = new FrameBox(CENTER_X, CENTER_Y, SCREEN_SIDE, SCREEN_SIDE);
        private static final FrameBox RESUME_BUTTON = new FrameBox(CENTER_X, CENTER_Y-BUTTON_HEIGHT- DISTANCE, SCREEN_SIDE-2* DISTANCE, BUTTON_HEIGHT);
        private static final FrameBox RESTART_BUTTON = new FrameBox(CENTER_X, CENTER_Y, SCREEN_SIDE-2* DISTANCE, BUTTON_HEIGHT);
        private static final FrameBox EXIT_BUTTON = new FrameBox(CENTER_X, CENTER_Y+BUTTON_HEIGHT+ DISTANCE, SCREEN_SIDE-2* DISTANCE, BUTTON_HEIGHT);

        private static final FrameBox[] BOXES = {PAUSE_SCREEN, RESUME_BUTTON, EXIT_BUTTON, RESTART_BUTTON};

        private void update() {
            for (FrameBox frameBox : BOXES) {
                frameBox.update();
            }
        }

        private void processInput(MouseData mouseData) {
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean pressed = mouseData.pressed;

            if (!pressed) return;

            if (gameState.getState() == STATE.PAUSE) {
                if (isIn(mouseX, mouseY, RESUME_BUTTON.getFrameBox())) {
                    gameState.continueGame();
                }
                if (isIn(mouseX, mouseY, EXIT_BUTTON.getFrameBox())) {
                    gameState.exitGame();
                }
                if (isIn(mouseX, mouseY, RESTART_BUTTON.getFrameBox())) {
                    gameState.restartGame();
                }
            }

        }

        private void draw() {
            // draw pause screen: resume, restart, exit
            Color background = new Color(16, 78, 6);
            Color buttons = new Color(23, 148, 9);
            Color outline1 = Color.BLACK;
            Color outline2 = Color.WHITE;
            int fontSize = 30;
            drawRectWithOutline(PAUSE_SCREEN.getFrameBox(), background, outline1);
            drawRectWithOutline(RESUME_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(RESUME_BUTTON.getFrameBox(), "RESUME", outline2, fontSize);
            drawRectWithOutline(RESTART_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(RESTART_BUTTON.getFrameBox(), "RESTART", outline2, fontSize);
            drawRectWithOutline(EXIT_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(EXIT_BUTTON.getFrameBox(), "EXIT", outline2, fontSize);

        }

    }

    private class DeadScreen {
        private static final double BUTTON_HEIGHT = 60;
        private static final double DISTANCE = 30;
        private static final double SCREEN_HEIGHT = 2*BUTTON_HEIGHT + 3*DISTANCE;
        private static final double SCREEN_WIDTH = 300;

        private static final FrameBox DEAD_SCREEN = new FrameBox(CENTER_X, CENTER_Y, SCREEN_WIDTH, SCREEN_HEIGHT);
        private static final FrameBox RESTART_BUTTON = new FrameBox(CENTER_X, CENTER_Y - DISTANCE/2 - BUTTON_HEIGHT/2, SCREEN_WIDTH-2* DISTANCE, BUTTON_HEIGHT);
        private static final FrameBox EXIT_BUTTON = new FrameBox(CENTER_X, CENTER_Y + DISTANCE/2 + BUTTON_HEIGHT/2, SCREEN_WIDTH-2* DISTANCE, BUTTON_HEIGHT);
        private static final FrameBox YOU_DIED = new FrameBox(CENTER_X, CENTER_Y - SCREEN_HEIGHT/2 - DISTANCE*2, SCREEN_WIDTH, BUTTON_HEIGHT);

        private static final FrameBox[] BOXES = {EXIT_BUTTON, RESTART_BUTTON, DEAD_SCREEN, YOU_DIED};

        private void update() {
            for (FrameBox frameBox : BOXES) {
                frameBox.update();
            }
        }

        private void processInput(MouseData mouseData) {
            double mouseX = mouseData.mouseX;
            double mouseY = mouseData.mouseY;
            boolean pressed = mouseData.pressed;

            if (!pressed) return;

            if (gameState.getState() == STATE.DEAD) { // STATE.PAUSE
                if (isIn(mouseX, mouseY, EXIT_BUTTON.getFrameBox())) {
                    gameState.exitGame();
                }
                if (isIn(mouseX, mouseY, RESTART_BUTTON.getFrameBox())) {
                    gameState.restartGame();
                }
            }

        }

        private void draw() {

            Color background = new Color(137, 10, 10);
            Color buttons = new Color(202, 60, 60);
            Color outline1 = Color.WHITE;
            Color outline2 = Color.BLACK;
            int fontSize = 30;
            drawRectWithOutline(DEAD_SCREEN.getFrameBox(), background, outline1);
            drawRectWithOutline(RESTART_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(RESTART_BUTTON.getFrameBox(), "RESTART", outline2, fontSize);
            drawRectWithOutline(EXIT_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(EXIT_BUTTON.getFrameBox(), "EXIT", outline2, fontSize);
            drawRectWithOutline(YOU_DIED.getFrameBox(), buttons, outline2);
            textInsideBox(YOU_DIED.getFrameBox(), "YOU DIED!", outline2, fontSize);
        }

    }

    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    // UI CLASSES - NON INPUT TAKING
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------

    private static class HealthBar {
        private static final double THICKNESS = 2;
        private static final double HALFHEIGHT = 12;
        private static final double DISTANCE = 15;

        private final FrameBox outBox = new FrameBox(0, 0, 0, 0);
        private final FrameBox inBox = new FrameBox(0, 0, 0, 0);

        private void update(double maxHp, double hp) {
            outBox.setCenterX(DISTANCE + (maxHp/2.0 + THICKNESS));
            outBox.setCenterY(DISTANCE + (HALFHEIGHT + THICKNESS));
            outBox.setWidth(maxHp + 2*THICKNESS);
            outBox.setHeight(HALFHEIGHT*2 + 2*THICKNESS);

            inBox.setCenterX(DISTANCE + (hp/2.0 + THICKNESS));
            inBox.setCenterY(DISTANCE + (HALFHEIGHT + THICKNESS));
            inBox.setWidth(hp);
            inBox.setHeight(HALFHEIGHT*2);

            outBox.update();
            inBox.update();
        }

        private void draw() {
            StdDraw.setPenColor(StdDraw.BLACK);
            drawRectangle(outBox.getFrameBox());

            //the hp inside the outline
            StdDraw.setPenColor(StdDraw.GREEN);
            drawRectangle(inBox.getFrameBox());
        }

    }

    private static class CoinAmount {

        private static final double SIDE = 40;
        private static final double DISTANCE = 30;

        private final FrameBox frameBox = new FrameBox(2*CENTER_X - 2*SIDE, DISTANCE, SIDE, SIDE);
        private int coinsCollected;
        private String fileName = IMAGES_ROOT+"coin/singlecoin/0.png";

        private void update(int coinsCollected) {
            frameBox.update();
            this.coinsCollected = coinsCollected;

            if (this.coinsCollected >= 10) fileName = IMAGES_ROOT+"coin/triplecoin/0.png";
            if (this.coinsCollected >= 50) fileName = IMAGES_ROOT+"coin/coinbag/0.png";
        }

        private void draw() {
            Box box = frameBox.getFrameBox();
            StdDraw.picture(box.getCenterX(), box.getCenterY(), fileName, SIDE, SIDE);
            textInsideBox(box, "%d".formatted(coinsCollected));
        }

    }

    private static class GemAmount {

        private static final double SIDE = 40;
        private static final double DISTANCE = 30;

        private final FrameBox frameBox = new FrameBox(2*CENTER_X - 3*SIDE, DISTANCE, SIDE, SIDE);
        private int gemsCollected;

        private void update(int gemsCollected) {
            frameBox.update();
            this.gemsCollected = gemsCollected;
        }

        private void draw() {
            Box box = frameBox.getFrameBox();
            String fileName = IMAGES_ROOT + "ui/gem.png";
            StdDraw.picture(box.getCenterX(), box.getCenterY(), fileName, SIDE, SIDE);
            textInsideBox(box, "%d".formatted(gemsCollected));
        }

    }

    private static class LifeAmount {

        private static final double SIDE = 40;
        private static final double DISTANCE = 30;

        private final FrameBox frameBox = new FrameBox(2*CENTER_X - 4*SIDE, DISTANCE, SIDE, SIDE);
        private int lives;

        private void update(int lives) {
            frameBox.update();
            this.lives = lives;
        }

        private void draw() {
            Box box = frameBox.getFrameBox();
            StdDraw.picture(box.getCenterX(), box.getCenterY(), IMAGES_ROOT+"ui/heart.png", SIDE, SIDE);
            textInsideBox(box, "%d".formatted(lives));
        }

    }

    private static class AmmoBar {

        private static final double HALFHEIGHT = 7;
        private static final int DISTANCE = 15;
        private int ammo;

        private final FrameBox frameBox = new FrameBox(0, DISTANCE + 16, HALFHEIGHT*4, HALFHEIGHT*2);

        private void update(double maxHp, int ammo) {
            this.ammo = ammo;
            frameBox.setCenterX(DISTANCE + maxHp + 4*HALFHEIGHT);
            frameBox.update();
        }

        private void draw() {
            Box box = frameBox.getFrameBox();
            for (int i = 0; i<ammo; i++) {
                StdDraw.picture(box.getCenterX() + HALFHEIGHT*4*i, box.getCenterY(), IMAGES_ROOT+"projectile/regularprojectile/0.png", HALFHEIGHT*4, HALFHEIGHT*2, 45);
            }
        }

    }

    private static class CriticalHealthEffect {

        private static final double HALF_THICKNESS = 5;
        private static final int MAX_RECT_COUNT = 10;
        private double hpPercantage;

        private final FrameBox frameBox = new FrameBox(CENTER_X, CENTER_Y, 2*CENTER_X, 2*CENTER_Y);

        private void update(double hpPercantage) {
            this.hpPercantage = hpPercantage;
            frameBox.update();
        }

        private void draw() {
            Box box = frameBox.getFrameBox();
            double centerX = box.getCenterX();
            double centerY = box.getCenterY();
            int rectangleCount = (int) (MAX_RECT_COUNT - hpPercantage/3);

            for (int i = 0; i < rectangleCount; i++) {

                double fadeFactor = 1 - (i / (double) rectangleCount); // linear fade
                int alpha = (int) (fadeFactor * 255);
                Color color = new Color(123, 9, 9, alpha);
                StdDraw.setPenColor(color);

                // LEFT side
                StdDraw.filledRectangle(centerX - CENTER_X + (2 * i + 1) * HALF_THICKNESS, centerY, HALF_THICKNESS, CENTER_Y);

                // RIGHT side
                StdDraw.filledRectangle(centerX + CENTER_X - (2 * i + 1) * HALF_THICKNESS, centerY, HALF_THICKNESS, CENTER_Y);

                // BOTTOM side
                StdDraw.filledRectangle(centerX, centerY + CENTER_Y - (2 * i + 1) * HALF_THICKNESS, CENTER_X, HALF_THICKNESS);

                // TOP side
                StdDraw.filledRectangle(centerX, centerY - CENTER_Y + (2 * i + 1) * HALF_THICKNESS, CENTER_X, HALF_THICKNESS);
            }

        }


    }

}

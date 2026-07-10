package game.ui;

import game.core.GameState;
import game.io.InputHandler.MouseData;
import game.core.GameState.STATE;
import helpers.utils.FrameBox;
import lib.StdDraw;
import mapobjects.components.Box;
import mapobjects.components.HPBar;
import mapobjects.entities.Player;

import java.awt.*;

import static game.core.Main.IMAGES_ROOT;
import static helpers.methods.CollisionMethods.isIn;
import static helpers.methods.DrawMethods.*;
import static helpers.utils.FrameBox.*;

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
        ammoBar.update(player.getAmmo());
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
            boolean clicked = mouseData.clicked;

            if (!clicked) return;

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
            boolean clicked = mouseData.clicked;

            if (!clicked) return;

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
            Font font = new Font("Arial", Font.PLAIN, 30);
            drawRectWithOutline(PAUSE_SCREEN.getFrameBox(), background, outline1);
            drawRectWithOutline(RESUME_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(RESUME_BUTTON.getFrameBox(), "RESUME", outline2, font);
            drawRectWithOutline(RESTART_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(RESTART_BUTTON.getFrameBox(), "RESTART", outline2, font);
            drawRectWithOutline(EXIT_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(EXIT_BUTTON.getFrameBox(), "EXIT", outline2, font);

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
            boolean clicked = mouseData.clicked;

            if (!clicked) return;

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
            Font font = new Font("Arial", Font.PLAIN, 30);
            drawRectWithOutline(DEAD_SCREEN.getFrameBox(), background, outline1);
            drawRectWithOutline(RESTART_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(RESTART_BUTTON.getFrameBox(), "RESTART", outline2, font);
            drawRectWithOutline(EXIT_BUTTON.getFrameBox(), buttons, outline2);
            textInsideBox(EXIT_BUTTON.getFrameBox(), "EXIT", outline2, font);
            drawRectWithOutline(YOU_DIED.getFrameBox(), buttons, outline2);
            textInsideBox(YOU_DIED.getFrameBox(), "YOU DIED!", outline2, font);
        }

    }

    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    // UI CLASSES - NON INPUT TAKING
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------

    private static class HealthBar {

        private static final double THICKNESS = 2;
        private static final double DISTANCE = 15;

        private static final double HEIGHT = 28;
        private static final double WIDTH = 200;

        private static final double IN_HEIGHT = HEIGHT - 2 * THICKNESS;
        private static final double IN_WIDTH = WIDTH - 2 * THICKNESS;

        private final FrameBox outBox = new FrameBox(DISTANCE + WIDTH/2, DISTANCE + HEIGHT/2, WIDTH, HEIGHT);
        private final FrameBox inBox = new FrameBox(0, DISTANCE + THICKNESS + IN_HEIGHT/2, IN_WIDTH, IN_HEIGHT);

        private double hp, maxHp;

        private void update(double maxHp, double hp) {
            this.hp = hp;
            this.maxHp = maxHp;
            double width = IN_WIDTH * (hp / maxHp);

            inBox.setCenterX(DISTANCE + THICKNESS + width/2);
            inBox.setWidth(width);

            outBox.update();
            inBox.update();
        }

        private void draw() {
            StdDraw.setPenColor(StdDraw.BLACK);
            drawRectangle(outBox.getFrameBox());

            //the hp inside the outline
            StdDraw.setPenColor(StdDraw.GREEN);
            drawRectangle(inBox.getFrameBox());

            Font font = new Font("Ariel", Font.BOLD, (int)HEIGHT/2);
            String health = "%d/%d".formatted((int)hp, (int)maxHp);
            textInsideBox(outBox.getFrameBox(), health, StdDraw.WHITE, font);

        }

    }

    private static class CoinAmount {

        private static final double SIDE = 40;
        private static final double DISTANCE = 30;

        private final FrameBox frameBox = new FrameBox(2*CENTER_X - 2*SIDE, DISTANCE, SIDE, SIDE);
        private int coinsCollected;
        private String fileName = IMAGES_ROOT+"currency/singleCoin.png";

        private void update(int coinsCollected) {
            frameBox.update();
            this.coinsCollected = coinsCollected;

            if (this.coinsCollected >= 10) fileName = IMAGES_ROOT+"currency/tripleCoin.png";
            if (this.coinsCollected >= 50) fileName = IMAGES_ROOT+"currency/coinBag.png";
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

        private static final int ANGLE = 45;
        private static final double RADIANS = (ANGLE / 360.0) * 2 * Math.PI;
        private static final double SINE = Math.sin(RADIANS);
        private static final double COSINE = Math.cos(RADIANS);

        private static final double DISTANCE = 14; // distance from HealthBar
        private static final double HEIGHT = 14, WIDTH = 28; // one ammo's dimensions
        private static final double EFF_W = Math.abs(WIDTH * COSINE) + Math.abs(HEIGHT * SINE);
        private static final double EFF_H = Math.abs(WIDTH * SINE) + Math.abs(HEIGHT * COSINE);

        private int ammo;
        private static final int MAX_AMMO_DRAWN = 10;

        private final FrameBox firstAmmoBox = new FrameBox(
            HealthBar.DISTANCE + HealthBar.WIDTH + DISTANCE + EFF_W / 2,
            HealthBar.DISTANCE + EFF_H / 2,
            EFF_W, EFF_H);
        private final FrameBox lastAmmoBox = new FrameBox(
            HealthBar.DISTANCE + HealthBar.WIDTH + DISTANCE + EFF_W / 2 + (MAX_AMMO_DRAWN - 1) * EFF_W,
            HealthBar.DISTANCE + EFF_H / 2,
            EFF_W, EFF_H);

        private void update(int ammo) {
            this.ammo = ammo;
            firstAmmoBox.update();
            lastAmmoBox.update();
        }

        private void draw() {
            Box box = firstAmmoBox.getFrameBox();
            int ammoDrawn = Math.min(ammo, MAX_AMMO_DRAWN);

            for (int i = 0; i<ammoDrawn; i++) {
                    StdDraw.picture(box.getCenterX() + EFF_W*i, box.getCenterY(), IMAGES_ROOT+"ui/projectile.png", WIDTH, HEIGHT, 45);
            }
            if (ammo>MAX_AMMO_DRAWN) { // does not display +0 or +1, starts from +2.
                Font font = new Font("Ariel", Font.BOLD, (int)EFF_H/2);
                textInsideBox(lastAmmoBox.getFrameBox(), "+" + (ammo - MAX_AMMO_DRAWN + 1), StdDraw.BLACK, font);
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

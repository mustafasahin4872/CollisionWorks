package game.ui;

import game.core.GameState;
import game.io.InputHandler.MouseData;
import game.core.GameState.STATE;
import game.ui.components.FrameBox;
import mapobjects.components.Box;
import game.io.Drawer.BoxDrawer;
import game.io.Drawer.TextDrawer;
import game.io.Drawer.ClassicButtonDrawer;
import game.io.Drawer.OutlinedBoxDrawer;
import game.io.Drawer.PictureDrawer;
import mapobjects.components.HPBar;
import mapobjects.entities.Player;

import java.awt.*;

import static helpers.CollisionEngine.isIn;
import static game.ui.components.FrameBox.*;

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

        private final PictureDrawer drawer = new PictureDrawer(PAUSE_BUTTON.getFrameBox(), "ui/", "pause");

        private void draw() {
            drawer.draw();
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
        private static final Color BACKGROUND_COLOR = new Color(16, 78, 6);
        private static final Color BUTTON_COLOR = new Color(23, 148, 9);
        private static final Font FONT = new Font("Arial", Font.PLAIN, 30);
        private static final OutlinedBoxDrawer PAUSE_DRAWER = new OutlinedBoxDrawer(PAUSE_SCREEN.getFrameBox(), BACKGROUND_COLOR, Color.BLACK);
        private static final ClassicButtonDrawer RESUME_DRAWER = new ClassicButtonDrawer(RESUME_BUTTON.getFrameBox(), BUTTON_COLOR, Color.WHITE, "RESUME", Color.WHITE, FONT);
        private static final ClassicButtonDrawer RESTART_DRAWER = new ClassicButtonDrawer(RESTART_BUTTON.getFrameBox(), BUTTON_COLOR, Color.WHITE, "RESTART", Color.WHITE, FONT);
        private static final ClassicButtonDrawer EXIT_DRAWER = new ClassicButtonDrawer(EXIT_BUTTON.getFrameBox(), BUTTON_COLOR, Color.WHITE, "EXIT", Color.WHITE, FONT);


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
            PAUSE_DRAWER.draw();
            RESUME_DRAWER.draw();
            RESTART_DRAWER.draw();
            EXIT_DRAWER.draw();
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

        private static final Color BACKGROUND_COLOR = new Color(137, 10, 10);
        private static final Color BUTTON_COLOR = new Color(202, 60, 60);
        private static final Font FONT = new Font("Arial", Font.PLAIN, 30);

        private static final OutlinedBoxDrawer DEAD_DRAWER = new OutlinedBoxDrawer(DEAD_SCREEN.getFrameBox(), BACKGROUND_COLOR, Color.WHITE);
        private static final ClassicButtonDrawer RESTART_DRAWER = new ClassicButtonDrawer(RESTART_BUTTON.getFrameBox(), BUTTON_COLOR, "RESTART", Color.BLACK, FONT);
        private static final ClassicButtonDrawer EXIT_DRAWER = new ClassicButtonDrawer(EXIT_BUTTON.getFrameBox(), BUTTON_COLOR, "EXIT", Color.BLACK, FONT);
        private static final ClassicButtonDrawer YOU_DIED_DRAWER = new ClassicButtonDrawer(YOU_DIED.getFrameBox(), BUTTON_COLOR, Color.WHITE, "YOU DIED!", Color.BLACK, FONT);

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
            DEAD_DRAWER.draw();
            RESTART_DRAWER.draw();
            EXIT_DRAWER.draw();
            YOU_DIED_DRAWER.draw();
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

        private final BoxDrawer inDrawer = new BoxDrawer(inBox.getFrameBox(), Color.GREEN);
        private final BoxDrawer outDrawer = new BoxDrawer(outBox.getFrameBox(), Color.BLACK);
        private final TextDrawer healthTextDrawer = new TextDrawer(outBox.getFrameBox(), Color.WHITE, new Font("Ariel", Font.BOLD, (int)HEIGHT/2));

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
            outDrawer.draw();

            inDrawer.draw();

            String health = "%d/%d".formatted((int)hp, (int)maxHp);
            healthTextDrawer.setText(health);
            healthTextDrawer.draw();

        }

    }

    private static class CoinAmount {

        private static final double SIDE = 40;
        private static final double DISTANCE = 30;

        private final FrameBox frameBox = new FrameBox(2*CENTER_X - 2*SIDE, DISTANCE, SIDE, SIDE);
        private int coinsCollected;
        private PictureDrawer drawer = new PictureDrawer(frameBox.getFrameBox(), "currency/", "singleCoin");

        private final TextDrawer textDrawer = new TextDrawer(frameBox.getFrameBox());

        private void update(int coinsCollected) {
            frameBox.update();
            this.coinsCollected = coinsCollected;

            if (this.coinsCollected >= 50) {
                drawer = new PictureDrawer(frameBox.getFrameBox(), "currency/", "coinBag");
            } else if (this.coinsCollected >= 10) {
                drawer = new PictureDrawer(frameBox.getFrameBox(), "currency/", "tripleCoin");
            } else {
                drawer = new PictureDrawer(frameBox.getFrameBox(), "currency/", "singleCoin");
            }
        }

        private void draw() {
            drawer.draw();
            textDrawer.setText("%d".formatted(coinsCollected));
            textDrawer.draw();
        }

    }

    private static class GemAmount {

        private static final double SIDE = 40;
        private static final double DISTANCE = 30;

        private final FrameBox frameBox = new FrameBox(2*CENTER_X - 3*SIDE, DISTANCE, SIDE, SIDE);
        private int gemsCollected;
        private PictureDrawer drawer = new PictureDrawer(frameBox.getFrameBox(), "ui/", "gem");

        private final TextDrawer textDrawer = new TextDrawer(frameBox.getFrameBox());

        private void update(int gemsCollected) {
            frameBox.update();
            this.gemsCollected = gemsCollected;

            if (this.gemsCollected >= 50) {
                drawer = new PictureDrawer(frameBox.getFrameBox(), "currency/", "gemBag");
            } else if (this.gemsCollected >= 10) {
                drawer = new PictureDrawer(frameBox.getFrameBox(), "currency/", "tripleGem");
            } else {
                drawer = new PictureDrawer(frameBox.getFrameBox(), "ui/", "gem");
            }
        }

        private void draw() {
            drawer.draw();
            textDrawer.setText("%d".formatted(gemsCollected));
            textDrawer.draw();
        }

    }

    private static class LifeAmount {

        private static final double SIDE = 40;
        private static final double DISTANCE = 30;

        private final FrameBox frameBox = new FrameBox(2*CENTER_X - 4*SIDE, DISTANCE, SIDE, SIDE);
        private int lives;

        private final TextDrawer textDrawer = new TextDrawer(frameBox.getFrameBox());

        private void update(int lives) {
            frameBox.update();
            this.lives = lives;
        }

        private final PictureDrawer drawer = new PictureDrawer(frameBox.getFrameBox(), "ui/", "heart");

        private void draw() {
            drawer.draw();
            textDrawer.setText("%d".formatted(lives));
            textDrawer.draw();
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
        private final TextDrawer extraAmmoTextDrawer = new TextDrawer(lastAmmoBox.getFrameBox(), Color.BLACK, new Font("Ariel", Font.BOLD, (int)EFF_H/2));

        private final PictureDrawer[] ammoDrawers = new PictureDrawer[MAX_AMMO_DRAWN];

        {
            for (int i = 0; i<MAX_AMMO_DRAWN; i++) {
                Box projectileBox = new Box(0, 0, WIDTH, HEIGHT);
                ammoDrawers[i] = new PictureDrawer(projectileBox, "ui/", "projectile");
                ammoDrawers[i].setDegrees(45);
            }
        }

        private void update(int ammo) {
            this.ammo = ammo;
            firstAmmoBox.update();
            lastAmmoBox.update();
        }

        private void draw() {
            Box box = firstAmmoBox.getFrameBox();
            int ammoDrawn = Math.min(ammo, MAX_AMMO_DRAWN);

            for (int i = 0; i<ammoDrawn; i++) {
                ammoDrawers[i].setX(box.getCenterX() + EFF_W*i);
                ammoDrawers[i].setY(box.getCenterY());
                ammoDrawers[i].draw();
            }
            if (ammo>MAX_AMMO_DRAWN) { // does not display +0 or +1, starts from +2.
                extraAmmoTextDrawer.setText("+" + (ammo - MAX_AMMO_DRAWN + 1));
                extraAmmoTextDrawer.draw();
            }

        }

    }

    private static class CriticalHealthEffect {

        private static final double HALF_THICKNESS = 5;
        private static final int MAX_RECT_COUNT = 10;
        private double hpPercantage;

        private final FrameBox frameBox = new FrameBox(CENTER_X, CENTER_Y, 2*CENTER_X, 2*CENTER_Y);

        private final BoxDrawer[] leftDrawers = new BoxDrawer[MAX_RECT_COUNT];
        private final BoxDrawer[] rightDrawers = new BoxDrawer[MAX_RECT_COUNT];
        private final BoxDrawer[] bottomDrawers = new BoxDrawer[MAX_RECT_COUNT];
        private final BoxDrawer[] topDrawers = new BoxDrawer[MAX_RECT_COUNT];

        {
            for (int i = 0; i < MAX_RECT_COUNT; i++) {
                leftDrawers[i] = new BoxDrawer(new Box(0, 0, 2 * HALF_THICKNESS, 2 * CENTER_Y), Color.BLACK);
                rightDrawers[i] = new BoxDrawer(new Box(0, 0, 2 * HALF_THICKNESS, 2 * CENTER_Y), Color.BLACK);
                bottomDrawers[i] = new BoxDrawer(new Box(0, 0, 2 * CENTER_X, 2 * HALF_THICKNESS), Color.BLACK);
                topDrawers[i] = new BoxDrawer(new Box(0, 0, 2 * CENTER_X, 2 * HALF_THICKNESS), Color.BLACK);
            }
        }

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

                // LEFT side
                leftDrawers[i].setBoxColor(color);
                leftDrawers[i].setX(centerX - CENTER_X + (2 * i + 1) * HALF_THICKNESS);
                leftDrawers[i].setY(centerY);
                leftDrawers[i].draw();

                // RIGHT side
                rightDrawers[i].setBoxColor(color);
                rightDrawers[i].setX(centerX + CENTER_X - (2 * i + 1) * HALF_THICKNESS);
                rightDrawers[i].setY(centerY);
                rightDrawers[i].draw();

                // BOTTOM side
                bottomDrawers[i].setBoxColor(color);
                bottomDrawers[i].setX(centerX);
                bottomDrawers[i].setY(centerY + CENTER_Y - (2 * i + 1) * HALF_THICKNESS);
                bottomDrawers[i].draw();

                // TOP side
                topDrawers[i].setBoxColor(color);
                topDrawers[i].setX(centerX);
                topDrawers[i].setY(centerY - CENTER_Y + (2 * i + 1) * HALF_THICKNESS);
                topDrawers[i].draw();
            }

        }


    }

}

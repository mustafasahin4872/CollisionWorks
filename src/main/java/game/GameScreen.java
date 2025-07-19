package game;

import game.InputHandler.MouseData;
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
import static helpers.FrameBox.CENTER_X;
import static helpers.FrameBox.CENTER_Y;

// the drawing and input handling of buttons and stats on in-game screen
public class GameScreen {

    private final GameState gameState;
    private final PauseScreen pauseScreen;
    private final DeadScreen deadScreen;
    private final HealthBar healthBar;

    public GameScreen(GameState gameState) {
        this.gameState = gameState;
        pauseScreen = new PauseScreen();
        deadScreen = new DeadScreen();
        healthBar = new HealthBar();
    }

    // processes all input that is done in in-game ui
    public void processInput(MouseData mouseData) {
        pauseScreen.processInput(mouseData);
        deadScreen.processInput(mouseData);
    }

    // updates all the values needed in draw functions.
    public void update() {
        pauseScreen.update();
        deadScreen.update();
        healthBar.updateCenter();
        HPBar hpBar = gameState.player.getHealthBar();
        healthBar.updateHp(hpBar.getMaxHP(), hpBar.getHP());
    }

    // DRAW

    public void draw(double frameX, double frameY) {
        Player player = gameState.player;
        HPBar hpBar = player.getHealthBar();

        drawCriticalHealthEffect(frameX, frameY, hpBar);
        healthBar.draw();
        drawAmmo(frameX, frameY, hpBar, player.getAmmo());
        drawCoinAmount(frameX, frameY, player.getCoinsCollected());
        drawLifeAmount(frameX, frameY, hpBar);
        // TODO: move state checking here for pause screen!
        // that requires splitting pause button and screen!
        pauseScreen.draw();
        if (gameState.getState() == STATE.DEAD) deadScreen.draw();
    }


    private class PauseScreen {
        private static final double DISTANCE = 30;
        private static final double BUTTON_HEIGHT = 60;
        private static final double SCREEN_SIDE = 3*BUTTON_HEIGHT + 4*DISTANCE;
        private static final double PAUSE_SIDE = 40;

        private static final FrameBox PAUSE_BUTTON = new FrameBox(2*CENTER_X - DISTANCE, DISTANCE, PAUSE_SIDE, PAUSE_SIDE);
        private static final FrameBox PAUSE_SCREEN = new FrameBox(CENTER_X, CENTER_Y, SCREEN_SIDE, SCREEN_SIDE);
        private static final FrameBox RESUME_BUTTON = new FrameBox(CENTER_X, CENTER_Y-BUTTON_HEIGHT- DISTANCE, SCREEN_SIDE-2* DISTANCE, BUTTON_HEIGHT);
        private static final FrameBox RESTART_BUTTON = new FrameBox(CENTER_X, CENTER_Y, SCREEN_SIDE-2* DISTANCE, BUTTON_HEIGHT);
        private static final FrameBox EXIT_BUTTON = new FrameBox(CENTER_X, CENTER_Y+BUTTON_HEIGHT+ DISTANCE, SCREEN_SIDE-2* DISTANCE, BUTTON_HEIGHT);

        private static final FrameBox[] BOXES = {PAUSE_BUTTON, PAUSE_SCREEN, RESUME_BUTTON, EXIT_BUTTON, RESTART_BUTTON};

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

            if (gameState.getState() == STATE.GAME) {
                // check for pause button clicks
                if (isIn(mouseX, mouseY, PAUSE_BUTTON.getFrameBox())) {
                    gameState.setState(STATE.PAUSE);
                }
            } else { // STATE.PAUSE
                if (isIn(mouseX, mouseY, RESUME_BUTTON.getFrameBox())) {
                    gameState.setState(STATE.GAME);
                }
                if (isIn(mouseX, mouseY, EXIT_BUTTON.getFrameBox())) {
                    gameState.setState(STATE.SELECTION);
                }
                if (isIn(mouseX, mouseY, RESTART_BUTTON.getFrameBox())) {
                    gameState.restart();
                }
            }

        }

        private void draw() {
            // draw pause button
            Box pauseBox = PAUSE_BUTTON.getFrameBox();
            StdDraw.picture(pauseBox.getCenterX(), pauseBox.getCenterY(), IMAGES_ROOT+"ui/pause.png", pauseBox.getWidth(), pauseBox.getHeight());

            if (gameState.getState() == STATE.PAUSE) {
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

    }

    private class DeadScreen {
        private static final double BUTTON_HEIGHT = 60;
        private static final double DISTANCE = 30;
        private static final double SCREEN_HEIGHT = 2*BUTTON_HEIGHT + 3*DISTANCE;
        private static final double SCREEN_WIDTH = 300;

        private static final FrameBox DEAD_SCREEN = new FrameBox(CENTER_X, CENTER_Y, SCREEN_WIDTH, SCREEN_HEIGHT);
        private static final FrameBox RESTART_BUTTON = new FrameBox(CENTER_X, CENTER_Y - DISTANCE/2 - BUTTON_HEIGHT/2, SCREEN_WIDTH-2* DISTANCE, BUTTON_HEIGHT);
        private static final FrameBox EXIT_BUTTON = new FrameBox(CENTER_X, CENTER_Y + DISTANCE/2 + BUTTON_HEIGHT/2, SCREEN_WIDTH-2* DISTANCE, BUTTON_HEIGHT);

        private static final FrameBox[] BOXES = {EXIT_BUTTON, RESTART_BUTTON, DEAD_SCREEN};

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
                    gameState.setState(STATE.SELECTION);
                }
                if (isIn(mouseX, mouseY, RESTART_BUTTON.getFrameBox())) {
                    gameState.restart();
                }
            }

        }

        private void draw() {
            //TODO: write YOU DIED!!! to the top

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
        }

    }

    private static class HealthBar {
        private static final double THICKNESS = 2;
        private static final double HALFHEIGHT = 12;
        private static final double DISTANCE = 15;

        private final FrameBox outBox, inBox;

        public HealthBar() {
            outBox = new FrameBox(0, 0, 0, 0);
            inBox = new FrameBox(0, 0, 0, 0);
        }

        private void updateHp(double maxHp, double hp) {
            outBox.setCenterX(DISTANCE + (maxHp/2.0 + THICKNESS));
            outBox.setCenterY(DISTANCE + (HALFHEIGHT + THICKNESS));
            outBox.setWidth(maxHp + 2*THICKNESS);
            outBox.setHeight(HALFHEIGHT*2 + 2*THICKNESS);

            inBox.setCenterX(DISTANCE + (hp/2.0 + THICKNESS));
            inBox.setCenterY(DISTANCE + (HALFHEIGHT + THICKNESS));
            inBox.setWidth(hp);
            inBox.setHeight(HALFHEIGHT*2);
        }

        private void updateCenter() {
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

    // unused code, previous version of health bar mechanic.
    private void drawHPBar(HPBar hpBar) {

        //the outline of hp bar
        double maxHp = hpBar.getMaxHP();
        double hp = hpBar.getHP();
        double thickness = 2;
        double halfHeight = 12;
        double height = 24;
        int distance = 15;

        FrameBox outBox = new FrameBox(
            distance + (maxHp/2.0 + thickness),
            distance + (halfHeight + thickness),
            maxHp + 2*thickness, height + 2*thickness);
        FrameBox inBox = new FrameBox(
            distance + (hp/2.0 + thickness),
            distance + (halfHeight + thickness),
            hp, height
        );

        outBox.update();
        inBox.update();

        StdDraw.setPenColor(StdDraw.BLACK);
        drawRectangle(outBox.getFrameBox());

        //the hp inside the outline
        StdDraw.setPenColor(StdDraw.GREEN);
        drawRectangle(inBox.getFrameBox());


    }

    private void drawCoinAmount(double frameX, double frameY, int coinsCollected) {
        StdDraw.picture(frameX + game.Frame.X_SCALE/2.0 - 80, frameY - game.Frame.Y_SCALE/2.0 + 30, IMAGES_ROOT+"coin/singlecoin/0.png", 40, 40);
        StdDraw.setFont(); StdDraw.setPenColor();
        StdDraw.text(frameX + game.Frame.X_SCALE/2.0 - 80, frameY - game.Frame.Y_SCALE/2.0 + 30, "%d".formatted(coinsCollected));
    }

    private void drawLifeAmount(double frameX, double frameY, HPBar hpBar) {
        int side = 40;
        StdDraw.picture(frameX + Frame.X_SCALE/2.0 - 130, frameY - Frame.Y_SCALE/2.0 + 30, IMAGES_ROOT+"misc/heart.png", side, side);
        StdDraw.setFont(); StdDraw.setPenColor();
        StdDraw.text(frameX + Frame.X_SCALE/2.0 - 130, frameY - Frame.Y_SCALE/2.0 + 30, "%d".formatted(hpBar.getLives()));
    }

    private void drawAmmo(double frameX, double frameY, HPBar hpBar, int ammo) {
        double halfHeight = 7;
        int distance = 15;
        double baseX = frameX - Frame.X_SCALE/2.0 + distance + hpBar.getMaxHP() + 4*halfHeight;
        double baseY = frameY - Frame.Y_SCALE / 2.0 + distance + 16;
        for (int i = 0; i<ammo; i++) {
            StdDraw.picture(baseX + halfHeight*4*i, baseY, IMAGES_ROOT+"projectile/regularprojectile/0.png", halfHeight*4, halfHeight*2, 45);
        }
    }

    private void drawCriticalHealthEffect(double frameX, double frameY, HPBar hpBar) {
        double halfThickness = 5;
        int rectangleCount = (int) (10- hpBar.getRemainingHPPercentage()/3);

        for (int i = 0; i < rectangleCount; i++) {
            double fadeFactor = 1 - (i / (double) rectangleCount); // linear fade
            int alpha = (int) (fadeFactor * 255);
            StdDraw.setPenColor(new Color(123, 9, 9, alpha));

            // LEFT side
            StdDraw.filledRectangle(
                frameX - game.Frame.X_SCALE / 2.0 + (2 * i + 1) * halfThickness,
                frameY,
                halfThickness,
                game.Frame.Y_SCALE / 2.0
            );

            // RIGHT side
            StdDraw.filledRectangle(
                frameX + game.Frame.X_SCALE / 2.0 - (2 * i + 1) * halfThickness,
                frameY,
                halfThickness,
                game.Frame.Y_SCALE / 2.0
            );

            // BOTTOM side
            StdDraw.filledRectangle(
                frameX,
                frameY + game.Frame.Y_SCALE / 2.0 - (2 * i + 1) * halfThickness,
                game.Frame.X_SCALE / 2.0,
                halfThickness
            );

            // TOP side
            StdDraw.filledRectangle(
                frameX,
                frameY - game.Frame.Y_SCALE / 2.0 + (2 * i + 1) * halfThickness,
                Frame.X_SCALE / 2.0,
                halfThickness
            );
        }
    }

}

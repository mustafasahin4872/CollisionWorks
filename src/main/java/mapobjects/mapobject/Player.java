package mapobjects.mapobject;

import game.Frame;
import game.GameState;
import helpers.InputHandler.ArrowData;
import game.Main;
import helpers.MapObjectGenerator;

import helpers.PlayerData;
import mapobjects.component.*;
import mapobjects.category.*;

import java.util.HashSet;
import java.util.Set;

import static helpers.HelperMethods.*;

public class Player extends MapObject implements MovingCollidable, Spawnable, HealthBearer, Timed {

    private final String playerName;
    private final boolean animated;
    private final double defaultSide;

    private final Box collisionBox;
    private final Spawner spawner;
    protected final Set<Projectile> projectiles = new HashSet<>();
    protected final HPBar hpBar;
    private final Timer reloadTimer, shootingCooldown;


    private static final double
            MAX_SPEED1 = 7.5, MAX_SPEED2 = 15, MAX_SPEED3 = 40,
            A1 = 0.1, A2 = 0.4, A3 = 0.8, A4 = 1.6;

    private double spawnX, spawnY, xVelocity, yVelocity, maxSpeed, acceleration, deceleration;
    private int xDirection, yDirection;
    private boolean xCollided, yCollided;

    private boolean shoot;
    private int maxAmmo = 5;
    private int ammo = maxAmmo;
    private static final int MAX_LIVES = 3;

    private int lastCheckPointIndex;

    protected Accessory[] accessories;


    //CONSTRUCTORS

    // TODO: CREATE A DUMMY PLAYER TYPE, ASIDE FROM BOB
    public Player() {
        this("Bob");
    }

    public Player(String playerName) {
        super(0,0, 0, 0, 0, playerName);
        this.playerName = playerName;
        PlayerData playerData = PlayerData.valueOf(playerName);
        this.animated = playerData.isAnimated();
        this.imageType = playerData.getImageType();
        defaultSide = playerData.getDefaultSide();
        setWidth(defaultSide);
        setHeight(defaultSide);

        collisionBox = positionBox.clone();
        spawner = new Spawner(worldIndex, getX(), getY());
        hpBar = new HPBar(defaultSide*4, MAX_LIVES, 0);
        reloadTimer = new Timer(0, 1000);
        shootingCooldown = new Timer(0, 100);
        reloadTimer.activate();
        respawn();
    }


    //UPDATES

    public void call(GridObject[][][] layers) {

        checkDead();

        if (!isComplete()) {timeIsUp(this);}
        if (isActive()) {reload();}
        updateTimer();
        shootingCooldown.tick();

        if (shoot) {
            if (ammo > 0 && !shootingCooldown.isCompleted()) {
                shootingCooldown.activate();
                spawn();
                ammo--;
            }
            shoot = false;
        }

        int range = 2; //the checking range
        int[] gridNumbers = getGridNumbers();
        for (GridObject[][] layer : layers) {
            for (int i = gridNumbers[1]-range; i<gridNumbers[1]+range; i++) {
                for (int j = gridNumbers[0]-range; j<gridNumbers[0]+range; j++) {
                    if (i<0 || j<0 || i>=layer.length || j>=layer[0].length) continue;
                    GridObject currentGridObject = layer[i][j];
                    checkMapObjectEffects(currentGridObject);
                }
            }
        }
        //player's projectiles are not checking player collisions
        for (Projectile projectile : projectiles) {
            projectile.call(new Player(), layers);
        }
        projectiles.removeIf(Projectile::isExpired);
    }

    private void checkMapObjectEffects(GridObject currentGridObject) {
        if (currentGridObject instanceof Collidable c && !(c instanceof Ghost)) {
            c.checkCollision(this);
        } else if (currentGridObject instanceof OnEffector e) {
            e.checkPlayerIsOn(this);
        } else if (currentGridObject instanceof EmptyGridObject e) {
            checkMapObjectEffects(e.getLinkedObject());
        }
    }

    public void update() {

        if (!isXCollided()) {
            setX(getX()+ getXVelocity() * Frame.DT);
        }
        if (!isYCollided()) {
            setY(getY() + getYVelocity() * Frame.DT);
        }
        xCollided = false;
        yCollided = false;

        for (Accessory accessory : accessories) {
            accessory.update();
        }
    }

    public void acceptInput(ArrowData arrowData) {
        xDirection = arrowData.xDirection;
        yDirection = arrowData.yDirection;
        shoot = arrowData.space;
    }

    public void resetInput() {
        xDirection = 0;
        yDirection = 0;
        shoot = false;
    }

    public void updateVelocity() {

        final double DT = Frame.DT;
        final double SQRT2 = Math.sqrt(2);

        double effectiveAccelX = acceleration;
        double effectiveAccelY = acceleration;

        if (xDirection != 0 && Math.signum(xDirection) != Math.signum(xVelocity)) {
            effectiveAccelX += deceleration;
        }

        if (yDirection != 0 && Math.signum(yDirection) != Math.signum(yVelocity)) {
            effectiveAccelY += deceleration;
        }

        double nextVx, nextVy;

        if (xDirection != 0 && yDirection != 0) {
            nextVx = xVelocity + xDirection * effectiveAccelX / SQRT2 * DT;
            nextVy = yVelocity + yDirection * effectiveAccelY / SQRT2 * DT;
        } else {
            nextVx = xVelocity + xDirection * effectiveAccelX * DT;
            nextVy = yVelocity + yDirection * effectiveAccelY * DT;
        }

        double nextVSquared = nextVx * nextVx + nextVy * nextVy;
        double nextSpeed = Math.sqrt(nextVSquared);

        if (nextVSquared > maxSpeed * maxSpeed) {
            double scale = maxSpeed / nextSpeed;
            setXVelocity(nextVx * scale);
            setYVelocity(nextVy * scale);
        } else {
            setXVelocity(nextVx);
            setYVelocity(nextVy);
        }

        // X friction (no input)
        if (xDirection == 0) {
            if ((xVelocity >= 0 && xVelocity - deceleration * DT < 0) ||
                    (xVelocity <= 0 && xVelocity + deceleration * DT > 0)) {
                setXVelocity(0);
            } else {
                setXVelocity(xVelocity + (xVelocity > 0 ? -1 : 1) * deceleration * DT);
            }
        }

        // Y friction (no input)
        if (yDirection == 0) {
            if ((yVelocity >= 0 && yVelocity - deceleration * DT < 0) ||
                    (yVelocity <= 0 && yVelocity + deceleration * DT > 0)) {
                setYVelocity(0);
            } else {
                setYVelocity(yVelocity + (yVelocity > 0 ? -1 : 1) * deceleration * DT);
            }
        }

    }


    //GETTERS, SETTERS

    public String getPlayerName() {
        return playerName;
    }


    //ACCESSORY

    public void setAccessories(Accessory[] accessories) {
        this.accessories = accessories;
        for (Accessory accessory : accessories) {
            accessory.resize(defaultSide/50);
        }
    }


    //SPAWNING PROJECTILES

    @Override
    public MapObject[] getSpawnObjects() {
        return projectiles.toArray(new Projectile[0]);
    }

    @Override
    public void spawn() {
        MapObjectGenerator mapObjectGenerator = spawner.directionSpawn(getNextCenterCoordinates(), 30);
        int direction = ((int) Math.toDegrees(Math.atan2(yVelocity, xVelocity)) + 360) % 360;
        double speed = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
        Projectile projectile = mapObjectGenerator.mutateToRegularProjectile(20, 10, direction, speed + Projectile.DEFAULT_INITIAL_SPEED);
        projectiles.add(projectile);
    }

    @Override
    public Timer getTimer() {
        return reloadTimer;
    }

    @Override
    public void timeIsUp(Player player) {
        activateTimer();
    }

    private void reload() {
        if (ammo+1<=maxAmmo) ammo++;
    }

    public int getAmmo() {
        return ammo;
    }


    //COLLISION

    @Override
    public Box getCollisionBox() {
        return collisionBox;
    }

    @Override
    public void xCollide() {
        xCollided = true;
        xVelocity = 0;
    }

    @Override
    public void yCollide() {
        yCollided = true;
        yVelocity = 0;
    }

    @Override
    public boolean isXCollided() {
        return xCollided;
    }

    @Override
    public boolean isYCollided() {
        return yCollided;
    }


    //MOVEMENTS

    public void slip() {
        setAcceleration(A1);
        setDeceleration(A1);
        setMaxSpeed(MAX_SPEED3);
    }

    public void slow() {
        setAcceleration(A1);
        setDeceleration(A4);
        setMaxSpeed(MAX_SPEED1);
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        collisionBox.setCenterX(x);
        spawner.setCenterX(x);
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        collisionBox.setCenterY(y);
        spawner.setCenterY(y);
    }

    @Override
    public double getXVelocity() {
        return xVelocity;
    }

    @Override
    public double getYVelocity() {
        return yVelocity;
    }

    public void setXVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public void setYVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void resetMaxSpeed() {
        maxSpeed = MAX_SPEED2;
    }

    public void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }

    public void resetDeceleration() {
        deceleration = A3;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public void resetAcceleration() {
        acceleration = A3;
    }


    //DIRECTION VALUES

    public int getXDirection() {
        return xDirection;
    }

    public int getYDirection() {
        return yDirection;
    }

    //SIZE

    public double getDefaultSide() {
        return defaultSide;
    }

    public void resetSize() {
        setWidth(defaultSide);
        setHeight(defaultSide);
    }


    //LIVES AND HP

    @Override
    public HPBar getHealthBar() {
        return hpBar;
    }

    public void respawn() {
        setX(spawnX);
        setY(spawnY);
        xVelocity = 0;
        yVelocity = 0;
        acceleration = 0;
        deceleration = 0;
        ammo = maxAmmo;
        hpBar.revive();
    }

    @Override
    public void ifDied() {
        respawn();
    }

    @Override
    public void ifNoLivesLeft() {
        Main.gameState.setState(GameState.STATE.DEAD);
    }

    public void restart() {
        //TODO: CHECK IF THERE ARE MORE FIELDS THAT NEED RESETTING
        hpBar.restart();
        respawn();
    }

    //CHECKPOINT AND SPAWN POINT
    /// TODO: LAST CHECKPOINT INDEX INFO MUST BELONG TO MAP!!!
    /// SPAWNPOINT SHOULD STILL BELONG TO PLAYER

    public void setSpawnPoint(double[] spawnPoint) {
        spawnX = spawnPoint[0];
        spawnY = spawnPoint[1];
    }

    public void updateLastCheckPointIndex() {
        lastCheckPointIndex++;
    }

    public int getLastCheckPointIndex() {
        return lastCheckPointIndex;
    }

    public void resetLastCheckPointIndex() {
        lastCheckPointIndex = 0;
    }

    //DRAW METHODS

    public void draw() {
        updateName();
        super.draw();
    }

    private void updateName() {
        String base = getPlayerName() + "/" + getDirectionString(getXDirection(), getYDirection());
        String plus = (animated) ? ("_" + getCurrentAnimationNum()) : "";
        setName(base + plus);
    }

    private int getCurrentAnimationNum() {

        int animationNumber = 6;
        int blinkDuration = 300;
        int eyeOpenDuration = 3000;
        int total = blinkDuration + eyeOpenDuration;

        long delta = System.currentTimeMillis() - Main.GAME_START;
        double progress = (delta % total) - eyeOpenDuration;
        if (progress < 0) progress = 0;
        double progressRatio = progress / blinkDuration;

        int steps = 2 * (animationNumber - 1); // steps = 10 for animationNumber = 6
        int currentStep = (int)(progressRatio * steps);

        if (currentStep < animationNumber) {
            return currentStep; // ramping up: 0 to animationNumber - 1
        } else {
            return steps - currentStep; // ramping down
        }
        // 0(cooldown) - 1 - 2 - 3 - 4 - 5 - 4 - 3 - 2 - 1 - 0(cooldown)
    }

    public void drawProjectiles() {
        for (Projectile projectile : projectiles) projectile.draw();
    }

    public void drawAccessories() {
        if (accessories != null) {
            for (Accessory accessory : accessories) {
                accessory.draw();
            }
        }
    }
}

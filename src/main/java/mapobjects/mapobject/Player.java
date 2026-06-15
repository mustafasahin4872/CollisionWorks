package mapobjects.mapobject;

import game.Frame;
import game.GameState;
import helpers.InputHandler.ArrowData;
import game.Main;
import helpers.MapObjectGenerator;

import helpers.PlayerDefaults;
import mapobjects.component.*;
import mapobjects.category.*;

import java.util.HashSet;
import java.util.Set;

import static helpers.HelperMethods.*;

public class Player extends MapObject implements MovingCollidable, Spawnable, HealthBearer {

    // initial fields that are unique to the player type
    private final String playerName;
    private final boolean animated;

    // the base stats of a player
    // they are kept in case of non-permanent buffs are needed to be reverted
    // the permanent buff values are applied to here
    private double baseSide;
    private double baseMaxSpeed;
    private double baseAcceleration;
    private double baseDeceleration;
    private int maxAmmo;

    // positioning, moving and collision logic fields
    private double spawnX, spawnY;
    private double xVelocity, yVelocity;
    private double maxSpeed, acceleration, deceleration;
    private int xDirection, yDirection;
    private boolean xCollided, yCollided;

    // shooting logic fields
    private boolean shoot;
    private int ammo;

    // player's collision, spawn, health, time components
    private final Box collisionBox;
    private final Spawner spawner;
    protected final Set<Projectile> projectiles = new HashSet<>();
    protected final HPBar hpBar;
    private final Timer reloadTimer, shootingCooldown;



    protected Accessory[] accessories;


    //CONSTRUCTORS

    // TODO: CREATE A DUMMY PLAYER TYPE, ASIDE FROM BOB
    public Player() {
        this("Bob");
    }

    public Player(String playerName) {

        super(0,0, 0, 0, 0, playerName);
        this.playerName = playerName;

        PlayerDefaults playerDefaults = PlayerDefaults.valueOf(playerName);

        animated = playerDefaults.isAnimated();
        imageType = playerDefaults.getImageType();

        baseMaxSpeed = playerDefaults.getMaxSpeed();
        baseAcceleration = playerDefaults.getAcceleration();
        baseDeceleration = playerDefaults.getDeceleration();
        baseSide = playerDefaults.getSide();

        setWidth(baseSide);
        setHeight(baseSide);

        int defaultMaxLives = playerDefaults.getMaxLives();
        double defaultMaxHP = playerDefaults.getMaxHP();
        double defaultDef = playerDefaults.getDefence();
        hpBar = new HPBar(defaultMaxHP, defaultMaxLives, defaultDef);

        maxAmmo = playerDefaults.getMaxAmmo();
        ammo = maxAmmo;
        int reloadTime = playerDefaults.getReloadTime();
        int shootCooldown = playerDefaults.getShootCooldown();
        reloadTimer = new Timer(0, reloadTime);
        shootingCooldown = new Timer(0, shootCooldown);
        reloadTimer.activate();

        collisionBox = positionBox.clone();
        spawner = new Spawner(worldIndex, getX(), getY());

        respawn();
    }


    //UPDATES

    public void call(GridObject[][][] layers) {

        checkDead();

        if (!reloadTimer.isCompleted()) {reloadTimer.activate();}
        if (reloadTimer.isActive()) {reload();}
        reloadTimer.tick();
        shootingCooldown.tick();

        if (shoot) {
            if (ammo > 0 && !shootingCooldown.isCompleted()) {
                shootingCooldown.activate();
                spawn();
                ammo--;
                reloadTimer.startCooldown();
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
            accessory.resize(baseSide /50);
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
        double a = 0.12 * baseAcceleration;
        double s = 3 * baseMaxSpeed;
        setAcceleration(a);
        setDeceleration(a);
        setMaxSpeed(s);
    }

    public void slow() {
        double a = 0.12 * baseAcceleration;
        double d = 2 * baseAcceleration;
        double s = 0.5 * baseMaxSpeed;

        setAcceleration(a);
        setDeceleration(d);
        setMaxSpeed(s);
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

    private void setXVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    private void setYVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    private void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void resetMaxSpeed() {
        maxSpeed = baseMaxSpeed;
    }

    private void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }

    public void resetDeceleration() {
        deceleration = baseDeceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public void resetAcceleration() {
        acceleration = baseAcceleration;
    }


    //DIRECTION VALUES

    public int getXDirection() {
        return xDirection;
    }

    public int getYDirection() {
        return yDirection;
    }

    //SIZE

    public double getBaseSide() {
        return baseSide;
    }

    public void resetSize() {
        setWidth(baseSide);
        setHeight(baseSide);
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
        hpBar.resetLives();
        respawn();
    }

    public void setSpawnPoint(double[] spawnPoint) {
        spawnX = spawnPoint[0];
        spawnY = spawnPoint[1];
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

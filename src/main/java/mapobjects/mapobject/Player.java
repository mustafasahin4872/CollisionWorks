package mapobjects.mapobject;

import game.GameState;
import helpers.InputHandler.ArrowData;
import game.Main;
import helpers.MapObjectGenerator;

import mapobjects.component.*;
import mapobjects.category.*;


import java.util.HashSet;
import java.util.Set;

import static helpers.HelperMethods.*;

public abstract class Player extends MapObject implements MovingCollidable, Spawnable, HealthBearer, Timed {

    private final String playerName;

    private final Box collisionBox;
    private final Spawner spawner;
    protected final Set<Projectile> projectiles = new HashSet<>();
    protected final HPBar hpBar;
    private final Timer timer, shootingCooldown;

    private final double defaultSide;

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

    private int coinsCollected = 0;

    protected Accessory[] accessories;


    //CONSTRUCTORS

    public Player() {
        this("Bob");
    }

    public Player(String playerName) {
        this(playerName, "jpg");
    }

    public Player(String playerName, String imageType) {
        super(0,0, 0, getDefaultSide(playerName), getDefaultSide(playerName), playerName, imageType);
        this.playerName = playerName;
        defaultSide = getDefaultSide(playerName);
        collisionBox = positionBox.clone();
        spawner = new Spawner(worldIndex, getX(), getY());
        hpBar = new HPBar(defaultSide*4, MAX_LIVES, 0);
        timer = new Timer(0, 1000);
        shootingCooldown = new Timer(0, 100);
        timer.activate();
        respawn();
    }


    public String getName() {
        return super.name;
    }

    private static double getDefaultSide(String playerName) {
        final double defaultSide;
        defaultSide = switch (playerName) {
            case "Bob", "Mike" -> 50;
            case "Zahit" -> 70;
            default -> 50;
        };
        return defaultSide;
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
            projectile.call(new Player.RegularPlayer(), layers);
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
            setX(getX()+ getXVelocity() * game.Frame.DT);
        }
        if (!isYCollided()) {
            setY(getY() + getYVelocity() * game.Frame.DT);
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

        final double DT = game.Frame.DT;
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
        return timer;
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
        coinsCollected = 0;
        respawn();
    }

    //COIN

    public void collectCoin(int n) {
        coinsCollected += n;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }


    //CHECKPOINT AND SPAWN POINT

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

    public abstract void drawBig(double multiplier);


    public static class RegularPlayer extends Player {

        public RegularPlayer() {
        }

        public RegularPlayer(String playerName) {
            super(playerName);
        }

        public void draw() {

            for (Projectile projectile : projectiles) projectile.draw();

            setName(getPlayerName() + "/" + getDirectionString(getXDirection(), getYDirection()));

            super.draw();

            if (accessories != null) {
                for (Accessory accessory : accessories) {
                    accessory.draw();
                }
            }

        }

        public void drawBig(double multiplier) {
            resize(multiplier);

            setName(getPlayerName() + "/" + getDirectionString(getXDirection(), getYDirection()));

            super.draw();

            resetSize();

            if (accessories != null) {
                for (Accessory accessory : accessories) {
                    accessory.drawBig(multiplier);
                }
            }
        }

    }

    public static class AnimatedPlayer extends Player {

        private final Timer animationTimer;
        private final int animationNumber;

        public AnimatedPlayer(String name) {
            super(name, "png");
            animationTimer = new Timer(300, 3000);
            this.animationNumber = switch (name) {
                case "Mike" -> 6;
                default -> 6;
            };
        }

        @Override
        public void call(GridObject[][][] layers) {
            updateAnimationTimer();
            super.call(layers);
        }

        @Override
        public void draw() {

            for (Projectile projectile : projectiles) projectile.draw();

            int currentAnimationNum = getCurrentAnimationNum();

            setName(getPlayerName() + "/" + getDirectionString(getXDirection(), getYDirection()) + "_" + currentAnimationNum);

            super.draw();

            if (accessories != null) {
                for (Accessory accessory : accessories) {
                    accessory.draw();
                }
            }

        }

        @Override
        public void drawBig(double multiplier) {

            updateAnimationTimer();

            int currentAnimationNum = getCurrentAnimationNum();

            setName(getPlayerName() + "/" + getDirectionString(getXDirection(), getYDirection()) + "_" + currentAnimationNum);

            resize(multiplier);

            super.draw();

            if (accessories != null) {
                for (Accessory accessory : accessories) {
                    accessory.drawBig(multiplier);
                }
            }

            resetSize();
        }


        private int getCurrentAnimationNum() {
            double progressRatio = animationTimer.progressRatio() * 100;
            int steps = 2 * (animationNumber - 1); // e.g., 6 for animationNumber = 4
            double segmentSize = 100.0 / steps;

            int segment = Math.min((int)(progressRatio / segmentSize), steps - 1);

            if (segment < animationNumber) {
                return segment; // ramping up: 0 to animationNumber - 1
            } else {
                return steps - segment; // ramping down
            }
        }

        private void updateAnimationTimer() {
            animationTimer.tick();
            if (!animationTimer.isActive() && !animationTimer.isCompleted()) {
                animationTimer.activate();
            }
        }
    }

}

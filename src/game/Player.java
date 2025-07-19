package game;

import helperobjects.Blueprint;
import lib.StdDraw;
import mapobjects.component.Box;
import mapobjects.component.HPBar;
import mapobjects.component.Spawner;
import mapobjects.component.Timer;
import mapobjects.category.*;
import mapobjects.mapobject.EmptyGridObject;
import mapobjects.mapobject.Projectile;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Player extends MapObject implements MovingCollidable, Spawnable, HealthBearer, Timed {

    private final Box collisionBox;
    private final Spawner spawner;
    private final Set<Projectile> projectiles = new HashSet<>();
    private final HPBar hpBar;
    private final Timer timer, shootingCooldown;

    public enum PASSCODE {
        ZERO, DEAD, NEXT, ALTERNATE1, ALTERNATE2, ALTERNATE3, SHOP
    }
    private PASSCODE passCode = PASSCODE.ZERO;
    private final String name, fileRoot;

    private final double defaultSide;
    private double side;

    private static final double
            MAX_SPEED1 = 7.5, MAX_SPEED2 = 15, MAX_SPEED3 = 40,
            A1 = 0.1, A2 = 0.4, A3 = 0.8, A4 = 1.6;

    private double spawnX, spawnY, xVelocity, yVelocity, maxSpeed, acceleration, deceleration;
    private int xDirection, yDirection;
    private boolean shoot;
    private boolean xCollided, yCollided;

    private final int maxAmmo = 5;
    private int ammo = maxAmmo;

    private int lastCheckPointIndex;

    private char onTileType = ' ';

    private int coinsCollected = 0;
    private long playerTime;
    private final double[] effectTimer = new double[3];

    //there are 5 possible buffs, all are false at first and set true if buffed
    //fast, small, immune, magnetic, eagle eye

    private Accessory[] accessories;


    //CONSTRUCTORS

    public Player() {
        this("Bob");
    }

    public Player(String name) {
        this.name = name;
        fileRoot = "misc/playerImages/" + name + "/";
        defaultSide = switch (name) {
            case "Bob", "Mike" -> 50;
            case "Zahit" -> 70;
            default -> 50;
        };
        setSide(defaultSide);
        collisionBox = positionBox.clone();
        spawner = new Spawner(worldIndex, getX(), getY());
        hpBar = new HPBar(side*4, 3, 0);
        timer = new Timer(0, 1000);
        shootingCooldown = new Timer(0, 100);
        timer.activate();
        respawn();
    }


    //UPDATES

    //prototype collision and on tile effect checker, will simplify call methods on the other object types
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
        if (currentGridObject instanceof Collidable c) {
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
            accessory.setCoordinates();
        }
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


    //GETTERS, SETTERS, RESETTERS

    //NAME

    public String getName() {
        return name;
    }


    //ON TILE TYPE

    public char getOnTileType() {
        return onTileType;
    }

    public void setOnTileType(char onTileType) {
        this.onTileType = onTileType;
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
        Blueprint blueprint = spawner.directionSpawn(getNextCenterCoordinates(), 30);
        int direction = ((int) Math.toDegrees(Math.atan2(yVelocity, xVelocity)) + 360) % 360;
        double speed = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
        Projectile projectile = blueprint.mutateToProjectile(20, 10, direction, speed + Projectile.DEFAULT_INITIAL_SPEED);
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

    //COLLISION

    @Override
    public Box getCollisionBox() {
        return collisionBox;
    }

    public void xCollide() {
        xCollided = true;
        xVelocity = 0;
    }

    public void yCollide() {
        yCollided = true;
        yVelocity = 0;
    }

    public boolean isXCollided() {
        return xCollided;
    }

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

    public double getXVelocity() {
        return xVelocity;
    }

    public void setXVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public double getYVelocity() {
        return yVelocity;
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

    //INPUT

    public int getXDirection() {
        return xDirection;
    }

    public void setXDirection(int xDirection) {
        this.xDirection = xDirection;
    }

    public int getYDirection() {
        return yDirection;
    }

    public void setYDirection(int yDirection) {
        this.yDirection = yDirection;
    }

    public void shoot() {
        shoot = true;
    }

    //SIZE


    public void setSide(double side) {
        this.side = side;
        setWidth(side);
        setHeight(side);
    }

    public double getSide() {
        return side;
    }

    public double getDefaultSide() {
        return defaultSide;
    }

    public void resize(double multiplier) {
        side *= multiplier;
    }

    public void resetSide() {
        side = defaultSide;
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
        passCode = PASSCODE.DEAD;
    }

    public void addLife() {
        hpBar.addLife();
    }

    //BUFFS

    public void buff(char[] buffs, double time) {
        if (buffs==null) return;
        for (char buff : buffs) {
            if (buff == 'F') {
                effectTimer[0] += time;
            } else if (buff == 'S') {
                effectTimer[1] += time;
            } else if (buff == 'I') {
                effectTimer[2] += time;
            }
        }
    }

    public void readBuffs() {
        for (int i = 0; i< effectTimer.length; i++) {
            double timeLeft = effectTimer[i];
            if (timeLeft - (System.currentTimeMillis()-playerTime) < 0) {
                effectTimer[i] = 0;
            } else {
                effectTimer[i] = timeLeft - (System.currentTimeMillis()-playerTime);
            }

            if (timeLeft != 0) {
                if (i == 0) {

                } else if (i == 1) {

                } else if (i == 2) {

                }
            }
        }
    }

    public long getPlayerTime() {
        return playerTime;
    }

    public void setPlayerTime() {
        playerTime = System.currentTimeMillis();
    }


    //COIN

    public void collectCoin(int n) {
        coinsCollected += n;
    }


    //CHECKPOINT AND SPAWN POINT

    public double[] getSpawnPoint() {
        return new double[]{spawnX, spawnY};
    }

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


    //PASSCODE

    public PASSCODE getPassCode() {
        return passCode;
    }

    public void setPassCode(PASSCODE passCode) {
        this.passCode = passCode;
    }

    public void resetPassCode() {
        passCode = PASSCODE.ZERO;
    }


    //DRAW METHODS

    public void draw(double frameX, double frameY) {

        for (Projectile projectile : projectiles) projectile.draw();

        double x = getX(), y = getY();

        if (xDirection == 1 && yDirection == -1) {
            StdDraw.picture(x, y, fileRoot+"UR.jpg", side, side);
        } else if (xDirection == 0 && yDirection == -1) {
            StdDraw.picture(x, y, fileRoot+"U.jpg", side, side);
        } else if (xDirection == -1 && yDirection == -1) {
            StdDraw.picture(x, y, fileRoot+"UL.jpg", side, side);
        } else if (xDirection == -1 && yDirection == 0) {
            StdDraw.picture(x, y, fileRoot+"L.jpg", side, side);
        } else if (xDirection == -1 && yDirection == 1) {
            StdDraw.picture(x, y, fileRoot+"DL.jpg", side, side);
        } else if (xDirection == 0 && yDirection == 1) {
            StdDraw.picture(x, y, fileRoot+"D.jpg", side, side);
        } else if (xDirection == 1 && yDirection == 1) {
            StdDraw.picture(x, y, fileRoot+"DR.jpg", side, side);
        } else if (xDirection == 1 && yDirection == 0){
            StdDraw.picture(x, y, fileRoot+"R.jpg", side, side);
        } else if (xDirection == 0 && yDirection == 0){
            StdDraw.picture(x, y, fileRoot+"0.jpg", side, side);
        } else {
            System.out.println("drawing error for player!");
        }

        if (accessories == null) return;
        for (Accessory accessory : accessories) {
            accessory.draw();
        }

        drawCriticalHealthEffect(frameX, frameY, hpBar);
        drawHPBar(frameX, frameY, 12);
        drawAmmo(frameX, frameY, 7);
        drawCoinAmount(frameX, frameY);
        drawLifeAmount(frameX, frameY);
    }

    public void drawBig(double multiplier) {
        double x = getX(), y = getY();
        double sideResized = side*multiplier;
        if (xDirection == 1 && yDirection == 1) {
            StdDraw.picture(x, y, fileRoot+"UR.jpg", sideResized, sideResized);
        } else if (xDirection == 0 && yDirection == 1) {
            StdDraw.picture(x, y, fileRoot+"U.jpg", sideResized, sideResized);
        } else if (xDirection == -1 && yDirection == 1) {
            StdDraw.picture(x, y, fileRoot+"UL.jpg", sideResized, sideResized);
        } else if (xDirection == -1 && yDirection == 0) {
            StdDraw.picture(x, y, fileRoot+"L.jpg", sideResized, sideResized);
        } else if (xDirection == -1 && yDirection == -1) {
            StdDraw.picture(x, y, fileRoot+"DL.jpg", sideResized, sideResized);
        } else if (xDirection == 0 && yDirection == -1) {
            StdDraw.picture(x, y, fileRoot+"D.jpg", sideResized, sideResized);
        } else if (xDirection == 1 && yDirection == -1) {
            StdDraw.picture(x, y, fileRoot+"DR.jpg", sideResized, sideResized);
        } else if (xDirection == 1 && yDirection == 0){
            StdDraw.picture(x, y, fileRoot+"R.jpg", sideResized, sideResized);
        } else {
            StdDraw.picture(x, y, fileRoot+"0.jpg", sideResized, sideResized);
        }

        if (accessories == null) return;
        for (Accessory accessory : accessories) {
            accessory.drawBig(multiplier);
        }
    }

    public void drawHPBar(double frameX, double frameY, double halfHeight) {
        //the outline of hp bar
        double max_HP = hpBar.getMaxHP();
        double HP = hpBar.getHP();
        double thickness = 2;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(
                frameX - Frame.X_SCALE/2.0 + 15 + (max_HP/2.0 + thickness),
                frameY - Frame.Y_SCALE/2.0 + 15 + (halfHeight + thickness),
                max_HP/2.0 + thickness,
                halfHeight + thickness);

        //the hp inside the outline
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.filledRectangle(
                frameX - Frame.X_SCALE/2.0 + 15 + thickness + HP/2.0,
                frameY - Frame.Y_SCALE/2.0 + 15 + (halfHeight + thickness),
                HP/2.0,
                halfHeight
        );
    }

    public void drawCoinAmount(double frameX, double frameY) {
        StdDraw.picture(frameX + Frame.X_SCALE/2.0 - 30, frameY - Frame.Y_SCALE/2.0 + 30, "misc/coinImages/coin.png", 40, 40);
        StdDraw.setFont(); StdDraw.setPenColor();
        StdDraw.text(frameX + Frame.X_SCALE/2.0 - 30, frameY - Frame.Y_SCALE/2.0 + 30, "%d".formatted(coinsCollected));
    }

    public void drawLifeAmount(double frameX, double frameY) {
        StdDraw.picture(frameX + Frame.X_SCALE/2.0 - 80, frameY - Frame.Y_SCALE/2.0 + 30, "misc/misc/heart.png", 40, 40);
        StdDraw.setFont(); StdDraw.setPenColor();
        StdDraw.text(frameX + Frame.X_SCALE/2.0 - 80, frameY - Frame.Y_SCALE/2.0 + 30, "%d".formatted(hpBar.getLives()));
    }

    public void drawAmmo(double frameX, double frameY, double halfHeight) {
        double baseX = frameX - Frame.X_SCALE/2.0 + 15 + hpBar.getMaxHP() + 4*halfHeight;
        double baseY = frameY - Frame.Y_SCALE / 2.0 + 15 + 16;
        for (int i = 0; i<ammo; i++) {
            StdDraw.picture(baseX + halfHeight*4*i, baseY, "misc/misc/projectile.png", halfHeight*4, halfHeight*2, 45);
        }
    }

    public void drawCriticalHealthEffect(double frameX, double frameY, HPBar HPBar) {
        double halfThickness = 5;
        int rectangleCount = (int) (10- HPBar.getRemainingHPPercentage()/3);

        for (int i = 0; i < rectangleCount; i++) {
            double fadeFactor = 1 - (i / (double) rectangleCount); // linear fade
            int alpha = (int) (fadeFactor * 255);
            StdDraw.setPenColor(new Color(123, 9, 9, alpha));

            // LEFT side
            StdDraw.filledRectangle(
                    frameX - Frame.X_SCALE / 2.0 + (2 * i + 1) * halfThickness,
                    frameY,
                    halfThickness,
                    Frame.Y_SCALE / 2.0
            );

            // RIGHT side
            StdDraw.filledRectangle(
                    frameX + Frame.X_SCALE / 2.0 - (2 * i + 1) * halfThickness,
                    frameY,
                    halfThickness,
                    Frame.Y_SCALE / 2.0
            );

            // BOTTOM side
            StdDraw.filledRectangle(
                    frameX,
                    frameY + Frame.Y_SCALE / 2.0 - (2 * i + 1) * halfThickness,
                    Frame.X_SCALE / 2.0,
                    halfThickness
            );

            // TOP side
            StdDraw.filledRectangle(
                    frameX,
                    frameY - Frame.Y_SCALE / 2.0 + (2 * i + 1) * halfThickness,
                    Frame.X_SCALE / 2.0,
                    halfThickness
            );
        }
    }


}
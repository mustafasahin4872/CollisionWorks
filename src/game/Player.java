package game;

import lib.StdDraw;

public class Player {

    public enum PASSCODE {
        ZERO, DEAD, NEXT, ALTERNATE1, ALTERNATE2, ALTERNATE3, SHOP
    }

    private PASSCODE passCode = PASSCODE.ZERO;
    private final String name, fileRoot;
    private final double defaultSide;
    private double side;
    private static final double
            MAX_SPEED1 = 10, MAX_SPEED2 = 20, MAX_SPEED3 = 40,
            A1 = 0.2, A2 = 0.4, A3 = 0.8;
    private double spawnX, spawnY, x, y, xVelocity, yVelocity,
            maxSpeed = MAX_SPEED2, acceleration = A2, deceleration = A2;
    private int xDirection, yDirection;
    private boolean xCollided, yCollided;
    private int lives = 3;
    private static final double MAX_HP = 200;
    private double hitPoints = MAX_HP;
    private int coinsCollected = 0;
    private int lastCheckPoint;
    private long playerTime;
    private final double[] effectTimer = new double[3];
    private char onTileType = ' ';
    private Accessory[] accessories;

    //there are 5 possible buffs, all are false at first and set true if buffed
    //fast, small, immune, magnetic, eagle eye


    //CONSTRUCTORS

    public Player() {
        this("Bob");
    }

    public Player(String name) {
        this.name = name;
        fileRoot = "misc/playerImages/" + name + "/";
        x = spawnX;
        y = spawnY;
        defaultSide = switch (name) {
            case "Bob", "Mike" -> 50;
            case "Zahit" -> 70;
            default -> 50;
        };
        side = defaultSide;
    }

    //GENERAL GETTERS AND SETTERS

    public String getName() {
        return name;
    }

    public char getOnTileType() {
        return onTileType;
    }

    public void setOnTileType(char onTileType) {
        this.onTileType = onTileType;
    }

    public void setAccessories(Accessory[] accessories) {
        this.accessories = accessories;
        for (Accessory accessory : accessories) {
            accessory.resize(defaultSide/50);
        }
    }

    public long getPlayerTime() {
        return playerTime;
    }

    public void setPlayerTime() {
        playerTime = System.currentTimeMillis();
    }

    //COLLISION

    public void resetXCollided() {
        xCollided = false;
    }

    public void resetYCollided() {
        yCollided = false;
    }

    public void xCollide() {
        xCollided = true;
    }

    public void yCollide() {
        yCollided = true;
    }

    public boolean isXCollided() {
        return xCollided;
    }

    public boolean isYCollided() {
        return yCollided;
    }

    //MOVEMENTS

    public void updateVelocity() {

        final double DT = Frame.DT;
        final double SQRT2 = Math.sqrt(2);

        double nextVx;
        double nextVy;

        if (xDirection != 0 && yDirection != 0) {
            nextVx = xVelocity + xDirection * acceleration / SQRT2 * DT;
            nextVy = yVelocity + yDirection * acceleration / SQRT2 * DT;
        }  else {
            nextVx = xVelocity + xDirection * acceleration * DT;
            nextVy = yVelocity + yDirection * acceleration * DT;
        }

        double nextVSquared = Math.pow(nextVx, 2) + Math.pow(nextVy, 2);
        double nextSpeed = Math.sqrt(nextVSquared);
        if (nextVSquared > maxSpeed * maxSpeed) {
            double scale = maxSpeed / nextSpeed;
            setXVelocity(nextVx * scale);
            setYVelocity(nextVy * scale);
        } else {
            setXVelocity(nextVx);
            setYVelocity(nextVy);
        }

        if (xDirection == 0) {
            if ((xVelocity >= 0 && xVelocity - deceleration * DT < 0) || (xVelocity <= 0 && xVelocity + deceleration * DT > 0)) {
                setXVelocity(0);
            } else {
                if (xVelocity > 0) {
                    setXVelocity(xVelocity - deceleration * DT);
                } else {
                    setXVelocity(xVelocity + deceleration * DT);
                }
            }
        }

        if (yDirection == 0) {

            if ((yVelocity >= 0 && yVelocity - deceleration * DT < 0) || (yVelocity <= 0 && yVelocity + deceleration * DT > 0)) {
                setYVelocity(0);
            } else {
                if (yVelocity > 0) {
                    setYVelocity(yVelocity - deceleration * DT);
                } else {
                    setYVelocity(yVelocity + deceleration * DT);
                }
            }
        }

    }

    public void slip() {
        acceleration = A1;
        deceleration = A1;
        maxSpeed = MAX_SPEED3;
    }

    public void slow() {
        acceleration = A1;
        deceleration = A3;
        maxSpeed = MAX_SPEED1;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
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

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void resetMaxSpeed() {
        maxSpeed = MAX_SPEED2;
    }

    public double getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }

    public void resetDeceleration() {
        deceleration = A2;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public void resetAcceleration() {
        acceleration = A2;
    }

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

    //SIZE

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

    //HP

    public void heal(double healAmount) {
        if (hitPoints + healAmount > MAX_HP) {
            hitPoints = MAX_HP;
        } else {
            hitPoints += healAmount;
        }
    }

    public void damage(double damageAmount) {
        if (hitPoints - damageAmount < 0) {
            hitPoints = 0;
        } else {
            hitPoints -= damageAmount;
        }
    }

    public boolean isPlayerDead() {
        return hitPoints == 0;
    }

    public void respawn() {
        x = spawnX;
        y = spawnY;
        xVelocity = 0;
        yVelocity = 0;
        acceleration = 0;
        deceleration = 0;
    }

    public void die() {
        lives--;
        if (lives == 0) {
            passCode = PASSCODE.DEAD;
            return;
        }
        hitPoints = MAX_HP;
        respawn();
    }

    public void addLives(int n) {
        lives+=n;
    }

    public void addLife() {
        lives++;
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

    public void collectCoin(int n) {
        coinsCollected += n;
    }


    //CHECKPOINT AND SPAWNPOINT

    public double[] getSpawnPoint() {
        return new double[]{spawnX, spawnY};
    }

    public void setSpawnPoint(double[] spawnPoint) {
        setSpawnX(spawnPoint[0]);
        setSpawnY(spawnPoint[1]);
    }

    public void setSpawnX(double spawnX) {
        this.spawnX = spawnX;
    }

    public void setSpawnY(double spawnY) {
        this.spawnY = spawnY;
    }

    public void updateLastCheckPoint() {
        lastCheckPoint++;
    }

    public int getLastCheckPointIndex() {
        return lastCheckPoint;
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

    //DRAW

    public void draw() {

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
            accessory.setCoordinates();
            accessory.draw();
        }
    }

    public void drawBig(double multiplier) {

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
            accessory.setCoordinates();
            accessory.drawBig(multiplier);
        }
    }

    public void drawHPBar(double frameX, double frameY) {
        //the outline of hp bar
        double thickness = 2;
        double barHalfheight = 12;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(
                frameX - Frame.X_SCALE/2.0 + 15 + (MAX_HP/2.0 + thickness),
                frameY - Frame.Y_SCALE/2.0 + 15 + (barHalfheight + thickness),
                MAX_HP/2.0 + thickness,
                barHalfheight + thickness);

        //the hp in side the outline
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.filledRectangle(
                frameX - Frame.X_SCALE/2.0 + 15 + thickness + hitPoints/2.0,
                frameY - Frame.Y_SCALE/2.0 + 15 + (barHalfheight + thickness),
                hitPoints/2.0,
                barHalfheight
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
        StdDraw.text(frameX + Frame.X_SCALE/2.0 - 80, frameY - Frame.Y_SCALE/2.0 + 30, "%d".formatted(lives));

    }

}

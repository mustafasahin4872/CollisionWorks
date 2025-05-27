public class Player implements Drawable {

    private static final double DEFAULT_SIDE = 50;
    private static double side;
    private static final double DEFAULT_SPAWN_X = 100;
    private static final double DEFAULT_SPAWN_Y = 300;
    private double spawnX;
    private double spawnY;
    private double x;
    private double y;

    private double xVelocity = 0;
    private double yVelocity = 0;

    private static final double MAX_SPEED1 = 10, MAX_SPEED2 = 20, MAX_SPEED3 = 40, A1 = 0.2, A2 = 0.4, A3 = 0.8;
    private double maxSpeed = MAX_SPEED2, acceleration = A2, deceleration = A2;

    private int xDirection = 0, yDirection = 0;

    private static final double MAX_HP = 200;
    private double hitPoints = MAX_HP;

    private int coinsCollected = 0;

    private boolean isPlayerDead = false;

    private long playerTime;
    private final double[] effectTimer = new double[3];

    private char onTileType = ' ';
    //there are 5 possible buffs, all are false at first and set true if buffed
    //fast, small, immune, magnetic, eagle eye


    //CONSTRUCTORS

    public Player(){
        this(DEFAULT_SIDE);
    }

    public Player(double spawnX, double spawnY) {
        this(spawnX, spawnY, DEFAULT_SIDE);
    }

    public Player(double side) {
        this(DEFAULT_SPAWN_X, DEFAULT_SPAWN_Y, side);
    }

    public Player(double spawnX, double spawnY, double side) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        x = spawnX;
        y = spawnY;
        Player.side = side;
    }


    public void updateVelocity() {

        double dt = Frame.DT;
        double sqrt2 = Math.sqrt(2);

        double nextVx;
        double nextVy;

        if (xDirection != 0 && yDirection != 0) {
            nextVx = xVelocity + xDirection * acceleration / sqrt2 * dt;
            nextVy = yVelocity + yDirection * acceleration / sqrt2 * dt;
        }  else {
            nextVx = xVelocity + xDirection * acceleration * dt;
            nextVy = yVelocity + yDirection * acceleration * dt;
        }

        double nextVSquared = Math.pow(nextVx, 2) + Math.pow(nextVy, 2);
        double nextSpeed = Math.sqrt(nextVSquared);
        if (nextVSquared > maxSpeed * maxSpeed) {
            double scale = maxSpeed / nextSpeed;
            setxVelocity(nextVx * scale);
            setyVelocity(nextVy * scale);
        } else {
            setxVelocity(nextVx);
            setyVelocity(nextVy);
        }

        if (xDirection == 0) {
            if ((xVelocity >= 0 && xVelocity - deceleration * dt < 0) || (xVelocity <= 0 && xVelocity + deceleration * dt > 0)) {
                setxVelocity(0);
            } else {
                if (xVelocity > 0) {
                    setxVelocity(xVelocity - deceleration * dt);
                } else {
                    setxVelocity(xVelocity + deceleration * dt);
                }
            }
        }

        if (yDirection == 0) {

            if ((yVelocity >= 0 && yVelocity - deceleration * dt < 0) || (yVelocity <= 0 && yVelocity + deceleration * dt > 0)) {
                setyVelocity(0);
            } else {
                if (yVelocity > 0) {
                    setyVelocity(yVelocity  - deceleration * dt);
                } else {
                    setyVelocity(yVelocity + deceleration * dt);
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

    public void heal(double healAmount) {
        if (hitPoints + healAmount > MAX_HP) {
            hitPoints = MAX_HP;
        } else {
            hitPoints += healAmount;
        }
    }

    public void damage(double damageAmount) {
        if (hitPoints - damageAmount <= 0) {
            hitPoints = 0;
            isPlayerDead = true;
        } else {
            hitPoints -= damageAmount;
        }
    }

    public void buff(char[] buffs, double time) {
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

    public void collectCoin(int n) {
        coinsCollected += n;
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

        setPlayerTime();
    }


    //GETTERS AND SETTERS

    public static double getSide() {
        return side;
    }

    public static void setSide(double side) {
        Player.side = side;
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

    public double getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public double getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(double yVelocity) {
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

    public int getxDirection() {
        return xDirection;
    }

    public void setxDirection(int xDirection) {
        this.xDirection = xDirection;
    }

    public int getyDirection() {
        return yDirection;
    }

    public void setyDirection(int yDirection) {
        this.yDirection = yDirection;
    }

    public boolean isPlayerDead() {
        return isPlayerDead;
    }

    public void setPlayerTime() {
        playerTime = System.currentTimeMillis();
    }

    public char getOnTileType() {
        return onTileType;
    }

    public void setOnTileType(char onTileType) {
        this.onTileType = onTileType;
    }

    public void respawn() {
        x = spawnX;
        y = spawnY;
    }
    //DRAW

    @Override
    public void draw() {

        if (xDirection == 1 && yDirection == 1) {
            StdDraw.picture(x, y, "misc/playerImages/playerUR.png", side, side);
        } else if (xDirection == 0 && yDirection == 1) {
            StdDraw.picture(x, y, "misc/playerImages/playerU.png", side, side);
        } else if (xDirection == -1 && yDirection == 1) {
            StdDraw.picture(x, y, "misc/playerImages/playerUL.png", side, side);
        } else if (xDirection == -1 && yDirection == 0) {
            StdDraw.picture(x, y, "misc/playerImages/playerL.png", side, side);
        } else if (xDirection == -1 && yDirection == -1) {
            StdDraw.picture(x, y, "misc/playerImages/playerDL.png", side, side);
        } else if (xDirection == 0 && yDirection == -1) {
            StdDraw.picture(x, y, "misc/playerImages/playerD.png", side, side);
        } else if (xDirection == 1 && yDirection == -1) {
            StdDraw.picture(x, y, "misc/playerImages/playerDR.png", side, side);
        } else if (xDirection == 1 && yDirection == 0){
            StdDraw.picture(x, y, "misc/playerImages/playerR.png", side, side);
        } else {
            StdDraw.picture(x, y, "misc/playerImages/player.png", side, side);
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

}

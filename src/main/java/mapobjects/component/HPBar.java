package mapobjects.component;

import lib.StdDraw;
import mapobjects.category.MapObject;

public class HPBar {

    private final int maxLives;
    private int lives;
    private final double maxHP;
    private double HP;
    private final double defense;

    public HPBar() {
        this(100, 1, 0);
    }

    public HPBar(double maxHP, double defense) {
        this(maxHP, 1, defense);
    }

    public HPBar(double maxHP) {
        this(maxHP, 1, 0);
    }

    public HPBar(double maxHP, int maxLives) {
        this(maxHP, maxLives, 0);
    }

    public HPBar(double maxHP, int maxLives, double defense) {
        this.maxHP = maxHP;
        this.maxLives = maxLives;
        lives = maxLives;
        this.defense = defense;
        HP=maxHP;
    }


    public boolean isDead() {
        return HP == 0;
    }

    public boolean noLivesLeft() {
        return lives == 0;
    }

    public void addLife() {
        this.lives++;
    }

    public void addLives(int lives) {
        this.lives+=lives;
    }

    public int getLives() {
        return lives;
    }

    public double getMaxHP() {
        return maxHP;
    }

    public double getHP() {
        return HP;
    }

    public double getDefense() {
        return defense;
    }

    public double getRemainingHPPercentage() {
        return HP/maxHP*100;
    }

    public void die() {
        lives--;
    }

    public void heal(double healAmount) {
        HP += healAmount;
        if (HP>maxHP) {
            HP = maxHP;
        }
    }

    public void takeDamage(double damageAmount) {
        damageAmount = Math.max(damageAmount-defense, 0);
        HP -= damageAmount;
        if (HP<=0) {
            HP = 0;
            die();
        }
    }

    public void revive() {
        HP = maxHP;
    }

    public void restart() {
        lives = maxLives;
        revive();
    }


    //draws the HP bar with respect to the mapObject
    public void drawHPBar(MapObject mapObject) {

        double centerX = mapObject.getX();
        double centerY = mapObject.getY() - mapObject.getHeight() * 0.6;
        double halfWidth = mapObject.getWidth()*0.4;
        double halfHeight = mapObject.getHeight()*0.05;
        double leftMostX = centerX-halfWidth;
        double thickness = halfHeight*0.1;

        //outline
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(centerX, centerY, halfWidth+thickness, halfHeight+thickness);
        //hp
        StdDraw.setPenColor((int) ((maxHP-HP)/maxHP*255), (int) (HP/maxHP*255), 30);
        StdDraw.filledRectangle(leftMostX+halfWidth*(HP/maxHP), centerY, halfWidth*(HP/maxHP), halfHeight);

    }

}

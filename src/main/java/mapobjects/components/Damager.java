package mapobjects.components;

public class Damager {

    private double damage;
    private double shred; // TODO: ADD DEFENSE SHREDDING OPTION AND MORE

    public Damager(double damage, double shred) {
        this.damage = damage;
        this.shred = shred;
    }

    public Damager(double damage) {
        this(damage, 0);
    }

    public double getDamage() {
        return damage;
    }

    public double getShred() {
        return shred;
    }

    public void increaseDamage(double extra) {
        damage += extra;
    }

    public void scaleDamage(double multiplier) {
        damage *= multiplier;
    }

    public void dealDamage(HPBar hpBar) {
        hpBar.takeDamage(damage);
    }

}

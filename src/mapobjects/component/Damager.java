package mapobjects.component;

public class Damager {

    private double damage;
    public Damager(double damage) {
        this.damage = damage;
    }

    public void increaseDamage(double extra) {
        damage += extra;
    }

    public void scaleDamage(double multiplier) {
        damage *= multiplier;
    }

    public void damage(HPBar HPBar) {
        HPBar.takeDamage(damage);
    }

}

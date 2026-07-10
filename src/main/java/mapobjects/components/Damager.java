package mapobjects.components;

public class Damager {

    private double damage;
    private double shred; // TODO: ADD DEFENSE SHREDDING OPTION AND MORE
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

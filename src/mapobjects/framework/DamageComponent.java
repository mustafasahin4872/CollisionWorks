package mapobjects.framework;

import game.Player;

public class DamageComponent {
    private final Player player;
    private double damage;

    public DamageComponent(Player player, double damage) {
        this.player = player;
        this.damage = damage;
    }


    public void increaseDamage(double extra) {
        damage += extra;
    }

    public void scaleDamage(double multiplier) {
        damage *= multiplier;
    }

    public void damage() {
        player.damage(damage);
    }

}

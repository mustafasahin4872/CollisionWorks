package mapobjects.framework;

import game.Player;

public class Damager {

    private final Player player;
    private double damage;

    public Damager(Player player, double damage) {
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

package mapobjects.framework;

import game.Player;

public class DamageComponent {
    private Player player;
    private double damage;

    public DamageComponent(Player player, double damage) {
        this.player = player;
        this.damage = damage;
    }

    public void damage() {
        player.damage(damage);
    }

}

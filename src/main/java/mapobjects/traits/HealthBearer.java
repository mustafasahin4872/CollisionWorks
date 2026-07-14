package mapobjects.traits;

import mapobjects.components.HPBar;
import mapobjects.effects.Effect;

// extends collidable, //TODO: IS THIS GOOD DESIGN?
public interface HealthBearer extends Collidable, Receiver {

    default void receiveEffect(Effect.HealthEffect effect) {
        Receiver.super.receiveEffect(effect);
    }

    HPBar getHealthBar();

    default void heal(double healAmount) {
        getHealthBar().heal(healAmount);
    }

    default void damage(double damageAmount) {
        getHealthBar().takeDamage(damageAmount);
    }

    default void checkDead() {
        if (getHealthBar().isDead()) ifDied();
        if (getHealthBar().noLivesLeft()) ifNoLivesLeft();
    }

    void ifDied();

    void ifNoLivesLeft();

}

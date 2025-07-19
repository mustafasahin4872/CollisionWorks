package mapobjects.category;

import mapobjects.component.HPBar;

public interface HealthBearer {

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

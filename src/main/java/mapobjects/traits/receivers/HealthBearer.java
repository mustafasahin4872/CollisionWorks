package mapobjects.traits.receivers;

import mapobjects.components.HPBar;
import mapobjects.traits.Collidable;

// extends collidable, //TODO: IS THIS GOOD DESIGN?
public interface HealthBearer extends Collidable, Receiver {

    HPBar getHealthBar();

    default void checkDead() {
        if (getHealthBar().isDead()) ifDied();
        if (getHealthBar().noLivesLeft()) ifNoLivesLeft();
    }

    void ifDied();

    void ifNoLivesLeft();

}

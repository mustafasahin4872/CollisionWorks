package mapobjects.traits.schemas;

import mapobjects.components.HPBar;
import mapobjects.traits.collisions.HasBody;

// all health bearers must have bodies
public interface HealthBearer extends HasBody {

    HPBar getHealthBar();

    default void checkDead() {
        if (getHealthBar().isDead()) ifDied();
        if (getHealthBar().noLivesLeft()) ifNoLivesLeft();
    }

    void ifDied();

    void ifNoLivesLeft();

}

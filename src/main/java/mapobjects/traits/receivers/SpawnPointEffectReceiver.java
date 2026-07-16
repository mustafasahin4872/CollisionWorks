package mapobjects.traits.receivers;

import mapobjects.effects.Effect;
import mapobjects.effects.SpawnPointEffect;

import java.util.Set;

public interface SpawnPointEffectReceiver extends Receiver {

    void setSpawnPoint(double x, double y);

    default void processSpawnPointEffect(Set<Effect> effects) {

        for (Effect effect : effects) {
            if (effect instanceof SpawnPointEffect(double x, double y)) {
                setSpawnPoint(x, y);
            }
        }

    }

}

package mapobjects.traits;

import mapobjects.effects.Effect;

public interface Effector {

    Effect getEffect();

    default void sendEffect(Receiver receiver) {
        receiver.receiveEffect(getEffect());
    }

}

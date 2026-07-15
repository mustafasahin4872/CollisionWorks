package mapobjects.traits.effectors;

import mapobjects.effects.Effect;
import mapobjects.traits.receivers.Receiver;

public interface Effector {

    Effect getEffect();

    default void sendEffect(Receiver receiver) {
        receiver.receiveEffect(getEffect());
    }

}

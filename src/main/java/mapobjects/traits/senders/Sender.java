package mapobjects.traits.senders;

import mapobjects.components.Effector;
import mapobjects.effects.Effect;
import mapobjects.traits.receivers.Receiver;

public interface Sender {

    Effector getEffector();

    default void sendEffect(Receiver receiver) {
        for (Effect effect : getEffector().getEffects()) {
            receiver.receiveEffect(effect);
        }
    }

}

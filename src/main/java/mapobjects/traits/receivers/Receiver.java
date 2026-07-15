package mapobjects.traits.receivers;

import mapobjects.components.Inbox;
import mapobjects.effects.Effect;

public interface Receiver {

    Inbox getInbox();

    default void receiveEffect(Effect effect) {getInbox().receive(effect);}

    void processEffects();

    default void clearEffects() {getInbox().clear();}

    default void call() {
        processEffects();
        clearEffects();
    }

}

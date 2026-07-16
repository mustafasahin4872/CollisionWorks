package mapobjects.traits.receivers;

import mapobjects.components.Inbox;
import mapobjects.effects.Effect;

import java.util.Set;

public interface Receiver {

    Inbox getInbox();

    default void receiveEffect(Effect effect) {getInbox().receive(effect);}

    default void processEffects() {
        Set<Effect> effects = getInbox().getEffects();
        if (this instanceof GameStateReceiver g) {
            g.processGameStateEffects(effects);
        }
        if (this instanceof HealthEffectReceiver h) {
            h.processHealthEffects(effects);
        }
        if (this instanceof MovementEffectReceiver m) {
            m.processMovementEffect(effects);
        }
        if (this instanceof SpawnPointEffectReceiver s) {
            s.processSpawnPointEffect(effects);
        }
        // TileReceiver does not accept a brand new type of Effect, it is just an acceptation type
    }

    default void clearEffects() {getInbox().clear();}

    default void callReceiver() {
        processEffects();
        clearEffects();
    }

}

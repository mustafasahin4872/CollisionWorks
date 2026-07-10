package mapobjects.traits;

import mapobjects.entities.Player;
import mapobjects.components.Timer;

public interface Timed {

    Timer getTimer();

    void timeIsUp(Player player);

    default void activateTimer() {
        getTimer().activate();
    }
    default void updateTimer() {
        getTimer().tick();
    }
    default boolean isComplete() {
        return getTimer().isCompleted();
    }
    default boolean isActive() {
        return getTimer().isActive();
    }
    default boolean inCooldown() {
        return getTimer().inCooldown();
    }
    default boolean cooldownOver() {
        return getTimer().cooldownOver();
    }

}


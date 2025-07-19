package mapobjects.category;

import game.Player;
import mapobjects.component.Timer;

public interface Timed {

    Timer getTimer();
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
    void timeIsUp(Player player);

    default boolean inCooldown() {
        return isComplete() && !isActive();
    }

    default boolean cooldownOver() {
        return !isActive() && !isComplete();
    }

}


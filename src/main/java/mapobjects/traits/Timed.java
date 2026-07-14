package mapobjects.traits;

import mapobjects.components.Timer;

public interface Timed {

    Timer getTimer();

    default void whenInactive() {}
    default void whenActive() {}
    default void whenCompleted() {}
    default void whenInCooldown() {}
    default void whenCooldownOver() {}

    default void callTimer() {
        getTimer().tick();
        if (isInactive()) whenInactive();
        if (isActive()) whenActive();
        if (isComplete()) whenCompleted();
        if (inCooldown()) whenInCooldown();
        if (cooldownOver()) whenCooldownOver();
    }

    default boolean isInactive() {return getTimer().isInactive();}
    default boolean isActive() {
        return getTimer().isActive();
    }
    default boolean isComplete() {return getTimer().isComplete();}
    default boolean inCooldown() {
        return getTimer().inCooldown();
    }
    default boolean cooldownOver() {return getTimer().cooldownOver();}

    default void activateTimer() {
        getTimer().activate();
    }
    default void startCooldown() {
        getTimer().startCooldown();
    }

}


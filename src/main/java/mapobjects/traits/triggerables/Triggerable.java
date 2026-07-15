package mapobjects.traits.triggerables;

import java.util.Set;

/// no class should inherit Triggerable directly! inherit its descendants
/// overload the generic TriggererType type in children interfaces
/// match with Effector classes to write action logic
public interface Triggerable<TriggererType> {

    Set<TriggererType> getTriggerers();

    void setTriggerers(Set<TriggererType> triggerers);

    // checks for a single receiver if it triggered the effector.
    boolean triggered(TriggererType triggererType);

    default void checkForTriggers() {
        for (TriggererType t : getTriggerers()) {
            if (triggered(t)) {
                action(t);
            }
        }
    }

    void action(TriggererType t);

}

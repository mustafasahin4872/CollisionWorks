package mapobjects.components;

import mapobjects.traits.collisions.HasBody;

import java.util.Set;
import java.util.function.Consumer;

import static helpers.CollisionMethods.intersects;

public class Trigger<T extends HasBody> {

    private final Box triggerBox;
    private Set<T> triggerers = Set.of();
    private final Consumer<T> action;

    public Trigger(Box triggerBox, Consumer<T> action) {
        this.triggerBox = triggerBox;
        this.action = action;
    }

    public void setTriggerers(Set<T> triggerers) {
        this.triggerers = triggerers;
    }

    public void checkForTriggers() {
        for (T t : triggerers) {
            if (triggered(t)) action.accept(t);
        }
    }

    public boolean triggered(T t) {
        return intersects(triggerBox, t.getPositionBox());
    }

}

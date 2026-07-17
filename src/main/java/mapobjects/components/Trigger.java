package mapobjects.components;

import mapobjects.traits.collisions.HasBody;
import java.util.function.Consumer;
import static helpers.CollisionEngine.intersects;

public class Trigger<T extends HasBody> {

    private final Box triggerBox;
    private final Consumer<T> action;

    public Trigger(Box triggerBox, Consumer<T> action) {
        this.triggerBox = triggerBox;
        this.action = action;
    }

    public Box getTriggerBox() {
        return triggerBox;
    }

    public void whenTriggered(T t) {
        action.accept(t);
    }

}

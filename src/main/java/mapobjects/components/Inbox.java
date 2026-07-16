package mapobjects.components;

import mapobjects.effects.Effect;

import java.util.HashSet;
import java.util.Set;

public class Inbox {

    private final Set<Effect> effects = new HashSet<>();

    public Set<Effect> getEffects() {
        return effects;
    }

    public void receive(Effect effect) {effects.add(effect);}

    public void clear() {effects.clear();}

}

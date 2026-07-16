package mapobjects.components;

import mapobjects.effects.Effect;

import java.util.HashSet;
import java.util.Set;

public class Effector {

    private final Set<Effect> effects = new HashSet<>();

    public Effector(Effect... effects) {
        for (Effect effect : effects) {
            addEffect(effect);
        }
    }

    private void addEffect(Effect effect) {effects.add(effect);}

    public Set<Effect> getEffects() {
        return effects;
    }

}

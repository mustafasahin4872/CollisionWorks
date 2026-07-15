package mapobjects.traits.effectors;

import mapobjects.components.Damager;
import mapobjects.effects.DamageEffect;
import mapobjects.traits.receivers.HealthBearer;

import java.util.Set;

public interface Damaging extends Effector {

    void setTargets(Set<HealthBearer> targets);

    Damager getDamager();

    @Override
    default DamageEffect getEffect() {
        return new DamageEffect(getDamager().getDamage(), getDamager().getShred());
    }


    // encapsulate sendEffect to disable sending effects to non-HealthBearer entities
    default void dealDamage(HealthBearer healthBearer) {
        sendEffect(healthBearer);
    }

}

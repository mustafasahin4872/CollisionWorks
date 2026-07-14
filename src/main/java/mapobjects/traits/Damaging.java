package mapobjects.traits;

import mapobjects.components.Damager;
import mapobjects.effects.DamageEffect;

import java.util.Set;

public interface Damaging extends Effector {

    void setTargets(Set<HealthBearer> targets);

    Set<HealthBearer> getTargets();

    DamageEffect getEffect();

    Damager getDamager();

    default void dealDamage(HealthBearer healthBearer) {
        healthBearer.receiveEffect(getEffect());
    }

    default void sendEffect(HealthBearer healthBearer) {
        Effector.super.sendEffect(healthBearer);
    }

}

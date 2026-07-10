package mapobjects.traits;

import mapobjects.components.Damager;

import java.util.Set;

public interface Damaging {

    void setTargets(Set<HealthBearer> targets);
    Set<HealthBearer> getTargets();

    Damager getDamager();

    default void dealDamage(HealthBearer healthBearer) {
        getDamager().damage(healthBearer.getHealthBar());
    }

}

package mapobjects.category;

import mapobjects.component.Damager;

public interface Damaging {

    Damager getDamager();

    default void dealDamage(HealthBearer healthBearer) {
        getDamager().damage(healthBearer.getHealthBar());
    }

}

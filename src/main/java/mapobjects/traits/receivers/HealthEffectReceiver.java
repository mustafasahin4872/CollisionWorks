package mapobjects.traits.receivers;

import mapobjects.components.HPBar;
import mapobjects.effects.DamageEffect;
import mapobjects.effects.Effect;
import mapobjects.effects.HealEffect;

import java.util.Set;

// all health bearers must have bodies
public interface HealthEffectReceiver extends Receiver {

    default void processHealthEffects(Set<Effect> effects) {

        double totalHeal = 0;
        double totalDamage = 0;
        double totalShred = 0;

        for (Effect effect : effects) {
            if (effect instanceof HealEffect(double heal)) {
                totalHeal += heal;
            }
            if (effect instanceof DamageEffect(double damage, double shred)) {
                totalDamage += damage;
                totalShred += shred;
            }
        }
        double diff = totalDamage - totalHeal;
        if (diff >= 0) {
            getHealthBar().takeDamage(diff, totalShred);
        } else {
            getHealthBar().heal(-diff);
        }

    }

    HPBar getHealthBar();

    default void checkDead() {
        if (getHealthBar().isDead()) ifDied();
        if (getHealthBar().noLivesLeft()) ifNoLivesLeft();
    }

    void ifDied();

    void ifNoLivesLeft();

}

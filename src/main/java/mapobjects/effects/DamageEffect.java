package mapobjects.effects;
import mapobjects.effects.Effect.HealthEffect;

public record DamageEffect(double damage, double shred) implements HealthEffect {
}

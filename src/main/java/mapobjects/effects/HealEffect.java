package mapobjects.effects;
import mapobjects.effects.Effect.HealthEffect;

public record HealEffect(double heal) implements HealthEffect {}


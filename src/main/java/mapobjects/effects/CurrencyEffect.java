package mapobjects.effects;
import mapobjects.effects.Effect.GameStateEffect;

public record CurrencyEffect(int coinAmount, int gemAmount) implements GameStateEffect {}


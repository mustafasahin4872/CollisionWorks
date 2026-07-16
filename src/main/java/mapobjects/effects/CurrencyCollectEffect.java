package mapobjects.effects;
import mapobjects.effects.Effect.GameStateEffect;

public record CurrencyCollectEffect(int coinAmount, int gemAmount) implements GameStateEffect {}


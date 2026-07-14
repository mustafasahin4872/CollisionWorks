package mapobjects.effects;
import mapobjects.effects.Effect.GameStateEffect;

record CurrencyEffect(int coinAmount, int gemAmount) implements GameStateEffect {}


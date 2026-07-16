package mapobjects.effects;
import mapobjects.effects.Effect.GameStateEffect;
import game.core.GameState.STATE;

public record StateEffect(STATE state) implements GameStateEffect {}

package mapobjects.traits.receivers;

import game.core.GameState;
import mapobjects.effects.CurrencyCollectEffect;
import mapobjects.effects.Effect;
import mapobjects.effects.StateEffect;

import java.util.Set;

public interface GameStateReceiver extends Receiver {

    GameState getGameState();

    default void processGameStateEffects(Set<Effect> effects) {

        for (Effect effect : effects) {

            if (effect instanceof StateEffect(GameState.STATE state)) {
                getGameState().setState(state);
            }
            if (effect instanceof CurrencyCollectEffect(int coinAmount, int gemAmount)) {
                getGameState().collectCoin(coinAmount);
                getGameState().collectGem(gemAmount);
            }

        }

    }

}

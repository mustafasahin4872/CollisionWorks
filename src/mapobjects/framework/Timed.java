package mapobjects.framework;

import game.Player;

public interface Timed {

    void activateTimer();
    void updateTimer();
    void timeIsUp(Player player);

}

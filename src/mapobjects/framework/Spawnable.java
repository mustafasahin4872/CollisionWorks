package mapobjects.framework;

import game.Player;

public interface Spawnable {

    MapObject[] getSpawnObjects();
    void mutate();

    default void callSpawned(Player player) {
        for (MapObject mapObject : getSpawnObjects()) {
            mapObject.call(player);
        }
    }

    void spawn();

}

package mapobjects.framework;

import game.Player;

public interface Spawner {

    MapObject[] getSpawnObjects();
    void mutate();

    default void callSpawned(Player player) {
        for (MapObject mapObject : getSpawnObjects()) {
            mapObject.call(player);
        }
    }

    void spawn();

}

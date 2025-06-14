package mapobjects.framework;

import game.Player;

public interface Spawnable {

    MapObject[] getSpawnObjects();

    MapObject mutate(int index);
    MapObject[] mutateAll();

    default void callSpawnObjects(Player player) {
        for (MapObject mapObject : getSpawnObjects()) {
            mapObject.call(player);
        }
    }

    void spawn();

}

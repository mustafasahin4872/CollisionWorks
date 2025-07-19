package mapobjects.category;

import mapobjects.mapobject.Player;

public interface Spawnable {

    MapObject[] getSpawnObjects();

    default void callSpawnObjects(Player player) {
        for (MapObject mapObject : getSpawnObjects()) {
            mapObject.call(player);
        }
    }

    void spawn();
}

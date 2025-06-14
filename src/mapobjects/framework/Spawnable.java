package mapobjects.framework;

import game.Player;

public interface Spawnable {

    GridObject[] getSpawnObjects();

    GridObject mutate(int index);
    GridObject[] mutateAll();

    default void callSpawnObjects(Player player) {
        for (GridObject gridObject : getSpawnObjects()) {
            gridObject.call(player);
        }
    }

    void spawn();

}

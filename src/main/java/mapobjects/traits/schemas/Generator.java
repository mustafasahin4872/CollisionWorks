package mapobjects.traits.schemas;

import java.util.Set;

public interface Generator {

    void spawn();

    void setSpawnedObjects(Set<MapObject> spawnedObjects);

}

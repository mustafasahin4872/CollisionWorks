package mapobjects.initialized;

import game.Player;
import mapobjects.framework.Collidable;
import mapobjects.framework.GridObject;

public class EmptyGridObject extends GridObject {

    protected final GridObject linkedObject;

    public EmptyGridObject(int worldIndex, int xNum, int yNum, GridObject linkedObject) {
        super(worldIndex, xNum, yNum);
        this.linkedObject = linkedObject;
    }


    @Override
    public void call(Player player) {
        linkedObject.call(player);
        if (linkedObject.isExpired()) {expire();}
    }

    @Override
    public void draw() {} //not drawn


    @Override
    public boolean isSolid() {
        return linkedObject instanceof Collidable;
    }

    public GridObject getLinkedObject() {
        return linkedObject;
    }
}

package mapobjects.traits.receivers;

import mapobjects.traits.collisions.HasBody;

import static mapobjects.traits.schemas.GridObject.TILE_SIDE;

public interface TileReceiver extends Receiver, HasBody {

    default int[] getCoveredTileIndexes() {
        double width = getPositionBox().getWidth();
        double height = getPositionBox().getHeight();
        int startX = (int) ((getX() - width / 2) / TILE_SIDE);
        int endX = (int) ((getX() + width / 2) / TILE_SIDE);
        int startY = (int) ((getY() - height / 2) / TILE_SIDE);
        int endY = (int) ((getY() + height / 2) / TILE_SIDE);
        return new int[]{startX, startY, endX, endY};
    }

}

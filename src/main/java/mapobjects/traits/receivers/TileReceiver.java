package mapobjects.traits.receivers;

import static mapobjects.traits.schemas.GridObject.TILE_SIDE;

public interface TileReceiver extends Receiver {

    // MUST HAVE A POSITION AND DIMENSIONS
    double getWidth();
    double getHeight();
    double getX();
    double getY();

    default int[] getCoveredTileIndexes() {
        int startX = (int) ((getX() - getWidth() / 2) / TILE_SIDE);
        int endX = (int) ((getX() + getWidth() / 2) / TILE_SIDE);
        int startY = (int) ((getY() - getHeight() / 2) / TILE_SIDE);
        int endY = (int) ((getY() + getHeight() / 2) / TILE_SIDE);
        return new int[]{startX, startY, endX, endY};
    }

}

package mapobjects;

import game.Player;
import lib.StdDraw;

import java.awt.*;
import static helperobjects.DrawMethods.drawRectangle;

public class CheckPoint extends Button {

    private final int index;
    private final Sign unmarkedCheckPointSign = new Sign(0, xNum, yNum, new String[]{"Mark the previous checkpoint first"});
    private final double[] centerCoordinates;

    public CheckPoint(int xNum, int yNum, int index) {
        super(xNum, yNum, true);
        this.index = index;
        centerCoordinates = new double[]{(xNum-0.5)*TILE_SIDE*2, (yNum-0.5)*TILE_SIDE*2};
    }

    public Sign getUnmarkedCheckPointSign() {
        return unmarkedCheckPointSign;
    }

    @Override
    public void playerIsOn(Player player) {
        if (pressed) return; //already set the checkPoint
        // check if the previous checkPoints are reached
        boolean[] checkPointsReached = player.getCheckPointsReached();
        for (int i = 0; i<index; i++) {
            if (!checkPointsReached[i]) {
                unmarkedCheckPointSign.playerIsOn(player);
                return;
            }
        }
        pressed = true;
        player.setSpawnPoint(new double[]{coordinates[2]-coordinates[0], coordinates[3]-coordinates[1]});

    }

    @Override
    public void draw() {
        StdDraw.setPenColor(new Color(1, 1, 1));
        drawRectangle(coordinates);
        StdDraw.picture(centerCoordinates[0], centerCoordinates[1], "misc/checkPointImages/"+index+".png", TILE_SIDE, TILE_SIDE);
    }

}
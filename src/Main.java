import game.*;
import lib.StdDraw;

public class Main {
    public static void main(String[] args){
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize((int) Frame.X_SCALE, (int)Frame.Y_SCALE);
        playFrames(createFrame());
    }

    //infinite recursion! add a quiting option
    private static void playFrames(Frame currentFrame) {

        while (true) {

            currentFrame.play();

            Player currentPlayer = currentFrame.getPlayer();
            GameMap gameMap = currentFrame.getGameMap();
            GameMap nextGameMap = currentFrame.getNextMap();

            //the in between scene
            int nextWorldIndex = gameMap.getWorldIndex();
            if (gameMap.getLevelIndex() == 12) {nextWorldIndex++;}
            GameMap inBetweenMap = new GameMap(nextWorldIndex, 0, 32, 16, currentPlayer, gameMap.getAccessories());
            Frame inBetweenFrame = new Frame(inBetweenMap, currentPlayer);
            int passCode = inBetweenFrame.play();
            if (passCode == 5) {break;} //edit code to be of multiple return possibilities(enum or int types)

            currentFrame = new Frame(nextGameMap, currentPlayer);
        }

        playFrames(createFrame());

    }

    //creates Map and frameobjects.Frame objects by creating FrameObjects.Selection
    private static Frame createFrame() {
        //the level choosing screen
        Selection selection = new Selection();

        //start selection and get a map
        GameMap gameMap = selection.chooseLevel();

        return new Frame(gameMap, gameMap.getPlayer());
    }

}

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

        Player currentPlayer = currentFrame.getPlayer();

        while (true) {

            currentFrame.play();
            currentPlayer.resetPassCode();
            GameMap gameMap = currentFrame.getGameMap();
            GameMap nextGameMap = currentFrame.getNextMap();

            //the in between scene
            int nextWorldIndex = gameMap.getWorldIndex();
            if (gameMap.getLevelIndex() == 12) {nextWorldIndex++;}
            GameMap inBetweenMap = new GameMap(nextWorldIndex, 0, 32, 16, currentPlayer);
            Frame inBetweenFrame = new Frame(inBetweenMap, currentPlayer);
            Player.PASSCODE passCode = inBetweenFrame.play();

            if (passCode == Player.PASSCODE.SHOP) {break;} //add meaningful passcode mechanics
            currentPlayer.resetPassCode(); //put this to somewhere meaningful

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

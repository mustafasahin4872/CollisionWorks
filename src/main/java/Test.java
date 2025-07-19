import game.Frame;
import lib.StdDraw;
import game.GameMap;
import game.Player;

public class Test {
    public static void main(String[] args) {

            StdDraw.enableDoubleBuffering();
            StdDraw.setCanvasSize((int) Frame.X_SCALE, (int)Frame.Y_SCALE);

            Player player = new Player();
            GameMap gameMap =new GameMap(1, 9, 18, 12, player);
            Frame frame = new Frame(gameMap, gameMap.getPlayer());
            frame.play();

        }
}

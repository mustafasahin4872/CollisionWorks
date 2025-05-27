import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize((int)Frame.X_SCALE, (int)Frame.Y_SCALE);

        Selection selection = new Selection();
        //the level choosing screen
        int[] level = selection.chooseLevel();

        Map map =new Map(level[0], level[1]);
        Frame frame = new Frame(map);
        frame.play();

    }

}

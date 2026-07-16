package mapobjects;

public abstract class TimerComponent extends MapObject implements Timer{

    protected boolean active, complete, recurring;
    protected long startTime;
    protected double period, cooldown;

    public TimerComponent(int worldIndex, int xNum, int yNum, double width, double height, String fileName, boolean cornerAligned) {
        super(worldIndex, xNum, yNum, width, height, fileName, cornerAligned);
    }

    protected void countDown() {
        if (!complete && active && System.currentTimeMillis()-startTime>period){
            startTime = System.currentTimeMillis();
            complete = true;
        }
        if (recurring && complete && System.currentTimeMillis()-startTime>cooldown) {
            startTime = System.currentTimeMillis();
            complete = false;
        }
    }

}

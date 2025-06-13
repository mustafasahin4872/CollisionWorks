package mapobjects.framework;

//imitates a timer
//when activated, starts ticking, after period is over,
//gets deactivated and goes to cooldown if recurring.
public class Timer {

    protected boolean active, completed;
    protected long startTime;
    protected final boolean recurring;
    protected final double period, cooldown;

    public Timer(double period, double cooldown) {
        this.period = period;
        this.cooldown = cooldown;
        recurring = !(cooldown == -1);
    }


    public boolean isCompleted() {
        return completed;
    }

    public boolean isActive() {
        return active;
    }


    public void tick() {
        long timePassed = System.currentTimeMillis() - startTime;
        if (active && !completed && timePassed >= period) {
            if (recurring) {startCooldown();}
            else {complete();}
        } else if (recurring && completed && timePassed >= cooldown) {
            finishCooldown();
        }
    }


    public void activate() {
        active = true;
        completed = false;
        startTime = System.currentTimeMillis();
    }

    private void complete() {
        completed = true;
        active = false;
    }

    public void startCooldown() {
        complete();
        startTime = System.currentTimeMillis();
    }

    private void finishCooldown() {
        completed = false;
        active = false;
    }


    public double progressRatio() {
        if (!active) {
            return 0;
        } else if (completed) {
            return 1;
        } else {
            double timePassed = System.currentTimeMillis() - startTime;
            return timePassed/period;
        }
    }

}

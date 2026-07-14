package mapobjects.components;

/// has 4 states:
/// active: 0 -> PERIOD
/// completed: PERIOD -> cooldown start
/// inCooldown: 0 -> COOLDOWN
/// cooldownOver: COOLDOWN -> reactivation
/// has 2 modes: cyclical = true or false
/// if true, after a period is done, stays completed for 1 frame then goes into cooldown,
/// after cooldown, stays cooldownOver for one frame then restarts
/// if false, needs manual calls for activate() and startCooldown(),
/// until activation, stays in complete or cooldownOver states indefinitely.

public class Timer {

    public enum CLOCK_STATE {ACTIVE, COMPLETE, IN_COOLDOWN, COOLDOWN_OVER, INACTIVE}
    private CLOCK_STATE clockState = CLOCK_STATE.INACTIVE;

    private long startTime;
    private final boolean cyclical;
    private double period, cooldown;

    public Timer(double period, double cooldown, boolean cyclical) {
        this.period = period;
        this.cooldown = cooldown;
        this.cyclical = cyclical;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }


    public boolean isActive() {
        return clockState == CLOCK_STATE.ACTIVE;
    }

    public boolean isComplete() {return clockState == CLOCK_STATE.COMPLETE;}

    public boolean inCooldown() {
        return clockState == CLOCK_STATE.IN_COOLDOWN;
    }

    public boolean cooldownOver() {
        return clockState == CLOCK_STATE.COOLDOWN_OVER;
    }

    public boolean isInactive() {return clockState == CLOCK_STATE.INACTIVE;}



    public void activate() {
        clockState = CLOCK_STATE.ACTIVE;
        startTime = System.currentTimeMillis();
    }

    public void complete() {
        clockState = CLOCK_STATE.COMPLETE;
    }

    public void startCooldown() {
        clockState = CLOCK_STATE.IN_COOLDOWN;
        startTime = System.currentTimeMillis();
    }

    private void finishCooldown() {
        clockState = CLOCK_STATE.COOLDOWN_OVER;
    }

    private void inactivate() {
        clockState = CLOCK_STATE.INACTIVE;
    }

    public void tick() {
        long timePassed = System.currentTimeMillis() - startTime;
        switch (clockState) {
            case ACTIVE -> {
                if (timePassed >= period) complete();
            }
            case COMPLETE -> {
                if (cyclical) startCooldown();
            }
            case IN_COOLDOWN -> {
                if (timePassed >= cooldown) finishCooldown();
            }
            case COOLDOWN_OVER -> {
                if (cyclical) activate();
            }
        }
    }

    public double progressRatio() {
        if (isComplete()) {
            return 1;
        } else if (isActive()) {
            double timePassed = System.currentTimeMillis() - startTime;
            return timePassed/period % 1;
        } else {
            return 0;
        }
    }

}

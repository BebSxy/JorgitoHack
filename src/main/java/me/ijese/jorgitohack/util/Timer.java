package me.ijese.jorgitohack.util;

public class Timer {
    private long time = -1L;
    public long current = System.currentTimeMillis();

    public boolean passed(double ms) {
        return (double)(System.currentTimeMillis() - this.current) >= ms;
    }

    public boolean passedS(double s) {
        return this.passedMs((long) s * 1000L);
    }

    public boolean passedDms(double dms) {
        return this.passedMs((long) dms * 10L);
    }

    public boolean passedDs(double ds) {
        return this.passedMs((long) ds * 100L);
    }

    public boolean passedMs(long ms) {
        return this.passedNS(this.convertToNS(ms));
    }

    public void setMs(long ms) {
        this.time = System.nanoTime() - this.convertToNS(ms);
    }

    boolean paused = false;

    long startTime = System.currentTimeMillis();

    long delay = 0L;

    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }

    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }

    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }

    public boolean hasPassed(double ms) {
        return (double)(System.currentTimeMillis() - this.time) >= ms;
    }

    public boolean hasReached(long var1) {
        return System.currentTimeMillis() - this.current >= var1;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void resetDelay() {
        this.startTime = System.currentTimeMillis();
    }

    public boolean isPassed() {
        return !this.paused && System.currentTimeMillis() - this.startTime >= this.delay;
    }

    public long getMs(long time) {
        return time / 1000000L;
    }

    public long convertToNS(long time) {
        return time * 1000000L;
    }
}


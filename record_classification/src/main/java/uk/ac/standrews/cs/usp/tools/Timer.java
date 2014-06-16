package uk.ac.standrews.cs.usp.tools;

/**
 * Simple Timer class that is can be used for timing method execution times, amongst other things.
 */
public class Timer {

    /** The start time. */
    private long startTime = 0;

    /** The finish time. */
    private long finishTime = 0;

    /**
     * Starts the timer.
     */
    public void start() {

        startTime = System.nanoTime();
    }

    /**
     * Stops the time.
     */
    public void stop() {

        finishTime = System.nanoTime();
    }

    /**
     * Returns the elapsed time between starting and stopping the timer.
     *
     * @return  long, elapsed time in nano-seconds
     */
    public long elapsedTime() {

        return finishTime - startTime;
    }
}

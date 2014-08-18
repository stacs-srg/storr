package uk.ac.standrews.cs.digitising_scotland.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Timer class that is can be used for timing method execution times, amongst other things.
 */
public class Timer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Timer.class);

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
     * Stops the timer.
     */
    public void stop() {

        finishTime = System.nanoTime();
        LOGGER.info("Elapsed Time: " + elapsedTime());

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

/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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

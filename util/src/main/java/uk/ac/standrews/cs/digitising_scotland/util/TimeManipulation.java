package uk.ac.standrews.cs.digitising_scotland.util;

import static java.util.concurrent.TimeUnit.*;

/**
 * Created by graham on 28/05/2014.
 */
public class TimeManipulation {

    public static void reportElapsedTime(long start_time) {

        // TODO use standard logging.

        System.out.println("Elapsed time: " + formatMillis(System.currentTimeMillis() - start_time));
    }

    private static String formatMillis(long millis) {

        final long hours = MILLISECONDS.toHours(millis);
        long millis_remaining = millis - HOURS.toMillis(hours);
        final long minutes = MILLISECONDS.toMinutes(millis_remaining);
        millis_remaining = millis_remaining - MINUTES.toMillis(minutes);
        final long seconds = MILLISECONDS.toSeconds(millis_remaining);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

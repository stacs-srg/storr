package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * Generates (sequential) persistent unique IDs.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 *
 */
public class IDFactory {

    private static int id;

    static {
        try {
            loadPersistentId();
        }
        catch (final Throwable e) {
            ErrorHandling.exceptionError(e, "Cannot recover persistent id");
        }
    }

    public static int getNextID() {

        return ++id;
    }

    public static void resetId() {

        id = 0;
    }

    private static void loadPersistentId() {

        // TODO get id from database
    }
}

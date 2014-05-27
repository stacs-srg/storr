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

    private static final String UUID_COUNT = "uuid_count";
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
        savePersistentId();
    }

    private static void loadPersistentId() {

        // TODO get id from database
//        id = Integer.parseInt(PopulationProperties.getProperties().getProperty(UUID_COUNT));
    }

    public static void savePersistentId() {

//        PopulationProperties.getProperties().setProperty(UUID_COUNT, Integer.toString(id));
    }
}

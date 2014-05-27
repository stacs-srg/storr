package uk.ac.standrews.cs.digitising_scotland.population_model.generation.util;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.io.IOException;
import java.util.Random;

/**
 * Created by graham on 29/04/2014.
 */
public class RandomFactory {

    public static final long SEED = 534534524234234L;

    private static boolean deterministic = true;
    private static boolean configured = false;

    public static synchronized Random getRandom() {

        if (!configured) {

            try {
                deterministic = PopulationProperties.getDeterministic();
            } catch (IOException e) {
                throw new RuntimeException("Couldn't read determinism flag from properties");
            }
            configured = true;
        }

        /*
        Create a new Random instance every time, so that the user that wants determinism isn't affected
        by previous calls to the Random instance.
        */
        return deterministic ? new Random(SEED) : new Random();
    }
}

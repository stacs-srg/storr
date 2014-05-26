package uk.ac.standrews.cs.digitising_scotland.population_model.generation.util;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.util.Random;

/**
 * Created by graham on 29/04/2014.
 */
public class RandomFactory {

    public static final long SEED = 534534524234234L;

    public static Random getRandom() {

        /*
        Create a new Random instance every time, so that the user that wants determinism isn't affected
        by previous calls to the Random instance.
        */
        return PopulationProperties.getDeterministic() ? new Random(SEED) : new Random();
    }
}

package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EnumeratedDistributionTestManual {

    public static void main(final String[] args) throws IOException, InconsistentWeightException {

        Map<String, Double> item_probabilities = new HashMap<>();

        item_probabilities.put("quick", 0.1);
        item_probabilities.put("brown", 0.1);
        item_probabilities.put("fox", 0.8);

        final EnumeratedDistribution distribution = new EnumeratedDistribution(item_probabilities, new Random());

        for (int i = 0; i < 30; i++) {
            System.out.println(distribution.getSample());
        }
    }
}

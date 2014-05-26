package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

public class StringWithCumulativeProbability {

    String item;
    Double cumulative_probability;

    public StringWithCumulativeProbability(final String item, final double cumulative_probability) {

        this.item = item;
        this.cumulative_probability = cumulative_probability;
    }
}

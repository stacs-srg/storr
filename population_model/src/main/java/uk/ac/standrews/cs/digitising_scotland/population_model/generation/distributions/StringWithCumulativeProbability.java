package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

public class StringWithCumulativeProbability {

    private String item;
    private Double cumulative_probability;

    public StringWithCumulativeProbability(final String item, final double cumulative_probability) {

        this.item = item;
        this.cumulative_probability = cumulative_probability;
    }

    public String getItem() {
        return item;
    }

    public Double getCumulativeProbability() {
        return cumulative_probability;
    }
}

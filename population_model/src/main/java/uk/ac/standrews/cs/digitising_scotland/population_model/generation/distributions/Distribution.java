package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

/**
 * Allows user to pick random samples from some distribution.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 *
 * @param <Value> the type of the samples
 */
public interface Distribution<Value> {

    /**
     * Picks a random sample from the distribution.
     * @return the next sample from the distribution
     */
    Value getSample();

    // TODO add method parameterised by date
}

package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

/**
 * Exception indicating a negative weight supplied to a distribution.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class NegativeWeightException extends Exception {

    /**
     * Creates an exception.
     * @param message the message
     */
    public NegativeWeightException(final String message) {

        super(message);
    }
}

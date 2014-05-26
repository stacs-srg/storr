package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

/**
 * Exception indicating a negative standard deviation supplied to a distribution.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class NegativeDeviationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -6878666381850895224L;

    /**
     * Creates an exception.
     * @param message the message
     */
    public NegativeDeviationException(final String message) {

        super(message);
    }
}

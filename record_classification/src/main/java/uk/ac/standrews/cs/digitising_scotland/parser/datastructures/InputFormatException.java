package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

/**
 * InputFormatException is called in the instance of an input being mal-formed or of the wrong type.
 */
public class InputFormatException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 742441592450510717L;

    /**
     * Instantiates a new input format exception.
     *
     * @param errorMessage the error message
     */
    public InputFormatException(final String errorMessage) {

        super(errorMessage);
        System.err.println(errorMessage);

    }

}

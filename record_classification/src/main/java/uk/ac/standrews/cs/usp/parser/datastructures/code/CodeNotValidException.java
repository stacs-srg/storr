package uk.ac.standrews.cs.usp.parser.datastructures.code;

/**
 * Thrown when a trying to instantiate a code with a string representation of that code which is
 * not valid.
 * 
 * Non valid codes are either null or do not conform to the type of that code.
 * 
 * Example, {@link OccCode}s are all numeric. CoD codes must start with a letter.
 * @author jkc25
 *
 */
public class CodeNotValidException extends Exception {

    /**
     * Generated serialVesionUID.
     */
    private static final long serialVersionUID = -363132943300791654L;

    /**
     * Thrown when a code is not valid for its type.
     * @param errorMessage Message to report to user.
     */
    public CodeNotValidException(final String errorMessage) {

        super(errorMessage);
        System.err.println(errorMessage);

    }
}

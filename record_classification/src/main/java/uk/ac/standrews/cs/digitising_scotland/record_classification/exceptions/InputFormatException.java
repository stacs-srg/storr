package uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InputFormatException is called in the instance of an input being malformed or of the wrong type.
 */
public class InputFormatException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 742441592450510717L;

    /**
     * Instantiates a new input format exception.
     *
     * @param errorMessage the error message
     */
    public InputFormatException(final String errorMessage, final Class parent) {

        super(errorMessage);
        Logger LOGGER = LoggerFactory.getLogger(parent);
        LOGGER.error(this.toString());
        for (StackTraceElement trace : this.getStackTrace()) {
            LOGGER.error(trace.toString());

        }
    }

}

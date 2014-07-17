package uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions;

/**
 * The Class FolderCreationException indicated that a folder required for correct program exectuion has not been created.
 * 
 */
public class FolderCreationException extends RuntimeException {

    /**
     * Instantiates a new folder creation exception.
     *
     * @param message the string
     */
    public FolderCreationException(final String message) {

        super(message);
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8768682566508579625L;

}

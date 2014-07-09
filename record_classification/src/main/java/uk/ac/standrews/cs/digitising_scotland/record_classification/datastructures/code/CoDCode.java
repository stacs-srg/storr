package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

/**
 * Lower level class that represents a cause of death code.
 * @author jkc25, frjd2
 *
 */
public class CoDCode extends Code {

    /** The Constant serialVersionUID. Used for serialization.*/
    private static final long serialVersionUID = -5117020872028294902L;
    private static final int MAX_CODE_LENGTH = 7;

    /**
     * Instantiates a new cause of death code. Used for testing only. In production level code {@link Code}s should
     * be created using the {@link CodeFactory}.
     *
     * @param code String representation of the code
     * @param description the description of that code
     * @param id the id of the code
     * @throws CodeNotValidException the code not valid exception
     */
    protected CoDCode(final String code, final String description, final int id) throws CodeNotValidException {

        super(code, description, id);
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code#checkValid()
     */
    @Override
    protected boolean checkValid() throws CodeNotValidException {

        if (!isValid()) { throw new CodeNotValidException(super.getCodeAsString() + " is not a valid HICOD code"); }
        return true;
    }

    /**
     * Checks if is valid.
     *
     * @return true, if is valid
     */
    private boolean isValid() {

        return super.getCodeAsString().length() <= MAX_CODE_LENGTH && super.getCodeAsString().substring(0, 1).matches("[a-zA-Z]");
    }
}

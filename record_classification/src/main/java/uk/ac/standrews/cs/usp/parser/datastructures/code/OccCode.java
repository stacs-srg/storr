package uk.ac.standrews.cs.usp.parser.datastructures.code;

/**
 * The Class OccCode represnts a lower level occupation {@link Code}.
 * @author jkc25, frjd2
 */
public final class OccCode extends Code {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4789737924859908555L;

    /**
     * Instantiates a new occupation code. Should only be used for testing.
     * In production code all {@link OccCode} should be generated using the {@link CodeFactory}.
     *
     * @param code String representation of the code
     * @param description the description of the code
     * @param id the id of the code
     * @throws CodeNotValidException the code not valid exception
     */
    protected OccCode(final String code, final String description, final int id) throws CodeNotValidException {

        super(code, description, id);
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.usp.parser.datastructures.code.Code#checkValid()
     */
    @Override
    protected boolean checkValid() throws CodeNotValidException {

        if (!isValid()) { throw new CodeNotValidException(super.getCodeAsString() + " is not a valid HISCO code"); }
        return true;
    }

    /**
     * Checks if is valid.
     *
     * @return true, if is valid
     */
    private boolean isValid() {

        return super.getCodeAsString().length() <= 5 && super.getCodeAsString().substring(0, 1).matches("[0-9]");
    }
}

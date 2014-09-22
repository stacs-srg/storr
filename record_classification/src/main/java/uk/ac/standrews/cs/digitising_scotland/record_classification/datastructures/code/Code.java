package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

/**
 * Represents either a HICOD or HISCO code.
 * As isValid will be a different check for each type o code, only CoDCode and OccCode can
 * be instantiated.
 * @author jkc25, frjd2
 *
 */
public class Code implements java.io.Serializable {

    private static final long serialVersionUID = 2214478914861326040L;
    private String code;
    private String description;

    /**
     * Describes a code and description pair.
     * @param code HICOD or HISCO code
     * @param description description of code
     * @throws CodeNotValidException is code is not a valid
     */
    protected Code(final String code, final String description) {

        this.code = code;
        this.description = description;
    }

    /**
     Checks if this is a ancestor of the supplied {@link Code}.
     * @param codeToCheck code to check against.
     * @return true, if is ancestor
     */
    public boolean isAncestor(final Code codeToCheck) {

        return codeToCheck.isDescendant(this);
    }

    /**
     * Checks if this is a descendant of the supplied {@link Code}.
     * @param codeToCheck code to check against.
     * @return true, if is descendant
     */
    public boolean isDescendant(final Code codeToCheck) {

        Code code = codeToCheck;
        if (this.getCodeAsString().equals(code.getCodeAsString())) { return false; }
        if (this.getCodingLevel() < code.getCodingLevel()) { return false; }
        for (int i = 0; i < code.getCodingLevel(); i++) {
            if (!this.getCharAtIndex(i).equals(code.getCharAtIndex(i))) { return false; }
        }
        return true;
    }

    protected String getCharAtIndex(final int index) {

        return this.getCodeAsString().substring(index, index + 1);
    }

    /**
     * Gets the code as a String.
     *
     * @return the code
     */
    public String getCodeAsString() {

        return code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {

        return description;
    }

    /**
     * Gets the depth level of the code.
     * @return int
     */
    public int getCodingLevel() {

        return code.length();
    }

    @Override
    public String toString() {

        return "Code [code=" + code + ", description=" + description + "]";
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Code other = (Code) obj;
        if (code == null) {
            if (other.code != null) return false;
        }
        else if (!code.equals(other.code)) return false;
        return true;
    }

}

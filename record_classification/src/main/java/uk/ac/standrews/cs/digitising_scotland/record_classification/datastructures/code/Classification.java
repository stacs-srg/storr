package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.Serializable;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * This class represents a classification, either gold standard or from a classifier.
 * The class contains 3 variables, the {@link Code}, the {@link TokenSet} that relates to the code and finally
 * the confidence of that code.
 * <br><br>
 * @author jkc25, frjd2
 * Created by fraserdunlop on 11/06/2014 at 10:51.
 */
public class Classification implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7683621012309471383L;

    /** The code of the classification. */
    private final Code code;

    /** The token set representing the string that relates to the code. */
    private final TokenSet tokenSet;

    /** The confidence of the classification. */
    private final Double confidence;

    /**
     * Instantiates a new code triple.
     *
     * @param code the code
     * @param tokenSet the token set
     * @param confidence the confidence
     */
    public Classification(final Code code, final TokenSet tokenSet, final Double confidence) {

        this.code = code;
        this.tokenSet = tokenSet;
        this.confidence = confidence;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public Code getCode() {

        return code;
    }

    /**
     * Gets the token set.
     *
     * @return the token set
     */
    public TokenSet getTokenSet() {

        return tokenSet;
    }

    /**
     * Gets the confidence.
     *
     * @return the confidence
     */
    public Double getConfidence() {

        return confidence;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((confidence == null) ? 0 : confidence.hashCode());
        result = prime * result + ((tokenSet == null) ? 0 : tokenSet.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Classification other = (Classification) obj;
        if (code == null) {
            if (other.code != null) { return false; }
        }
        else if (!code.equals(other.code)) { return false; }
        if (confidence == null) {
            if (other.confidence != null) { return false; }
        }
        else if (!confidence.equals(other.confidence)) { return false; }
        if (tokenSet == null) {
            if (other.tokenSet != null) { return false; }
        }
        else if (!tokenSet.equals(other.tokenSet)) { return false; }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "CodeTriple [code=" + code + ", tokenSet=" + tokenSet + ", confidence=" + confidence + "]";
    }

}

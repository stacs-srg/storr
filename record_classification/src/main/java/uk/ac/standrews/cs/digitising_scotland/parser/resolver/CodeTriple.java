package uk.ac.standrews.cs.digitising_scotland.parser.resolver;

import java.io.Serializable;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code;

/**
 * Created by fraserdunlop on 11/06/2014 at 10:51.
 */
public class CodeTriple implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7683621012309471383L;
    private final Code code;
    private final TokenSet tokenSet;
    private final Double confidence;

    public CodeTriple(final Code code, final TokenSet tokenSet, final Double confidence) {

        this.code = code;
        this.tokenSet = tokenSet;
        this.confidence = confidence;
    }

    public Code getCode() {

        return code;
    }

    public TokenSet getTokenSet() {

        return tokenSet;
    }

    public Double getConfidence() {

        return confidence;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((confidence == null) ? 0 : confidence.hashCode());
        result = prime * result + ((tokenSet == null) ? 0 : tokenSet.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        CodeTriple other = (CodeTriple) obj;
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

    @Override
    public String toString() {

        return "CodeTriple [code=" + code + ", tokenSet=" + tokenSet + ", confidence=" + confidence + "]";
    }

}

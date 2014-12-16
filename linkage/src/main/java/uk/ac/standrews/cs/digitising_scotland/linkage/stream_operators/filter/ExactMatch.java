package uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.filter;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IInputStream;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IOutputStream;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

/**
 * Provides exact match filtering of LXP records based on a label and a value.
 * Created by al on 29/04/2014.
 */
public class ExactMatch<T extends ILXP> extends Filter {

    private final String value;
    private final String label;

    public ExactMatch(final IInputStream<T> input, final IOutputStream<T> output, final String label, final String value) {

        super(input, output);
        this.label = label;
        this.value = value;
    }

    public boolean select(final ILXP record) {

        try {
            return record.containsKey(label) && record.getString(label).equals(value);
        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            return false;
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            return false;
        }
    }
}

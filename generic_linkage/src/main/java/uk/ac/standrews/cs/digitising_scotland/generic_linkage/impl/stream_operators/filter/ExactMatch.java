package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.filter;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IOutputStream;

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

        return record.containsKey(label) && record.get(label).equals(value);
    }
}

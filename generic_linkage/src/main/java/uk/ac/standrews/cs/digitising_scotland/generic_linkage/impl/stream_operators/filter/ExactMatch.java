package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.filter;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStreamTypedOld;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStreamTypedOLD;

/**
 * Provides exact match filtering of LXP records based on a label and a value.
 * Created by al on 29/04/2014.
 */
public class ExactMatch extends Filter {

    private final String value;
    private final String label;

    public ExactMatch(final ILXPInputStreamTypedOld input, final ILXPOutputStreamTypedOLD output, final String label, final String value) {

        super(input, output);
        this.label = label;
        this.value = value;
    }

    public boolean select(final ILXP record) {

        return record.containsKey(label) && record.get(label).equals(value);
    }
}

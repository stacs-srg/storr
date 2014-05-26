package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.filter;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;

/**
 * Provides exact match filtering of LXP records based on a label and a value.
 * Created by al on 29/04/2014.
 */
public class ExactMatch extends Filter {

    private final String value;
    private final String label;

    /**
     * @param input
     * @param output
     * @param label
     * @param value
     */
    public ExactMatch(ILXPInputStream input, ILXPOutputStream output, String label, String value) {
        super(input, output);
        this.label = label;
        this.value = value;
    }

    public boolean select(ILXP record) {

        return record.containsKey( label ) && record.get( label ).equals( value );
    }
}

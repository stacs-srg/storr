package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.filter;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IFilter;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStreamTypedOld;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStreamTypedOLD;

/**
 * Created by al on 28/04/2014.
 */
public abstract class Filter<T extends ILXP> implements IFilter<T> {

    private final ILXPInputStreamTypedOld<T> input;
    private final ILXPOutputStreamTypedOLD<T> output;

    public Filter(final ILXPInputStreamTypedOld input, final ILXPOutputStreamTypedOLD output) {
        this.input = input;
        this.output = output;
    }

    public void apply() {

        for (T record : input) {
            if (select(record)) {
                output.add(record);
            }
        }
    }

    public ILXPInputStreamTypedOld getInput() {
        return input;
    }

    public ILXPOutputStreamTypedOLD getOutput() {
        return output;
    }
}

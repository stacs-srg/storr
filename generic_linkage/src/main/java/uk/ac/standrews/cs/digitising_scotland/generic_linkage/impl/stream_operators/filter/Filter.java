package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.filter;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IFilter;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;

/**
 * Created by al on 28/04/2014.
 */
public abstract class Filter implements IFilter {

    private final ILXPInputStream input;
    private final ILXPOutputStream output;

    public Filter(final ILXPInputStream input, final ILXPOutputStream output) {
        this.input = input;
        this.output = output;
    }

    public void apply() {

        for (ILXP record : input) {
            if (select(record)) {
                output.add(record);
            }
        }
    }

    public ILXPInputStream getInput() {
        return input;
    }

    public ILXPOutputStream getOutput() {
        return output;
    }
}

package uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.filter;

import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IFilter;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IInputStream;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IOutputStream;

/**
 * Created by al on 28/04/2014.
 */
public abstract class Filter<T extends ILXP> implements IFilter<T> {

    private final IInputStream<T> input;
    private final IOutputStream<T> output;

    public Filter(final IInputStream<T> input, final IOutputStream<T> output) {
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

    public IInputStream<T> getInput() {
        return input;
    }

    public IOutputStream<T> getOutput() {
        return output;
    }
}

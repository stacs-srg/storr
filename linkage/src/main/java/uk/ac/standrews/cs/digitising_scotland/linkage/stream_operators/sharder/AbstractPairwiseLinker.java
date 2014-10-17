package uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder;

import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IInputStream;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IOutputStream;
import uk.ac.standrews.cs.digitising_scotland.linkage.interfaces.IPair;
import uk.ac.standrews.cs.digitising_scotland.linkage.interfaces.IPairWiseLinker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by al on 21/05/2014.
 */
public abstract class AbstractPairwiseLinker<T extends ILXP> implements IPairWiseLinker<T> {

    private final IInputStream<T> input;
    private final IOutputStream<IPair<T>> output;

    public AbstractPairwiseLinker(final IInputStream<T> input, final IOutputStream<IPair<T>> output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void pairwiseLink() {

        List<T> records = new ArrayList<T>();

        for (T record : input) {
            records.add(record);
        }
        linkRecords(records);
    }

    /**
     * @param records a collection of b & d records for people with the same first name, last name, father's first name and mother's first name.
     */
    private void linkRecords(final List<T> records) {

        for (IPair<T> pair : allPairs(records)) {
            if (compare(pair)) {
                addToResults(pair, output);
            }
        }
    }

    private Iterable<IPair> allPairs(final List<T> records) {

        List<IPair> all = new ArrayList<>();

        ILXP[] recordsArray = records.toArray(new ILXP[0]);

        for (int i = 0; i < recordsArray.length; i++) {
            for (int j = i + 1; j < recordsArray.length; j++) {
                all.add(new Pair(recordsArray[i], recordsArray[j]));
            }
        }

        return all;
    }

    public abstract boolean compare(IPair<T> pair);

    /**
     * Adds a matched result to a result collection.
     *
     * @param pair
     */
    public abstract void addToResults(IPair pair, IOutputStream results);
}

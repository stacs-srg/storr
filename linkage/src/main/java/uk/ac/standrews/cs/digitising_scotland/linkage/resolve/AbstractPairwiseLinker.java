package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by al on 21/05/2014.
 */
public abstract class AbstractPairwiseLinker implements IPairWiseLinker {

    private final ILXPInputStream input;
    private final ILXPOutputStream output;

    public AbstractPairwiseLinker(final ILXPInputStream input, final ILXPOutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void pairwiseLink() {

        List<ILXP> records = new ArrayList<>();

        for (ILXP record : input) {

            if (record.get("TYPE") == null) {
                ErrorHandling.error("Found record with no type field: " + record);
            }
            records.add(record);
        }
        linkRecords(records);
    }

    /**
     * @param records a collection of b & d records for people with the same first name, last name, father's first name and mother's first name.
     */
    private void linkRecords(final List<ILXP> records) {

        for (Pair pair : allPairs(records)) {
            if (compare(pair)) {
                addToResults(pair, output);
            }
        }
    }

    private Iterable<Pair> allPairs(final List<ILXP> records) {

        List<Pair> all = new ArrayList<>();

        ILXP[] recordsArray = records.toArray(new ILXP[0]);

        for (int i = 0; i < recordsArray.length; i++) {
            for (int j = i + 1; j < recordsArray.length; j++) {
                all.add(new Pair(recordsArray[i], recordsArray[j]));
            }
        }

        return all;
    }

    public abstract boolean compare(Pair pair);

    /**
     * Adds a matched result to a result collection.
     *
     * @param pair
     */
    public abstract void addToResults(Pair pair, ILXPOutputStream results);
}

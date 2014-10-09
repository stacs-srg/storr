package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.dummies;

import java.util.ArrayList;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * Dummy record - fields set null and stubs with no implementation for all methods.
 * Created by fraserdunlop on 19/06/2014 at 10:48.
 */

public class DummyRecord extends Record {

    public DummyRecord() {

        super((int) Math.rint(Math.random() * 1000000), null);
    }

    @Override
    public OriginalData getOriginalData() {

        return null;
    }

    @Override
    public ArrayList<String> getDescription() {

        return null;
    }

    @Override
    public Set<Classification> getGoldStandardClassificationSet() {

        return null;
    }

    @Override
    public boolean isCoDRecord() {

        return true;
    }

    @Override
    public String toString() {

        return "";
    }

    @Override
    public Set<Classification> getClassifications() {

        return null;
    }

}

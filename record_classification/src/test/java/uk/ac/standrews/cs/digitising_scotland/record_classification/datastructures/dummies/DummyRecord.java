package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.dummies;

import java.util.Collection;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * Dummy record - fields set null and stubs with no implementation for all methods.
 * Created by fraserdunlop on 19/06/2014 at 10:48.
 */

public class DummyRecord extends Record {

    public DummyRecord() {

        super((int) Math.rint(Math.random() * 1000), null);
    }

    @Override
    public OriginalData getOriginalData() {

        return null;
    }

    @Override
    public String getCleanedDescription() {

        return null;
    }

    @Override
    public void setCleanedDescription(final String cleanedDescription) {

    }

    @Override
    public Set<CodeTriple> getGoldStandardClassificationSet() {

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
    public Set<CodeTriple> getCodeTriples() {

        return null;
    }

    @Override
    public void addCodeTriples(final CodeTriple codeTriples) {

    }

    @Override
    public void addAllCodeTriples(final Collection<CodeTriple> codeTriples) {

    }
}

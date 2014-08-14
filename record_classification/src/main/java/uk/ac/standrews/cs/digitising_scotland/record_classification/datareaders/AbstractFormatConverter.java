package uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders;

import java.io.File;
import java.io.IOException;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

public abstract class AbstractFormatConverter {

    public abstract List<Record> convert(final File inputFile) throws IOException, InputFormatException;

}

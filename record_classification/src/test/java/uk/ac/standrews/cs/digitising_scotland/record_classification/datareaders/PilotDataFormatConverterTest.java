package uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

public class PilotDataFormatConverterTest {

    @Test
    public void test() throws IOException, InputFormatException {

        File inputFile = new File(getClass().getResource("/pilotStudyTestCase.tsv").getFile());

        AbstractFormatConverter converter = new PilotDataFormatConverter();

        Bucket bucket = new Bucket(converter.convert(inputFile));

        Record record1 = bucket.getRecord(1);
        Record record2 = bucket.getRecord(2);
        Record record3 = bucket.getRecord(3);

        Assert.assertEquals("CARDIO VASCULAR DEGENERATION", record1.getDescription());
        Assert.assertEquals("RHEUMATOID ARTHRITIS PNEUMONIA CARDIAC FAILURE", record2.getDescription());
        Assert.assertEquals("SENILITY; CHRONIC BRONCHITIS; MYOCARDITIS", record3.getDescription());

    }
}

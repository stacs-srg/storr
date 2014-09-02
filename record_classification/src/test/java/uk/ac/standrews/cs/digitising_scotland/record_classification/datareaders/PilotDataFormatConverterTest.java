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

        PilotDataFormatConverter converter = new PilotDataFormatConverter();

        Bucket bucket = new Bucket(converter.convert(inputFile));

        Record record1 = bucket.getRecord(1);
        Record record2 = bucket.getRecord(2);
        Record record3 = bucket.getRecord(3);

        Assert.assertEquals("cardio vascular degeneration", record1.getDescription().get(0));
        Assert.assertEquals("rheumatoid arthritis pneumonia cardiac failure", record2.getDescription().get(0));
        Assert.assertEquals("senility; chronic bronchitis; myocarditis", record3.getDescription().get(0));

    }
}

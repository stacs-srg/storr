package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

public class RelatedFieldChangerTest {

    @Test
    public void testReplaceB() throws InputFormatException {

        RelatedFieldChanger rfc = new RelatedFieldChanger();
        Bucket bucket = new Bucket();
        List<String> description = new ArrayList<String>();
        String one = "BRONCHO PNEUMONIA";
        String two = "MULTIPLE FRACTURES";
        String three = "LUNG LACERATIONS DUE TO (B)";
        description.add(one);
        description.add(two);
        description.add(three);

        OriginalData originalData = new OriginalData(description, 2015, 1, "fileName");
        Record r = new Record(0, originalData);
        bucket.addRecordToBucket(r);
        rfc.updateRelatedFields(bucket);

        Assert.assertEquals("LUNG LACERATIONS DUE TO (B)<MULTIPLE FRACTURES>", bucket.getRecord(0).getDescription().get(2));
        System.out.println(bucket.getRecord(0));
    }

    @Test
    public void testReplaceA() throws InputFormatException {

        RelatedFieldChanger rfc = new RelatedFieldChanger();
        Bucket bucket = new Bucket();
        List<String> description = new ArrayList<String>();
        String one = "MULTIPLE FRACTURES";
        String two = "LUNG LACERATIONS DUE TO (A)";
        String three = "BRONCHO PNEUMONIA";
        description.add(one);
        description.add(two);
        description.add(three);

        OriginalData originalData = new OriginalData(description, 2015, 1, "fileName");
        Record r = new Record(0, originalData);
        bucket.addRecordToBucket(r);
        rfc.updateRelatedFields(bucket);

        Assert.assertEquals("LUNG LACERATIONS DUE TO (A)<MULTIPLE FRACTURES>", bucket.getRecord(0).getDescription().get(1));
        System.out.println(bucket.getRecord(0));
    }

}

package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

public class RelatedFieldChanger {

    public Bucket updateRelatedFields(final Bucket bucket) {

        Bucket updatedBucket = new Bucket();
        for (Record record : bucket) {
            record = processRecord(record);
            updatedBucket.addRecordToBucket(record);;
        }
        return updatedBucket;
    }

    private Record processRecord(final Record record) {

        String phrase = "";
        final List<String> dList = record.getDescription();
        for (String description : dList) {
            if (description.contains("DUE TO (A)")) {
                phrase = "(A)<" + dList.get(0) + ">";

            }
            if (description.contains("DUE TO (B)")) {
                phrase = "(B)<" + dList.get(1) + ">";

            }
            if (description.contains("DUE TO (C)")) {
                phrase = "(C)<" + dList.get(2) + ">";

            }
            String newDescription = description.replaceAll("(DUE TO \\([ABC]\\))", "DUE TO " + phrase);
            record.updateDescription(description, newDescription);
        }

        return record;
    }
}

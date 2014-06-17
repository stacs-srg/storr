package uk.ac.standrews.cs.digitising_scotland.parser.preprocessor;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;

/**
 * Reads a {@link Bucket} and performs data cleaning such as spelling correction and feature selection on the descriptions in each {@link Record}.
 * OriginalData.description is not changed, instead the cleanedDescripion field is populated.
 * @author jkc25, frjd2
 *
 */
public abstract class DataCleaning {

    /**
     * Currently a dummy method.
     * Copies original string to cleaned string.
     * @param bucketToClean bucket to perform cleaning on.
     * @return the bucket with cleaned records attached.
     */
    public static Bucket cleanData(final Bucket bucketToClean) {

        Bucket cleanedBucket = bucketToClean;
        //TODO dummy method, copies original data to cleaned data.
        for (Record record : cleanedBucket) {
            record.setCleanedDescription(record.getOriginalData().getDescription());
        }

        return cleanedBucket;
    }

}

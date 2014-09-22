package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.PilotDataFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;

/**
 * The Class PredictionBucketGenerator creates a bucket of records that are waiting to have predictions made on them,
 * ie they have no gold standard classifications.
 */
public class PredictionBucketGenerator {

    CodeDictionary codeDictionary;

    public PredictionBucketGenerator(final CodeDictionary codeDictionary) {

        this.codeDictionary = codeDictionary;
    }

    /**
     * Creates the prediction bucket from the given text file. This method currently expects the data to be in the form of the
     * pilot data. The source code should be updated if this changes.
     *
     * @param prediction file containing the records to be classified, one per line.
     * @return the bucket containing records to be classified
     */
    public Bucket createPredictionBucket(final File prediction) {

        Bucket toClassify = null;
        AbstractFormatConverter formatConverter = new PilotDataFormatConverter();

        try {
            toClassify = new Bucket(formatConverter.convert(prediction, codeDictionary));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return toClassify;
    }

    /**
     * Creates the prediction bucket from the given text file. This method takes an {@link AbstractFormatConverter} and
     * uses this to perform the record creation.
     *
     * @param prediction the file containing the prediction records
     * @param formatConverter the format converter to create records with
     * @return the bucket
     */
    public Bucket createPredictionBucket(final File prediction, final AbstractFormatConverter formatConverter) {

        Bucket toClassify = null;

        try {
            toClassify = new Bucket(formatConverter.convert(prediction, codeDictionary));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return toClassify;
    }

}

package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.PilotDataFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;

public class PredictionBucketGenerator {

    public Bucket createPredictionBucket(final File prediction) {

        Bucket toClassify = null;
        AbstractFormatConverter formatConverter = new PilotDataFormatConverter();

        try {
            toClassify = new Bucket(formatConverter.convert(prediction));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return toClassify;
    }

}

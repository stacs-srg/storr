package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;
import java.io.IOException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.PilotDataFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

public class PredictionBucketGenerator {

    public Bucket createPredictionBucket(final File prediction) {

        Bucket toClassify = null;
        try {
            toClassify = new Bucket(PilotDataFormatConverter.convert(prediction));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
      

        return toClassify;
    }

}

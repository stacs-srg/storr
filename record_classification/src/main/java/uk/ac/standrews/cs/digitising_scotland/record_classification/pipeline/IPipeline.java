package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;

public interface IPipeline {

    Bucket classify(final Bucket bucket, final boolean multipleClassifications) throws Exception;

    Bucket getClassified();

}

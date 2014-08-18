package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;

public class ClassificationHolder {

    private ExactMatchPipeline exactMatchPipeline;
    private MachineLearningClassificationPipeline machineLearningClassifier;

    private Bucket exactMatched;
    private Bucket notExactMatched;
    private Bucket machineLearned;
    private Bucket allClassified;

    public ClassificationHolder(final ExactMatchPipeline emp, final MachineLearningClassificationPipeline mlp) {

        this.exactMatchPipeline = emp;
        this.machineLearningClassifier = mlp;
        exactMatched = new Bucket();
        notExactMatched = new Bucket();
        machineLearned = new Bucket();
        allClassified = new Bucket();

    }

    public Bucket classify(Bucket predictionBucket) throws IOException {

        exactMatched = exactMatchPipeline.classify(predictionBucket);
        notExactMatched = BucketUtils.getComplement(predictionBucket, exactMatched);
        machineLearned = machineLearningClassifier.classify(notExactMatched);
        allClassified = BucketUtils.getUnion(machineLearned, exactMatched);
        return allClassified;
    }

    public Bucket getExactMatched() {

        return exactMatched;
    }

    public Bucket getMachineLearned() {

        return machineLearned;
    }

    public Bucket getAllClassified() {

        return allClassified;
    }

}

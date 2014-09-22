package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import org.apache.mahout.math.Matrix;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.OLR;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;

public class TrainOnNewDataTest {

    @Test
    @Ignore("not implemented yet")
    public void test() throws InterruptedException {

        OLR olrModel = new OLR();
        Matrix beta = olrModel.getBeta();
        Bucket bucket = null;
        CodeIndexer codeIndexer = new CodeIndexer();
        VectorFactory vectorFactory = new VectorFactory(bucket, codeIndexer);
        OLRClassifier olrClassifer = new OLRClassifier(vectorFactory);

        Bucket newBucket = null;
        olrClassifer.train(newBucket, beta);

    }

}

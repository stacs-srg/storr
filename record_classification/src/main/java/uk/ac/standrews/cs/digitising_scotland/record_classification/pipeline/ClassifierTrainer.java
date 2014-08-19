package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;

public class ClassifierTrainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierTrainer.class);

    private VectorFactory vectorFactory;
    private AbstractClassifier olrClassifier;
    private ExactMatchClassifier exactMatchClassifier;
    private Bucket trainingBucket;

    private String experimentalFolderName;

    public ClassifierTrainer(final Bucket trainingBucket, final String experimentalFolderName) {

        vectorFactory = new VectorFactory(trainingBucket);
        this.trainingBucket = trainingBucket;
        this.experimentalFolderName = experimentalFolderName;

    }

    public ExactMatchClassifier trainExactMatchClassifier() throws Exception {

        LOGGER.info("********** Creating Lookup Tables **********");

        exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(experimentalFolderName + "/Models/lookupTable");
        exactMatchClassifier.train(trainingBucket);
        return exactMatchClassifier;
    }

    public AbstractClassifier trainOLRClassifier() throws Exception {

        LOGGER.info("********** Training OLR Classifiers **********");
        olrClassifier = new OLRClassifier(vectorFactory);
        OLRClassifier.setModelPath(experimentalFolderName + "/Models/olrModel");
        olrClassifier.train(trainingBucket);
        return olrClassifier;
    }

    public VectorFactory getVectorFactory() {

        return vectorFactory;
    }

    public AbstractClassifier getOlrClassifier() {

        return olrClassifier;
    }

    public ExactMatchClassifier getExactMatchClassifier() {

        return exactMatchClassifier;
    }

}

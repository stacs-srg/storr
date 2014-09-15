package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;

/**
 * The Class ClassifierTrainer contains methods for training both {@link OLRClassifier} and {@link ExactMatchClassifier} objects.
 * 
 */
public class ClassifierTrainer {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierTrainer.class);

    /** The vector factory. */
    private VectorFactory vectorFactory;

    /** The olr classifier. */
    private OLRClassifier olrClassifier;

    /** The exact match classifier. */
    private ExactMatchClassifier exactMatchClassifier;

    /** The training bucket. */
    private Bucket trainingBucket;

    /** The experimental folder name. */
    private String experimentalFolderName;

    /**
     * Instantiates a new classifier trainer with a bucket containing training data and the folder where models should be written to.
     *
     * @param trainingBucket the training bucket
     * @param experimentalFolderName the experimental folder name
     */
    public ClassifierTrainer(final Bucket trainingBucket, final String experimentalFolderName) {

        this.trainingBucket = trainingBucket;
        this.experimentalFolderName = experimentalFolderName;
        vectorFactory = new VectorFactory(trainingBucket);

    }

    /**
     * Trains an exact match classifier with data from the training bucket. The model is written to the experimental folder name
     * /Models/lookupTable.
     *
     * @return the exact match classifier with a build lookup table/model
     * @throws Exception the exception
     */
    public ExactMatchClassifier trainExactMatchClassifier() throws Exception {

        LOGGER.info("********** Creating Lookup Tables **********");

        exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(experimentalFolderName + "/Models/lookupTable");
        exactMatchClassifier.train(trainingBucket);
        return exactMatchClassifier;
    }

    /**
     * Trains an online logistic regression (OLR) classifier. The model is written to the experimental folder
     * + "/Models/olrModel".
     *
     * @return the olrClassifier classifier after training
     * @throws Exception the exception
     */
    public OLRClassifier trainOLRClassifier() throws Exception {

        LOGGER.info("********** Training OLR Classifiers **********");
        olrClassifier = new OLRClassifier(vectorFactory);
        OLRClassifier.setModelPath(experimentalFolderName + "/Models/olrModel");
        olrClassifier.train(trainingBucket);
        return olrClassifier;
    }

    /**
     * Instansiates an {@link ExactMatchClassifier} and a {@link OLRClassifier} from models stored on disk.
     * The model locations should be in "/lookupTable" and "/olrModel" from modelLocations path.
     * @param modelLocations Path to parent directory of pre-built models.
     */
    public void getExistingsModels(final String modelLocations) {

        exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(modelLocations + "/lookupTable");
        exactMatchClassifier.getModelFromDefaultLocation();
        olrClassifier = new OLRClassifier(new VectorFactory());
        OLRClassifier.setModelPath(modelLocations + "/olrModel");
        olrClassifier = olrClassifier.getModelFromDefaultLocation();

    }

    /**
     * Gets the vector factory that was used in vector creation.
     *
     * @return the vector factory
     */
    public VectorFactory getVectorFactory() {

        return vectorFactory;
    }

    /**
     * Gets the olr classifier.
     *
     * @return the olr classifier
     */
    public AbstractClassifier getOlrClassifier() {

        return olrClassifier;
    }

    /**
     * Gets the exact match classifier.
     *
     * @return the exact match classifier
     */
    public ExactMatchClassifier getExactMatchClassifier() {

        return exactMatchClassifier;
    }

}

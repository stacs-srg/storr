package uk.ac.standrews.cs.usp.parser.classifiers;

import java.io.IOException;
import java.util.Properties;

import uk.ac.standrews.cs.usp.parser.classifiers.OLR.OLRClassifier;
import uk.ac.standrews.cs.usp.parser.datastructures.Bucket;
import uk.ac.standrews.cs.usp.parser.datastructures.Record;
import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;
import uk.ac.standrews.cs.usp.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.usp.parser.resolver.Pair;
import uk.ac.standrews.cs.usp.tools.configuration.MachineLearningConfiguration;

/**
 * Implements the best confidence voting scheme for machine learning classifiers.
 * @author jkc25
 *
 */
public class BestConfidenceClassifier extends AbstractClassifier {

    private Properties properties;
    private NaiveBayesClassifier naiveBayesClassifier;
    private OLRClassifier olrClassifier;

    /**
     * Constructs a {@link BestConfidenceClassifier} with the default properties file.
     * @param vectorFactory vector factory
     */
    public BestConfidenceClassifier(final VectorFactory vectorFactory) {

        this.properties = MachineLearningConfiguration.getDefaultProperties();
        this.naiveBayesClassifier = new NaiveBayesClassifier(vectorFactory);
        this.olrClassifier = new OLRClassifier(vectorFactory);
    }

    /**
     * Constructs a {@link BestConfidenceClassifier} with a custom properties file that extends the default.
     * @param vectorFactory vector factory
     * @param customPropertiesFile String with path to custom {@link Properties} file
     */
    public BestConfidenceClassifier(final String customPropertiesFile, final VectorFactory vectorFactory) {

        MachineLearningConfiguration mlc = new MachineLearningConfiguration();
        this.properties = mlc.extendDefaultProperties(customPropertiesFile);
        this.naiveBayesClassifier = new NaiveBayesClassifier(customPropertiesFile, vectorFactory);
        this.olrClassifier = new OLRClassifier(customPropertiesFile, vectorFactory);
    }

    @Override
    public void train(final Bucket bucket) throws Exception {

        naiveBayesClassifier.train(bucket);
        olrClassifier.train(bucket);
    }

    @Override
    public Record classify(final Record record) throws IOException {

        Record classifiedRecord = record;
        classifiedRecord = naiveBayesClassifier.classify(classifiedRecord);
        classifiedRecord = olrClassifier.classify(classifiedRecord);

        return classifiedRecord;
    }

    /**
     * Classifies all the {@link Record}s in a {@link Bucket} and returns a bucket containing those records.
     * @param bucket to {@link Bucket} to classify
     * @return Bucket with classified vectors
     * @throws IOException Problem reading from disk
     */
    public Bucket classify(final Bucket bucket) throws IOException {

        Bucket classifiedBucket = new Bucket();
        for (Record record : bucket) {
            classifiedBucket.addRecordToBucket(classify(record));
        }

        return classifiedBucket;
    }

    @Override
    public void getModelFromDefaultLocation() {

        olrClassifier.getModelFromDefaultLocation();
        naiveBayesClassifier.getModelFromDefaultLocation();

    }

    @Override
    public Pair<Code, Double> classify(final TokenSet tokenSet) throws IOException {

        Pair<Code, Double> nbPair = naiveBayesClassifier.classify(tokenSet);
        Pair<Code, Double> olrPair = olrClassifier.classify(tokenSet);

        if (nbPair.getRight() > olrPair.getRight()) {
            return nbPair;
        }
        else {
            return olrPair;
        }
    }
}

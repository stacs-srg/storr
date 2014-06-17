package uk.ac.standrews.cs.digitising_scotland.parser.pipeline;

import java.io.File;
import java.io.IOException;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.ClassificationPipeline;
import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.NaiveBayesClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.OLR.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.lookup.NGramClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.preprocessor.DataCleaning;

// TODO: Auto-generated Javadoc
/**
 * The Class Classifier.
 */
public class Classifier {

    /** The file to classify. */
    private final File fileToClassify;

    /** The vector factory. */
    private final VectorFactory vectorFactory;

    /**
     * Instantiates a new classifier.
     *
     * @param fileToClassify the file to classify
     * @param vectorFactory the vector factory
     */
    public Classifier(final File fileToClassify, final VectorFactory vectorFactory) {

        this.fileToClassify = fileToClassify;
        this.vectorFactory = vectorFactory;
    }

    /**
     * Classifies the bucket using previously trained models that must be stored in the default locations.
     *
     * @return the bucket after classification.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    public Bucket classifyBucket() throws IOException, InputFormatException {

        Bucket toClassify = new Bucket(RecordFactory.makeUnCodedRecordsFromFile(fileToClassify));
        DataCleaning.cleanData(toClassify);

        ClassificationPipeline pipeLine = new ClassificationPipeline();
        AbstractClassifier exactMatch = new ExactMatchClassifier();
        AbstractClassifier nGrams = new NGramClassifier();
        AbstractClassifier bayes = new NaiveBayesClassifier(vectorFactory);
        AbstractClassifier olr = new OLRClassifier(vectorFactory);

        exactMatch.getModelFromDefaultLocation();
        nGrams.getModelFromDefaultLocation();
        bayes.getModelFromDefaultLocation();
        olr.getModelFromDefaultLocation();

        pipeLine.addTrainedClassifier(exactMatch);
        pipeLine.addTrainedClassifier(nGrams);
        pipeLine.addTrainedClassifier(bayes);
        pipeLine.addTrainedClassifier(olr);

        //toClassify.generateVectors(toClassify);

        pipeLine.classifyBucket(toClassify);

        System.out.println(toClassify);
        return toClassify;
    }

    /**
     * The main method and entry point when the user wants to classify records from previously trained models.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    public static void main(final String[] args) throws IOException, InputFormatException {

        if (args.length != 1) {
            System.err.println("Please supply a file to be classified. Usage: Classify <fileToClassify.txt>");
            throw new RuntimeException();
        }

        VectorFactory vectorFactory = new VectorFactory(); //FIXME this is empty

        File fileToClassify = new File(args[args.length - 1]);
        Classifier classifier = new Classifier(fileToClassify, vectorFactory);
        classifier.classifyBucket();
    }
}

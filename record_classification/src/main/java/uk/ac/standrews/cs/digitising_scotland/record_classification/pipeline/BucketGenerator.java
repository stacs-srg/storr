package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.LongFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.PilotDataFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

/**
 * The Class TrainingBucketGenerator.
 */
public class BucketGenerator {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BucketGenerator.class);

    /** The Abstract Format converter. This is set to be an instance of {@link LongFormatConverter} by default.
     *  This can be changed as and when is necessary in the future.
     */
    private static AbstractFormatConverter formatConverter = new LongFormatConverter();

    private CodeDictionary codeDictionary;

    public BucketGenerator(final CodeDictionary codeDictionary) {

        this.codeDictionary = codeDictionary;
    }

    /**
     * Generates a bucket of training records (with gold standard codes) from the given training file.
     * The file should be either in the short NRS format or in the format the matches the {@link AbstractFormatConverter}
     * specified in the class. Set to {@link LongFormatConverter} as  default.
     *
     * @param trainingFile the training file to generate the records and train the models from
     * @return the bucket that will be populated
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     * @throws CodeNotValidException 
     */
    public Bucket generateTrainingBucket(final File trainingFile) throws IOException, InputFormatException, CodeNotValidException {

        LOGGER.info("********** Generating Training Bucket **********");

        return createTrainingBucket(trainingFile);

    }

    /**
     * Creates the bucket of records.
     * 
     * This method checks the file format of the input file and calls the correct method to parse the file into records objects.
     * These records are then added to the bucket that is returned from the method.
     *
     * @param training the training file
     * @return the bucket to be populated
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     * @throws CodeNotValidException
     */
    private Bucket createTrainingBucket(final File training) throws IOException, InputFormatException, CodeNotValidException {

        Bucket bucket = new Bucket();
        Iterable<Record> records;
        boolean longFormat = PipelineUtils.checkFileType(training);

        if (longFormat) {
            records = formatConverter.convert(training, codeDictionary);
        }
        else {
            records = RecordFactory.makeCodedCodRecordsFromFile(training, codeDictionary);
        }

        bucket.addCollectionOfRecords(records);

        return bucket;
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
            LOGGER.error(e.getMessage(), e.getCause());
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
            LOGGER.error(e.getMessage(), e.getCause());
        }

        return toClassify;
    }

}

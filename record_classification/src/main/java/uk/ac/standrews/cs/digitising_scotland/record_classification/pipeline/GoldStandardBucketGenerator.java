package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.LongFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

/**
 * The Class TrainingBucketGenerator.
 */
public class GoldStandardBucketGenerator {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GoldStandardBucketGenerator.class);

    /** The Abstract Format converter. This is set to be an instance of {@link LongFormatConverter} by default.
     *  This can be changed as and when is necessary in the future.
     */
    private static AbstractFormatConverter formatConverter = new LongFormatConverter();

    private CodeDictionary codeDictionary;

    public GoldStandardBucketGenerator(final CodeDictionary codeDictionary) {

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
    public Bucket generate(final File trainingFile) throws IOException, InputFormatException, CodeNotValidException {

        LOGGER.info("********** Generating Training Bucket **********");

        return createBucketOfRecords(trainingFile);

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
    private Bucket createBucketOfRecords(final File training) throws IOException, InputFormatException, CodeNotValidException {

        Bucket bucket = new Bucket();
        Iterable<Record> records;
        boolean longFormat = PipelineUtils.checkFileType(training);

        if (longFormat) {
            records = formatConverter.convert(training, codeDictionary);
        }
        else {
            records = RecordFactory.makeCodedRecordsFromFile(training, codeDictionary);
        }

        bucket.addCollectionOfRecords(records);

        return bucket;
    }

}

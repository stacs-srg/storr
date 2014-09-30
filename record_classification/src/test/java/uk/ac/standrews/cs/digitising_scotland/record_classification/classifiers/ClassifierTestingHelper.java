package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * The Class ClassifierTestingHelper.
 */
public class ClassifierTestingHelper {

    /** The testing bucket. */
    private Bucket testingBucket;

    private CodeDictionary codeDictionary;

    /**
     * Instantiates a new classifier testing helper.
     */
    public ClassifierTestingHelper() {

    }

    /**
     * Populates all records in the bucket with the {@link Code} 2100.
     * Use for testing only.
     *
     * @param bucket Bucket to populate
     * @return bucket with gold standard codes
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     * @throws URISyntaxException the URI syntax exception
     */
    public Bucket giveBucketTestingOccCodes(final Bucket bucket) throws IOException, CodeNotValidException, URISyntaxException {

        loadDictionary("/CodeFactoryTestFile.txt");
        int c = 0;
        int size = bucket.size();
        for (Record record : bucket) {
            if (c < (size / 2.0)) {
                record = addGoldStandardCodeToRecord(record, "2200");
            }
            else {
                record = addGoldStandardCodeToRecord(record, "2100");

            }
            c++;

        }

        return bucket;
    }

    /**
     * Populates all records in the bucket with the {@link Code} 2100.
     * Use for testing only.
     *
     * @param bucket Bucket to populate
     * @return bucket with gold standard codes
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     * @throws URISyntaxException the URI syntax exception
     */
    public Bucket giveBucketTestingOccClassificationTriples(final Bucket bucket) throws IOException, CodeNotValidException, URISyntaxException {

        for (Record record : bucket) {
            loadDictionary("/CodeFactoryTestFile.txt");
            record = addGoldStandardCodeToRecord(record, "2200");
            addCodeTriplesStandardCodeToRecord(record, "2200");
        }

        return bucket;
    }

    /**
     * Give bucket testing cod codes.
     *
     * @param bucket the bucket
     * @return the bucket
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException
     */
    public Bucket giveBucketTestingCODCodes(final Bucket bucket) throws URISyntaxException, IOException {

        for (Record record : bucket) {
            loadDictionary("/CodeFactoryCoDFile.txt");
            addCodeTriplesStandardCodeToRecord(record, "R99");
        }

        return bucket;
    }

    /**
     * Give bucket testing hicod codes.
     *
     * @param bucket the bucket
     * @param code the code to give to the records in the bucket
     * @return the bucket
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException
     */
    public Bucket giveBucketTestingHICODCodes(final Bucket bucket, final String code) throws URISyntaxException, IOException {

        for (Record record : bucket) {
            loadDictionary("/CodeFactoryCoDFile.txt");
            addCodeTriplesStandardCodeToRecord(record, code);
        }

        return bucket;
    }

    //====================================================================================================

    /**
     * Load dictionary.
     *
     * @param codeDictionaryFile the code dictionary file
     * @throws URISyntaxException the URI syntax exception
    s
     */
    private void loadDictionary(final String codeDictionaryFile) throws URISyntaxException, IOException {

        File file = new File(this.getClass().getResource(codeDictionaryFile).toURI());
        codeDictionary = new CodeDictionary(file);
    }

    /**
     * Adds the gold standard code to classification set.
     *
     * @param record the record
     * @param goldStandardCode the gold standard code
     * @return the record
     */
    private Record addGoldStandardCodeToRecord(final Record record, final String goldStandardCode) {

        Code code = null;
        try {
            code = codeDictionary.getCode(goldStandardCode);
        }
        catch (CodeNotValidException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Classification c = new Classification(code, new TokenSet(record.getOriginalData().getDescription()), 1.0);
        Set<Classification> set = new HashSet<>();
        set.add(c);
        record.getOriginalData().setGoldStandardClassification(set);
        return record;
    }

    /**
     * Adds the gold standard code to classification set.
     *
     * @param record the record
     * @param codeAsString the code as string
     * @return the record
     */
    private Record addCodeTriplesStandardCodeToRecord(final Record record, final String codeAsString) {

        Code code = null;
        try {
            code = codeDictionary.getCode(codeAsString);
        }
        catch (CodeNotValidException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Classification c = new Classification(code, new TokenSet(record.getOriginalData().getDescription()), 1.0);
        Set<Classification> set = new HashSet<>();
        set.add(c);
        record.getListOfClassifications().put(record.getOriginalData().getDescription().get(0), c);
        return record;
    }

    /**
     * Gets the testing bucket.
     *
     * @return the testing bucket
     */
    public Bucket getTestingBucket() {

        return testingBucket;
    }

    /**
     * Sets the testing bucket.
     *
     * @param testingBucket the new testing bucket
     */
    public void setTestingBucket(final Bucket testingBucket) {

        this.testingBucket = testingBucket;
    }

    /**
     * Gets the training bucket.
     *
     * @param fileName the file name
     * @return the training bucket
     * @throws Exception the exception
     */
    public Bucket getTrainingBucket(final String fileName) throws Exception {

        Bucket bucketB;
        System.out.println(new File(getClass().getResource(fileName).getFile()).exists());
        File inputFileTraining = new File(getClass().getResource(fileName).getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        bucketB = new Bucket(listOfRecordsTraining);
        // bucketB = giveBucketTestingOccCodes(bucketB);
        return bucketB;
    }

}

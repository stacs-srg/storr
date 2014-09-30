package uk.ac.standrews.cs.digitising_scotland.record_classification.writers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.ClassifierTestingHelper;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;

public class HumanReadableWriterTest {

    /** The helper. */
    private ClassifierTestingHelper helper = new ClassifierTestingHelper();

    private String delimiter = "\t";

    /** The Constant OCCBUCKET. */
    private static final String OCCBUCKET = "target/OccRecordHumanWriteTest.txt";

    /** The Constant CODDATA. */
    private static final String CODDATA = "/DataClerkingWriterTestCOD.txt";

    /** The Constant CODBUCKET. */
    private static final String CODBUCKET = "target/CODRecordWriteTest.txt";

    /** The Constant HICODBUCKET. */
    private static final String HICODBUCKET = "target/HICODRecordWriteTest.txt";

    /** The Constant MULTICODBUCKET. */
    private static final String MULTICODBUCKET = "target/MultipleCODRecordWriteTest.txt";

    @Before
    public void setUp() {

        RecordFactory.resetIdCount();

    }

    /**
     * Clean up.
     */
    @AfterClass
    public static void cleanUp() {

        File file = new File(OCCBUCKET);
        Assert.assertTrue(file.delete());
        file = new File(CODBUCKET);
        Assert.assertTrue(file.delete());
        file = new File(HICODBUCKET);
        Assert.assertTrue(file.delete());
        file = new File(MULTICODBUCKET);
        Assert.assertTrue(file.delete());
        RecordFactory.resetIdCount();
    }

    /**
     * Test write occupation records with codes.
     *
     * @throws Exception the exception
     */
    @Test
    public void testWriteOcc() throws Exception {

        String occDataFile = "/HumanReadableWriterTestOcc.txt";
        File writeFile = createAndWriteOccBucketWithClassificationTriplesToFile(OCCBUCKET, occDataFile);
        String correctOccBucketFile = "/OccHumanRecordWriteCorrect.txt";
        checkFileAgainstKnownCorrect(correctOccBucketFile, writeFile);
    }

    /**
     * Test write cod.
     *
     * @throws Exception the exception
     */
    @Test
    public void testWriteCOD() throws Exception {

        File writeFile = createAndWriteCODBucketToFile(CODBUCKET, CODDATA);
        String correctCODBucketFile = "/CODHumanRecordWriteCorrect.txt";
        checkFileAgainstKnownCorrect(correctCODBucketFile, writeFile);
    }

    /**
     * Test write hicod.
     *
     * @throws Exception the exception
     */
    @Test
    public void testWriteHICOD() throws Exception {

        File writeFile = createAndWriteHICODBucketToFile(HICODBUCKET, CODDATA);
        String correctHICODBucketFile = "/HICODHumanRecordWriteCorrect.txt";
        checkFileAgainstKnownCorrect(correctHICODBucketFile, writeFile);
    }

    /**
     * Test write multiple cod.
     *
     * @throws Exception the exception
     */
    @Test
    public void testWriteMultipleCOD() throws Exception {

        File writeFile = createAndWriteMultipleCODBucketToFile(MULTICODBUCKET, CODDATA);
        String correctMultipleCODBucketFile = "/MultipleCODHumanWriteCorrect.txt";
        checkFileAgainstKnownCorrect(correctMultipleCODBucketFile, writeFile);
    }

    /**
     * Creates the and write multiple cod bucket to file.
     *
     * @param writeFileName the write file name
     * @param readFileName the read file name
     * @return the file
     * @throws Exception the exception
     */
    private File createAndWriteMultipleCODBucketToFile(final String writeFileName, final String readFileName) throws Exception {

        File writeFile = new File(writeFileName);
        HumanReadableWriter humanReadableWriter = new HumanReadableWriter(writeFile, delimiter);
        Bucket bucket = helper.getTrainingBucket(readFileName);
        bucket = addMultipleCODCodes(bucket);
        writeToFile(humanReadableWriter, bucket);
        return writeFile;
    }

    /**
     * Creates the and write hicod bucket to file.
     *
     * @param writeFileName the write file name
     * @param readFileName the read file name
     * @return the file
     * @throws Exception the exception
     */
    private File createAndWriteHICODBucketToFile(final String writeFileName, final String readFileName) throws Exception {

        File writeFile = new File(writeFileName);
        HumanReadableWriter humanReadableWriter = new HumanReadableWriter(writeFile, delimiter);
        Bucket bucket = helper.getTrainingBucket(readFileName);
        bucket = addHICODCodes(bucket);
        writeToFile(humanReadableWriter, bucket);
        return writeFile;
    }

    /**
     * Creates the and write occ bucket to file.
     *
     * @param writeFileName the write file name
     * @param readFileName the read file name
     * @return the file
     * @throws Exception the exception
     */
    private File createAndWriteOccBucketWithClassificationTriplesToFile(final String writeFileName, final String readFileName) throws Exception {

        File writeFile = new File(writeFileName);
        HumanReadableWriter humanReadableWriter = new HumanReadableWriter(writeFile, delimiter);
        Bucket bucket = helper.getTrainingBucket(readFileName);
        bucket = addOccCodes(bucket);
        bucket = addOccClassificationTriples(bucket);
        writeToFile(humanReadableWriter, bucket);
        return writeFile;
    }

    /**
     * Creates the and write cod bucket to file.
     *
     * @param writeFileName the write file name
     * @param readFileName the read file name
     * @return the file
     * @throws Exception the exception
     */
    private File createAndWriteCODBucketToFile(final String writeFileName, final String readFileName) throws Exception {

        File writeFile = new File(writeFileName);
        HumanReadableWriter humanReadableWriter = new HumanReadableWriter(writeFile, delimiter);
        Bucket bucket = helper.getTrainingBucket(readFileName);
        bucket = addCODCodes(bucket);
        writeToFile(humanReadableWriter, bucket);
        return writeFile;
    }

    /**
     * Check file against known correct.
     *
     * @param correctFileName the correct file name
     * @param writeFile the write file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void checkFileAgainstKnownCorrect(final String correctFileName, final File writeFile) throws IOException {

        File correctFile = new File(getClass().getResource(correctFileName).getFile());
        byte[] f1 = Files.readAllBytes(writeFile.toPath());
        byte[] f2 = Files.readAllBytes(correctFile.toPath());
        Assert.assertArrayEquals(f2, f1);
    }

    /**
     *
     * @param bucket the bucket
     * @return the bucket
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException 
     */
    private Bucket addCODCodes(final Bucket bucket) throws URISyntaxException, IOException {

        return helper.giveBucketTestingCODCodes(bucket);
    }

    /**
     * Adds the hicod codes.
     *
     * @param bucket the bucket
     * @return the bucket
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException 
     */
    private Bucket addHICODCodes(final Bucket bucket) throws URISyntaxException, IOException {

        return helper.giveBucketTestingHICODCodes(bucket, "I6191");
    }

    /**
     * Adds the multiple cod codes.
     *
     * @param bucket the bucket
     * @return the bucket
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException 
     */
    private Bucket addMultipleCODCodes(final Bucket bucket) throws URISyntaxException, IOException {

        String code = "R99";
        helper.giveBucketTestingHICODCodes(bucket, code);

        code = "I6191";
        helper.giveBucketTestingHICODCodes(bucket, code);

        code = "X59";
        helper.giveBucketTestingHICODCodes(bucket, code);

        return bucket;
    }

    /**
     * Adds the occ codes.
     *
     * @param bucket the bucket
     * @return the bucket
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     * @throws URISyntaxException the URI syntax exception
     */
    private Bucket addOccCodes(final Bucket bucket) throws IOException, CodeNotValidException, URISyntaxException {

        return helper.giveBucketTestingOccCodes(bucket);
    }

    /**
     * Adds the occ codes.
     *
     * @param bucket the bucket
     * @return the bucket
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     * @throws URISyntaxException the URI syntax exception
     */
    private Bucket addOccClassificationTriples(final Bucket bucket) throws IOException, CodeNotValidException, URISyntaxException {

        return helper.giveBucketTestingOccClassificationTriples(bucket);
    }

    /**
     * Write to file.
     *
     * @param HumanReadableWriter the data clerking writer
     * @param bucket the bucket
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeToFile(final HumanReadableWriter HumanReadableWriter, final Bucket bucket) throws IOException {

        for (Record record : bucket) {
            HumanReadableWriter.write(record);
        }
        HumanReadableWriter.close();
    }

}

package uk.ac.standrews.cs.digitising_scotland.record_classification.writers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.ClassifierTestingHelper;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;

public class DataClerkingWriterTest {

    private ClassifierTestingHelper helper = new ClassifierTestingHelper();
    private static final String OCCBUCKET = "target/OccRecordWriteTest.txt";
    private static final String CODDATA = "/DataClerkingWriterTestCOD.txt";
    private static final String CODBUCKET = "target/CODRecordWriteTest.txt";
    private static final String HICODBUCKET = "target/HICODRecordWriteTest.txt";
    private static final String MULTICODBUCKET = "target/MultipleCODRecordWriteTest.txt";

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
    }

    @Test
    public void testWriteOcc() throws Exception {

        String occDataFile = "/DataClerkingWriterTestOcc.txt";
        File writeFile = createAndWriteOccBucketToFile(OCCBUCKET, occDataFile);
        String correctOccBucketFile = "/OccRecordWriteCorrect.txt";
        //     checkFileAgainstKnownCorrect(correctOccBucketFile, writeFile);
    }

    @Test
    public void testWriteCOD() throws Exception {

        File writeFile = createAndWriteCODBucketToFile(CODBUCKET, CODDATA);
        String correctCODBucketFile = "/CODRecordWriteCorrect.txt";
        checkFileAgainstKnownCorrect(correctCODBucketFile, writeFile);
    }

    @Test
    public void testWriteHICOD() throws Exception {

        File writeFile = createAndWriteHICODBucketToFile(HICODBUCKET, CODDATA);
        String correctHICODBucketFile = "/HICODRecordWriteCorrect.txt";
        checkFileAgainstKnownCorrect(correctHICODBucketFile, writeFile);
    }

    @Test
    public void testWriteMultipleCOD() throws Exception {

        File writeFile = createAndWriteMultipleCODBucketToFile(MULTICODBUCKET, CODDATA);
        String correctMultipleCODBucketFile = "/MultipleCODWriteCorrect.txt";
        checkFileAgainstKnownCorrect(correctMultipleCODBucketFile, writeFile);
    }

    private File createAndWriteMultipleCODBucketToFile(final String writeFileName, final String readFileName) throws Exception {

        File writeFile = new File(writeFileName);
        DataClerkingWriter dataClerkingWriter = new DataClerkingWriter(writeFile);
        Bucket bucket = helper.getTrainingBucket(readFileName);
        bucket = addMultipleCODCodes(bucket);
        writeToFile(dataClerkingWriter, bucket);
        return writeFile;
    }

    private File createAndWriteHICODBucketToFile(final String writeFileName, final String readFileName) throws Exception {

        File writeFile = new File(writeFileName);
        DataClerkingWriter dataClerkingWriter = new DataClerkingWriter(writeFile);
        Bucket bucket = helper.getTrainingBucket(readFileName);
        bucket = addHICODCodes(bucket);
        writeToFile(dataClerkingWriter, bucket);
        return writeFile;
    }

    private File createAndWriteOccBucketToFile(final String writeFileName, final String readFileName) throws Exception {

        File writeFile = new File(writeFileName);
        DataClerkingWriter dataClerkingWriter = new DataClerkingWriter(writeFile);
        Bucket bucket = helper.getTrainingBucket(readFileName);
        bucket = addOccCodes(bucket);
        writeToFile(dataClerkingWriter, bucket);
        return writeFile;
    }

    private File createAndWriteCODBucketToFile(final String writeFileName, final String readFileName) throws Exception {

        File writeFile = new File(writeFileName);
        DataClerkingWriter dataClerkingWriter = new DataClerkingWriter(writeFile);
        Bucket bucket = helper.getTrainingBucket(readFileName);
        bucket = addCODCodes(bucket);
        writeToFile(dataClerkingWriter, bucket);
        return writeFile;
    }

    private void checkFileAgainstKnownCorrect(final String correctFileName, final File writeFile) throws IOException {

        File correctFile = new File(getClass().getResource(correctFileName).getFile());
        byte[] f1 = Files.readAllBytes(writeFile.toPath());
        byte[] f2 = Files.readAllBytes(correctFile.toPath());
        Assert.assertArrayEquals(f2, f1);
    }

    private Bucket addCODCodes(final Bucket bucket) throws URISyntaxException {

        return helper.giveBucketTestingCODCodes(bucket);
    }

    private Bucket addHICODCodes(final Bucket bucket) throws URISyntaxException {

        return helper.giveBucketTestingHICODCodes(bucket, "I6191");
    }

    private Bucket addMultipleCODCodes(final Bucket bucket) throws URISyntaxException {

        String code = "R99";
        helper.giveBucketTestingHICODCodes(bucket, code);

        code = "I6191";
        helper.giveBucketTestingHICODCodes(bucket, code);

        code = "X59";
        helper.giveBucketTestingHICODCodes(bucket, code);

        return bucket;
    }

    private Bucket addOccCodes(final Bucket bucket) throws IOException, CodeNotValidException, URISyntaxException {

        return helper.giveBucketTestingOccCodes(bucket);
    }

    private void writeToFile(final DataClerkingWriter dataClerkingWriter, final Bucket bucket) throws IOException {

        for (Record record : bucket) {
            dataClerkingWriter.write(record);
        }
        dataClerkingWriter.close();
    }
}

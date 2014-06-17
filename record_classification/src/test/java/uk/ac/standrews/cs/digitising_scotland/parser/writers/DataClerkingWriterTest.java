package uk.ac.standrews.cs.digitising_scotland.parser.writers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.ClassifierTestingHelper;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.parser.writers.DataClerkingWriter;

public class DataClerkingWriterTest {

    private ClassifierTestingHelper helper = new ClassifierTestingHelper();
    private static final String occBucketFile = "target/OccRecordWriteTest.txt";
    private static final String codDataFile = "/DataClerkingWriterTestCOD.txt";
    static final String codBucketFile = "target/CODRecordWriteTest.txt";
    private static final String hicodBucketFile = "target/HICODRecordWriteTest.txt";
    private static final String multipleCODBucketFile = "target/MultipleCODRecordWriteTest.txt";

    @AfterClass
    public static void cleanUp() {

        //File file = new File(occBucketFile);
        //    Assert.assertTrue(file.delete());
        //        file = new File(codBucketFile);
        //        Assert.assertTrue(file.delete());
        //        file = new File(hicodBucketFile);
        //        Assert.assertTrue(file.delete());
        //        file = new File(multipleCODBucketFile);
        // Assert.assertTrue(file.delete());
    }

    @Test
    public void testWriteOcc() throws Exception {

        String occDataFile = "/DataClerkingWriterTestOcc.txt";
        File writeFile = createAndWriteOccBucketToFile(occBucketFile, occDataFile);
        String correctOccBucketFile = "/OccRecordWriteCorrect.txt";
        //FIXME   checkFileAgainstKnownCorrect(correctOccBucketFile, writeFile);
    }

    @Test
    public void testWriteCOD() throws Exception {

        File writeFile = createAndWriteCODBucketToFile(codBucketFile, codDataFile);
        String correctCODBucketFile = "/CODRecordWriteCorrect.txt";
        checkFileAgainstKnownCorrect(correctCODBucketFile, writeFile);
    }

    @Test
    public void testWriteHICOD() throws Exception {

        File writeFile = createAndWriteHICODBucketToFile(hicodBucketFile, codDataFile);
        String correctHICODBucketFile = "/HICODRecordWriteCorrect.txt";
        checkFileAgainstKnownCorrect(correctHICODBucketFile, writeFile);
    }

    @Test
    public void testWriteMultipleCOD() throws Exception {

        File writeFile = createAndWriteMultipleCODBucketToFile(multipleCODBucketFile, codDataFile);
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

    private Bucket addCODCodes(Bucket bucket) throws URISyntaxException {

        bucket = helper.giveBucketTestingCODCodes(bucket);
        return bucket;
    }

    private Bucket addHICODCodes(Bucket bucket) throws URISyntaxException {

        bucket = helper.giveBucketTestingHICODCodes(bucket, "I6191");
        return bucket;
    }

    private Bucket addMultipleCODCodes(Bucket bucket) throws URISyntaxException {

        String code = "R99";
        bucket = helper.giveBucketTestingHICODCodes(bucket, code);

        code = "I6191";
        bucket = helper.giveBucketTestingHICODCodes(bucket, code);

        code = "X59";
        bucket = helper.giveBucketTestingHICODCodes(bucket, code);

        return bucket;
    }

    private Bucket addOccCodes(Bucket bucket) throws IOException, CodeNotValidException, URISyntaxException {

        bucket = helper.giveBucketTestingOccCodes(bucket);
        return bucket;
    }

    private void writeToFile(final DataClerkingWriter dataClerkingWriter, final Bucket bucket) throws IOException {

        for (Record record : bucket) {
            dataClerkingWriter.write(record);
        }
        dataClerkingWriter.close();
    }
}

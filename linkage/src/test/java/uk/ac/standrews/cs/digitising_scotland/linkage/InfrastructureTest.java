package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.*;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Bucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.filter.ExactMatch;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by al on 25/04/2014.
 */
public class InfrastructureTest {

    private static String bucket_name1 = "BUCKET1";
    private static String bucket_name2 = "BUCKET2";
    private static String bucket_name3 = "BLOCKED-BUCKETS";
    private static String repo_path = "src/test/resources/infrastructure_test_buckets";
    private static final String BIRTH_RECORDS_PATH = "src/test/resources/1000_TEST_BIRTH_RECORDS.txt";

    private static IRepository repo;

    @Before
    public void setUpEachTest() throws RepositoryException, IOException {

        deleteRepo();

        repo = new Repository(repo_path);


        repo.makeBucket(bucket_name1);
        repo.makeBucket(bucket_name2);
        repo.makeBucket(bucket_name3);
    }

    @After
    public void tearDown() throws IOException {


        deleteRepo();
    }


    public void deleteRepo() throws IOException {

        Path rp = Paths.get(repo_path);
        System.out.println("repo path: " + rp.toAbsolutePath());
        System.out.println("exists1: " + Files.exists(rp));

        if (Files.exists(rp)) {

            FileManipulation.deleteDirectory3(repo_path);

//            File dir = new File(repo_path);
//            FileManipulation.recursivelyDeleteFolder(repo_path);
        }

        System.out.println("exists2: " + Files.exists(rp));
    }

    @Test
//    @Ignore
    public synchronized void testCreation() throws Exception {
        Bucket b = new Bucket(bucket_name1, repo_path);
    }

    @Test
//    @Ignore
    public synchronized void testLXPCreation() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(bucket_name1);
        LXP lxp = new LXP(1);
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.save(lxp);
    }

    @Test
//    @Ignore
    public synchronized void testLXPOverwrite() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(bucket_name1);
        LXP lxp = new LXP(1);
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.save(lxp);

        try {
            b.save(lxp);
        } catch( Exception e ) {
            // should get an exception due to overwrite;
            return;
        }
        fail("Overwrite of LXP record not detected");
    }

    @Test
    public synchronized void testLXPFromFile() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(bucket_name1);
        LXP lxp = new LXP(1);
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.save(lxp);       // <<--------- write record **

        LXP lxp2 = new LXP();
        lxp2.initialiseFromBucket(b, 1);    // read in the record just written at **
        assertTrue(lxp2.containsKey("age"));
        assertEquals(lxp2.get("age"), "42");
        assertTrue(lxp2.containsKey("address"));
        assertEquals(lxp2.get("address"), "home");
    }

    @Test
//    @Ignore
    public synchronized void testStreams() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(bucket_name1);
        // create a few records
        for (int i = 1; i < 10; i++) {
            LXP lxp = new LXP(i);
            lxp.put("age", "42");
            lxp.put("address", "home");
            b.save(lxp);
        }
        int count = 1;
        for( ILXP record : b.getInputStream() ) {
            assertTrue(record.containsKey("age"));
            assertEquals(record.get("age"), "42");
            assertTrue(record.containsKey("address"));
            assertEquals(record.get("address"), "home");
            count++;
        }
        assertEquals(count, 10);
    }

    @Test
//    @Ignore
    public synchronized void testReadingPopulationRecords() throws RepositoryException, RecordFormatException, JSONException, IOException {

        IBucket b = repo.getBucket(bucket_name1);
        EventImporter importer = new EventImporter();

        importer.importBirths(b, BIRTH_RECORDS_PATH);
    }

    @Test
//    @Ignore
    public synchronized void testSimpleMatchPopulationRecords() throws RepositoryException, RecordFormatException, JSONException, IOException {

        IBucket b = repo.getBucket(bucket_name1);
        IBucket b2 = repo.getBucket(bucket_name2);

        EventImporter importer = new EventImporter();
        importer.importBirths(b, BIRTH_RECORDS_PATH);

        ExactMatch filter = new ExactMatch(b.getInputStream(), new BucketBackedOutputStream(b2), "surname", "GONTHWICK");
        filter.apply();
    }
}


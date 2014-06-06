package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.*;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.filter.ExactMatch;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.BirthLabels;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

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
    private static String bucket_name4 = "INDEX";
    private static String store_path = "src/test/resources/STORE";
    private static final String BIRTH_RECORDS_PATH = "src/test/resources/1000_TEST_BIRTH_RECORDS.txt";

    private static IStore store;
    private static IRepository repo;

    @Before
    public void setUpEachTest() throws RepositoryException, IOException, StoreException {

        deleteStore();

        store = new Store(store_path);

        repo = store.makeRepository("repo");

        repo.makeBucket(bucket_name1);
        repo.makeBucket(bucket_name2);
        repo.makeBucket(bucket_name3);
        repo.makeIndexedBucket(bucket_name4);
    }

    @After
    public void tearDown() throws IOException {

      //  deleteStore();
    }


    public void deleteStore() throws IOException {

        Path sp = Paths.get(store_path);
        System.out.println("store_path: " + sp.toAbsolutePath());
        System.out.println("exists1: " + Files.exists(sp));

        if (Files.exists(sp)) {

            FileManipulation.deleteDirectory(store_path);

        }

        System.out.println("exists2: " + Files.exists(sp));
    }

    @Test
//    @Ignore
    public synchronized void testLXPCreation() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(bucket_name1);
        LXP lxp = new LXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.put(lxp);
    }

    @Test
//    @Ignore
    public synchronized void testLXPOverwrite() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(bucket_name1);
        LXP lxp = new LXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.put(lxp);

        try {
            b.put(lxp);
        } catch( Exception e ) {
            // should get an exception due to overwrite;
            return;
        }
        fail("Overwrite of LXP record not detected");
    }

    @Test
//    @Ignore
    public synchronized void testLXPFromFile() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(bucket_name1);
        LXP lxp = new LXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.put(lxp);       // <<--------- write record **
        int id = lxp.getId();

        ILXP lxp2 = b.get(id);
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
            LXP lxp = new LXP();
            lxp.put("age", "42");
            lxp.put("address", "home");
            b.put(lxp);
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



    @Test
    public synchronized void testIndex() throws Exception, RepositoryException {

        IIndexedBucket b = repo.getIndexedBucket(bucket_name4);

        b.addIndex(BirthLabels.SURNAME);
        EventImporter importer = new EventImporter();
        int counter1 = importer.importBirths(b, BIRTH_RECORDS_PATH);

        IIndex index = b.getIndex(BirthLabels.SURNAME);

        Set<String> keys = index.keySet();
        int counter2 = 0;
        for( String key : keys ) {

            // System.out.print("keys: " + key + " :");
            List<Integer> values = index.values(key);
            counter2 += values.size();
            // System.out.println( values );

        }
        assertTrue(counter1 == counter2); // records indexed are the same as those read in.



    }
}


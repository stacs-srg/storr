package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by al on 25/04/2014.
 */
public class IndexTest {

    private static String birth_bucket_name1 = "BUCKET1";
    private static String birth_bucket_name2 = "BUCKET2";
    private static String generic_bucket_name1 = "BLOCKED-BUCKETS";
    private static String indexed_bucket_name1 = "INDEXED";
    private static String types_name = "types";
    private static String store_path = "src/test/resources/STORE";
    private static final String BIRTH_RECORDS_PATH = "src/test/resources/1000_TEST_BIRTH_RECORDS.txt";
    private static final String BIRTHRECORDTYPETEMPLATE = "src/test/resources/birthType.jsn";

    private static IStore store;
    private static IRepository repo;
    private IBucket types;
    private IReferenceType birthlabel;

    @Before
    public void setUpEachTest() throws RepositoryException, IOException, StoreException {

        store = new Store(store_path);

        repo = store.makeRepository("repo");

        birthlabel = TypeFactory.getInstance().createType(Birth.class, "BIRTH");

        IBucket<Birth> b1 = repo.makeBucket(birth_bucket_name1, BucketKind.DIRECTORYBACKED);
        b1.setTypeLabelID(birthlabel.getId());

        IBucket<Birth> b2 = repo.makeBucket(birth_bucket_name2, BucketKind.DIRECTORYBACKED);
        b2.setTypeLabelID(birthlabel.getId());

        IBucket b3 = repo.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);

        IBucket<Birth> b4 = repo.makeBucket(indexed_bucket_name1, BucketKind.INDEXED);
        b4.setTypeLabelID(birthlabel.getId());
    }

    @After
    public void tearDown() throws IOException {

        deleteStore();
    }


    public void deleteStore() throws IOException {

        Path sp = Paths.get(store_path);

        if (Files.exists(sp)) {

            FileManipulation.deleteDirectory(store_path);

        }
    }


    @Test
    public synchronized void testIndex() throws Exception, RepositoryException, IllegalKeyException {

        IBucket<Birth> bb = repo.getBucket(indexed_bucket_name1, new BirthFactory(birthlabel.getId()));

        IIndexedBucket<Birth> b = (IIndexedBucket<Birth>) repo.getBucket(indexed_bucket_name1, new BirthFactory(birthlabel.getId()));

        b.addIndex(Birth.SURNAME);
        long counter1 = EventImporter.importDigitisingScotlandBirths(b, BIRTH_RECORDS_PATH, birthlabel);

        IBucketIndex index = b.getIndex(Birth.SURNAME);

        Set<String> keys = index.keySet();
        long counter2 = 0;
        for (String key : keys) {

            // System.out.print("keys: " + key + " :");
            List<Long> values = index.values(key);
            counter2 += values.size();
            // System.out.println( values );

        }
        assertEquals(counter1, counter2); // records indexed are the same as those read in.

    }
}


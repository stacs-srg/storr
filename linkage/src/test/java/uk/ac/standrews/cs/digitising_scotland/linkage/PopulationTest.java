package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.StoreException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.BirthTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.filter.ExactMatch;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by al on 25/04/2014.
 */
public class PopulationTest {

    private static String birth_bucket_name1 = "BUCKET1";
    private static String birth_bucket_name2 = "BUCKET2";
    private static String generic_bucket_name1 = "BLOCKED-BUCKETS";
    private static String indexed_bucket_name1 = "INDEX";
    private static String types_name = "types";
    private static String store_path = "src/test/resources/STORE";
    private static final String BIRTH_RECORDS_PATH = "src/test/resources/1000_TEST_BIRTH_RECORDS.txt";
    private static final String BIRTHRECORDTYPETEMPLATE = "src/test/resources/BirthRecord.jsn";

    private static IStore store;
    private static IRepository repo;
    private IBucket types;
    private ITypeLabel birthlabel;

    @Before
    public void setUpEachTest() throws RepositoryException, IOException, StoreException {

        deleteStore();

        store = new Store(store_path);

        repo = store.makeRepository("repo");

        types =  repo.makeBucket(types_name, BucketKind.DIRECTORYBACKED);
        birthlabel = TypeFactory.getInstance().createType(BIRTHRECORDTYPETEMPLATE, "BIRTH", types);

        IBucket<Birth> b1 = repo.makeBucket(birth_bucket_name1,BucketKind.DIRECTORYBACKED);
        b1.setTypeLabelID( birthlabel.getId() );
        IBucket<Birth> b2 = repo.makeBucket(birth_bucket_name2,BucketKind.DIRECTORYBACKED);
        b2.setTypeLabelID( birthlabel.getId() );
        repo.makeBucket(generic_bucket_name1,BucketKind.DIRECTORYBACKED);
        IBucket<Birth> b3 = repo.makeBucket(indexed_bucket_name1, BucketKind.INDEXED);
        b3.setTypeLabelID( birthlabel.getId() );
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
    public synchronized void testReadingPopulationRecords() throws RepositoryException, RecordFormatException, JSONException, IOException {

        IBucket<Birth> b = repo.getBucket(birth_bucket_name1, new BirthFactory(birthlabel.getId()));

        EventImporter.importDigitisingScotlandRecords(b, BIRTH_RECORDS_PATH,birthlabel);
    }

    @Test
//    @Ignore
    public synchronized void testSimpleMatchPopulationRecords() throws RepositoryException, RecordFormatException, JSONException, IOException {


        IBucket<Birth> b = repo.getBucket(birth_bucket_name1, new BirthFactory(birthlabel.getId()));
        IBucket<Birth> b2 = repo.getBucket(birth_bucket_name2,new BirthFactory(birthlabel.getId()));

        EventImporter.importDigitisingScotlandRecords(b, BIRTH_RECORDS_PATH,birthlabel);

        ExactMatch filter = new ExactMatch(b.getInputStream(), b2.getOutputStream(), "surname", "GONTHWICK");
        filter.apply();
    }



    @Test
    public synchronized void testIndex() throws Exception, RepositoryException {

        IIndexedBucket<Birth> b = (IIndexedBucket<Birth>) repo.getBucket(indexed_bucket_name1,new BirthFactory(birthlabel.getId())); // TODO delete BirthTypeLabel and rest

        b.addIndex(BirthTypeLabel.SURNAME);
        int counter1 = EventImporter.importDigitisingScotlandRecords(b, BIRTH_RECORDS_PATH,birthlabel);

        IBucketIndex index = b.getIndex(BirthTypeLabel.SURNAME);

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


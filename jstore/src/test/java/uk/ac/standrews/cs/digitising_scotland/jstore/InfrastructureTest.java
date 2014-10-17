package uk.ac.standrews.cs.digitising_scotland.jstore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.*;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
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

    private static String generic_bucket_name1 = "BLOCKED-BUCKETS";
    private static String types_name = "types";
    private static String store_path = "src/test/resources/STORE";
    private static final String PERSONRECORDTYPETEMPLATE = "src/test/resources/PersonRecord.jsn";

    private static IStore store;
    private static IRepository repo;
    private IBucket types;
    private ITypeLabel personlabel;

    @Before
    public void setUpEachTest() throws RepositoryException, IOException, StoreException {

        deleteStore();

        store = new Store(store_path);

        repo = store.makeRepository("repo");

        types =  repo.makeBucket(types_name, BucketKind.DIRECTORYBACKED);
        repo.makeBucket(generic_bucket_name1,BucketKind.DIRECTORYBACKED);

        personlabel = TypeFactory.getInstance().createType(PERSONRECORDTYPETEMPLATE, "Person", types);

    }

    @After
    public void tearDown() throws IOException {

        deleteStore();
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
    public synchronized void testLXPCreation() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        LXP lxp = new LXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.put(lxp);
    }

    @Test
    public synchronized void testLXPOverwrite() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(generic_bucket_name1);
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
    public synchronized void testLabelledLXP() throws Exception, RepositoryException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel.getId());

        try {
            LXP lxp = new LXP();
            lxp.put("age", "42");
            lxp.put("address", "home");
            b.put(lxp);
        } catch( IOException e ) {
            System.out.println("IO exception caught");
            return;
        } catch( Exception e ) {
            // should get an exception due to wrong type;
            System.out.println("Type exception caught");
            return;
        }
        fail("Type violation not detected");
    }

    @Test
    public synchronized void testLXPFromFile() throws Exception, RepositoryException, KeyNotFoundException {
        IBucket b = repo.getBucket(generic_bucket_name1);
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
    public synchronized void testStreams() throws Exception, RepositoryException, KeyNotFoundException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        // create a few records
        for (int i = 1; i < 10; i++) {
            LXP lxp = new LXP();
            lxp.put("age", "42");
            lxp.put("address", "home");
            b.put(lxp);
        }
        int count = 1;
        for( Object o : b.getInputStream() ) {
            ILXP record = (ILXP) o;  // TODO dynamic cast - eliminate?
            assertTrue(record.containsKey("age"));
            assertEquals(record.get("age"), "42");
            assertTrue(record.containsKey("address"));
            assertEquals(record.get("address"), "home");
            count++;
        }
        assertEquals(count, 10);
    }







}


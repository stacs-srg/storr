package uk.ac.standrews.cs.digitising_scotland.jstore;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.interfaces.ITransaction;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.Types;
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

    private static String generic_bucket_name1 = "BUCKET1";
    private static String generic_bucket_name2 = "BUCKET2";
    private static String generic_bucket_name3 = "BUCKET3";

    private static String types_name = "types";
    private static String store_path = "src/test/resources/STORE";
    private static final String PERSONRECORDTYPETEMPLATE = "src/test/resources/PersonRecord.jsn";
    private static final String PERSONREFTUPLETYPETEMPLATE = "src/test/resources/PersonRefRecord.jsn";

    private static IStore store;
    private static IRepository repo;
    private IBucket types;
    private IReferenceType personlabel;
    private IReferenceType personlabel2;
    private IReferenceType personreftuple;

    @Before
    public void setUpEachTest() throws RepositoryException, IOException, StoreException {

        deleteStore();

        store = new Store(store_path);

        repo = store.makeRepository("repo");

        repo.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);
        repo.makeBucket(generic_bucket_name2, BucketKind.DIRECTORYBACKED);
        repo.makeBucket(generic_bucket_name3, BucketKind.DIRECTORYBACKED);

        personlabel = TypeFactory.getInstance().createType(PERSONRECORDTYPETEMPLATE, "Person");
        personlabel2 = TypeFactory.getInstance().createType(PERSONRECORDTYPETEMPLATE, "Person");

        personreftuple = TypeFactory.getInstance().createType(PERSONREFTUPLETYPETEMPLATE, "PersonRefTuple");

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
    public synchronized void testLXPCreation() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        LXP lxp = new LXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.makePersistent(lxp);
    }

    @Test
    public synchronized void testLXPOverwrite() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        LXP lxp = new LXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.makePersistent(lxp);

        try {
            b.makePersistent(lxp);
        } catch (Exception e) {
            // should get an exception due to overwrite;
            return;
        }
        fail("Overwrite of LXP record not detected");
    }

    @Test
    public synchronized void testLabelledLXP1() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel.getId());

        try {
            LXP lxp = new LXP();
            lxp.put("age", "42");
            lxp.put("address", "home");
            b.makePersistent(lxp);
        } catch (BucketException e) {
            System.out.println("Bucket exception caught");
            return;
        } catch (Exception e) {
            // should get an exception due to wrong type;
            System.out.println("Type exception caught");
            return;
        }
        fail("Type violation not detected");
    }

    @Test
    public synchronized void testSimpleTransaction() throws RepositoryException, IllegalKeyException, BucketException, StoreException {

        IBucket b = repo.getBucket(generic_bucket_name1);

        LXP lxp = new LXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.makePersistent(lxp);
        long oid = lxp.getId();

        ITransaction txn = store.getTransactionManager().beginTransaction();

        try {

            ILXP lxp2 = b.getObjectById(oid);
            lxp2.put("age", "43");

            b.update(lxp2);

            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }


    @Test
    public synchronized void testLabelledLXP2() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel.getId());

        LXP lxp = new LXP();        // correct structure but no label - that is OK!
        lxp.put("name", "al");
        lxp.put("age", 55);
        b.makePersistent(lxp);
    }

    @Test
    public synchronized void testLabelledLXP3() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel.getId());

        LXP lxp = new LXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.put(Types.LABEL, personlabel.getId());    // with correct label
        b.makePersistent(lxp); // Should succeed: labels correct and type label identical to bucket
    }

    @Test
    public synchronized void testLabelledLXP4() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel2.getId());

        LXP lxp = new LXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.put(Types.LABEL, personlabel.getId()); // structurally equivalent label
        b.makePersistent(lxp); // Should succeed labels correct and type label structurally equivalent
    }

    @Test(expected = Exception.class)
    public synchronized void testLabelledLXP5() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel2.getId());

        LXP lxp = new LXP();        // incorrect structure
        lxp.put("name", "al");
        lxp.put("address", "home");
        lxp.addTypeLabel(personlabel); // correct label but not structurally equivalent label
        b.makePersistent(lxp);
        // should getString an exception due to incorrect structure

    }

    @Test
    public synchronized void testReferenceLabel() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b1 = repo.getBucket(generic_bucket_name1);
        b1.setTypeLabelID(personlabel.getId());
        IBucket b2 = repo.getBucket(generic_bucket_name2);
        b2.setTypeLabelID(personreftuple.getId());


        LXP lxp = new LXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.put(Types.LABEL, personlabel.getId()); // correct label
        long person_id = lxp.getId();

        b1.makePersistent(lxp);


        LXP lxp2 = new LXP();        // correct structure
        lxp2.put("person_ref", person_id);
        b2.makePersistent(lxp2);
    }

    @Test
    public synchronized void testIllegalReferenceLabel() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b1 = repo.getBucket(generic_bucket_name1);
        b1.setTypeLabelID(personlabel.getId());
        IBucket b2 = repo.getBucket(generic_bucket_name2);
        b2.setTypeLabelID(personreftuple.getId());


        LXP lxp = new LXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.put(Types.LABEL, personlabel.getId()); // correct label
        long person_id = lxp.getId();

        b1.makePersistent(lxp);


        try {
            LXP lxp2 = new LXP();        // correct structure
            lxp2.put("person_ref", Integer.toString(100)); // an illegal reference - not a legal identifier

            b2.makePersistent(lxp2);
        } catch (BucketException e) { // should catch this - reference is not in the store yet!
            // do nothing test succeeds
            return;
        }
        fail("Illegal reference not detected");
    }

    @Test
    public synchronized void testIllegalReferenceLabel2() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b1 = repo.getBucket(generic_bucket_name1);
        b1.setTypeLabelID(personlabel.getId());
        IBucket b2 = repo.getBucket(generic_bucket_name2);
        b2.setTypeLabelID(personreftuple.getId());
        IBucket b3 = repo.getBucket(generic_bucket_name3);

        LXP lxp = new LXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.put(Types.LABEL, personlabel.getId()); // correct label
        long person_id = lxp.getId();

        b1.makePersistent(lxp);


        LXP lxp2 = new LXP();
        b3.makePersistent(lxp2); // just stick something in bucket3
        long lxp2_id = lxp2.getId();

        try {
            LXP lxp3 = new LXP();        // correct structure
            lxp3.put("person_ref", Long.toString(lxp2_id)); // an illegal reference to this tuple - wrong reference type

            b2.makePersistent(lxp3);
        } catch (BucketException e) { // should catch this - illegal reference
            // do nothing test succeeds if exception is caught
            return;
        }
        fail("Illegal reference not detected");

    }

    @Test
    public synchronized void testLegalTypedRecordInUntypedBucket() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b1 = repo.getBucket(generic_bucket_name1);

        LXP lxp = new LXP();        // incorrect structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.put(Types.LABEL, personlabel.getId()); // correct label

        b1.makePersistent(lxp);
    }

    @Test
    public synchronized void testIllegalTypedRecordInUntypedBucket() throws Exception, RepositoryException, IllegalKeyException {
        IBucket b1 = repo.getBucket(generic_bucket_name1);

        try {
            LXP lxp = new LXP();        // incorrect structure
            lxp.put("name", "al");
            lxp.put("wrongfield", "55");
            lxp.put(Types.LABEL, personlabel.getId()); // correct label

            b1.makePersistent(lxp);

        } catch (BucketException e) { // should catch this - structure is wrong
            // do nothing test succeeds if exception is caught
            return;
        }
        fail("Illegal field not detected");
    }

    @Test
    public synchronized void testLXPFromFile() throws Exception, RepositoryException, KeyNotFoundException, TypeMismatchFoundException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        LXP lxp = new LXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.makePersistent(lxp);       // <<--------- write record **
        long id = lxp.getId();

        ILXP lxp2 = b.getObjectById(id);
        assertTrue(lxp2.containsKey("age"));
        assertEquals(lxp2.getString("age"), "42");
        assertTrue(lxp2.containsKey("address"));
        assertEquals(lxp2.getString("address"), "home");
    }


    @Test
    public synchronized void testStreams() throws Exception, RepositoryException, KeyNotFoundException, TypeMismatchFoundException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        // create a few records
        for (int i = 1; i < 10; i++) {
            LXP lxp = new LXP();
            lxp.put("age", "42");
            lxp.put("address", "home");
            b.makePersistent(lxp);
        }
        int count = 1;
        for (Object o : b.getInputStream()) {
            ILXP record = (ILXP) o;
            assertTrue(record.containsKey("age"));
            assertEquals(record.getString("age"), "42");
            assertTrue(record.containsKey("address"));
            assertEquals(record.getString("address"), "home");
            count++;
        }
        assertEquals(count, 10);
    }


    @Test
    public synchronized void testBaseTypedFields() throws Exception, RepositoryException, KeyNotFoundException, TypeMismatchFoundException, IllegalKeyException {
        IBucket b = repo.getBucket(generic_bucket_name1);
        LXP lxp = new LXP();

        // base types supported are: BOOLEAN, DOUBLE, INTEGER, LONG & STRING
        lxp.put("boolean", true);
        lxp.put("double", 3.14d);
        lxp.put("int", 7);
        lxp.put("long", 23L);
        lxp.put("string", "al");
        b.makePersistent(lxp);       // <<--------- write record **
        long id = lxp.getId();

        ILXP lxp2 = b.getObjectById(id);
        assertTrue(lxp2.containsKey("boolean"));
        assertEquals(lxp2.getBoolean("boolean"), true);
        assertTrue(lxp2.containsKey("double"));
        assertEquals(3.14d, lxp2.getDouble("double"), 0.0d);
        assertTrue(lxp2.containsKey("int"));
        assertEquals(lxp2.getInt("int"), 7);
        assertTrue(lxp2.containsKey("long"));
        assertEquals(lxp2.getLong("long"), 23L);
        assertTrue(lxp2.containsKey("string"));
        assertEquals(lxp2.getString("string"), "al");

    }

    @Test
    @Ignore
    public synchronized void illegalFieldType() {

        // TODO need a test for illegal field label types -
        // e.g. reference to a type that doesn't exist
        // code needs fixed in injest if we keep it.
    }

    @Test(expected = IllegalKeyException.class)
    public synchronized void emptyStringInlabelType() throws RepositoryException, BucketException, IllegalKeyException {

        IBucket b = repo.getBucket(generic_bucket_name1);
        LXP lxp = new LXP();
        lxp.put("", "fish");
        b.makePersistent(lxp);       // <<--------- write record **
    }


}


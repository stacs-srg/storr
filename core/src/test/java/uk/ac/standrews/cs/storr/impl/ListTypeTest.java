package uk.ac.standrews.cs.storr.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.storr.util.FileManipulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * @author Al al@st-andrews.ac.uk 24-11-16
 */
public class ListTypeTest {

    private static final String REPO_NAME = "repo";

    private static String lxpBucketName = "lxpBucket";
    private static String classWithListOfScalarsBucketName = "classWithListOFScalarsBucket";
    private static String classWithListOfRefsBucketName = "classWithListOFRefsBucket";

    private IStore store;
    private IRepository repo;

    private IBucket<LXP> lxp_bucket;
    private IBucket<ClassWithListOfScalars> scalar_list_bucket;
    private IBucket<ClassWithListOfRefs> ref_list_bucket;

    ClassWithListOfScalarsFactory classWithListOfScalarsFactory;
    ClassWithListOfRefsFactory classWithListOfRefsFactory;


    private Path tempStore;
    private String store_path = "";


    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {
        tempStore = Files.createTempDirectory(null);

        StoreFactory.setStorePath( tempStore );
        store = StoreFactory.makeStore();
        store_path = tempStore.toString();
        System.out.println("STORE PATH = " + store_path + " has been created");


        TypeFactory tf = TypeFactory.getInstance();
        IReferenceType classwithlistofscalars_type = tf.createType(ClassWithListOfScalars.class, "classwithlistofscalars");
        IReferenceType classwithlistofrefs_type = tf.createType(ClassWithListOfRefs.class, "classwithlistofrefs");

        repo = store.makeRepository(REPO_NAME);
        classWithListOfScalarsFactory = new ClassWithListOfScalarsFactory(classwithlistofscalars_type.getId());
        classWithListOfRefsFactory = new ClassWithListOfRefsFactory(classwithlistofrefs_type.getId());

        lxp_bucket = repo.makeBucket(lxpBucketName, BucketKind.DIRECTORYBACKED );
        scalar_list_bucket = repo.makeBucket(classWithListOfScalarsBucketName, BucketKind.DIRECTORYBACKED, classWithListOfScalarsFactory );
        ref_list_bucket = repo.makeBucket(classWithListOfRefsBucketName, BucketKind.DIRECTORYBACKED, classWithListOfRefsFactory );

    }

    @After
    public void tearDown() throws IOException {
//        FileManipulation.deleteDirectory(tempStore);
        System.out.println("STORE PATH = " + tempStore.toString() + " has been deleted");
    }

    @Test
    public void checkStructuralEquivalenceWithListOfScalars() throws RepositoryException, BucketException, PersistentObjectException, IOException {

        List<Integer> list = new ArrayList(); list.add(99); list.add(88);

        ClassWithListOfScalars example = new ClassWithListOfScalars( 53,list );
        scalar_list_bucket.makePersistent(example);
        long id = example.getId();

        // Now try and read back - avoid all cache etc.

        Path file_path = tempStore.resolve( "REPOS" ).resolve( REPO_NAME ).resolve(classWithListOfScalarsBucketName).resolve( new Long( id ).toString() );
        BufferedReader reader = Files.newBufferedReader( file_path, FileManipulation.FILE_CHARSET );

        LXP lxp2 = new LXP(id, new JSONReader(reader),repo,lxp_bucket );

        assertEquals( lxp2.getId(), id );
        assertEquals( lxp2.getInt( "S_INT" ), 53 );
        List l = lxp2.getList( "S_LIST" );
        assertEquals( (int) l.get(0), 99 );
        assertEquals( (int) l.get(1), 88 );


    }

    private void assertEquals(long id, long id1) {
    }

    @Test
    public void checkStructuralEquivalenceWithListOfRefs() throws RepositoryException, BucketException, PersistentObjectException, IOException {

        LXP lxp1 = new LXP(); lxp1.put( "99", 99 ); lxp_bucket.makePersistent(lxp1);
        long lxp1_id = lxp1.getId();
        LXP lxp2 = new LXP(); lxp2.put( "88", 88 ); lxp_bucket.makePersistent(lxp2);
        long lxp2_id = lxp2.getId();

        List<LXP> list = new ArrayList(); list.add( lxp1 ); list.add( lxp2 );

        // ClassWithListOfRefs example = new ClassWithListOfRefs( 53,list );
        ClassWithListOfRefs example = classWithListOfRefsFactory.create( 53, list );
        ref_list_bucket.makePersistent(example);
        long id = example.getId();

        // Now try and read back - avoid all cache etc.

        Path file_path = tempStore.resolve( "REPOS" ).resolve( REPO_NAME ).resolve(classWithListOfRefsBucketName).resolve( new Long( id ).toString() );
        BufferedReader reader = Files.newBufferedReader( file_path, FileManipulation.FILE_CHARSET );

        LXP lxp3 = new LXP(id, new JSONReader(reader),repo,lxp_bucket  );
        assertEquals( lxp3.getId(), id );
        assertEquals( lxp3.getInt( "R_INT" ), 53 );
        List l = lxp3.getList( "R_LIST" );
        assertTrue( l.get(0).equals( "{\"99\":99}" ) );
        assertTrue( l.get(1).equals( "{\"88\":88}" ) );

    }


}

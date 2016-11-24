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

import static org.junit.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ListTypeTest {

    private static final String REPO_NAME = "repo";
    private static String classWithListBucketName = "classWithListBucket";

    private IStore store;
    private IRepository repo;
    private IBucket<ClassWithList> bucket;

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
        IReferenceType fieldLabelsType = tf.createType(ClassWithList.class, "fieldLabels");

        repo = store.makeRepository(REPO_NAME);
        FieldLabelsExemplarFactory fieldLabelsExemplarFactory = new FieldLabelsExemplarFactory(fieldLabelsType.getId());
        bucket = repo.makeBucket(classWithListBucketName, BucketKind.DIRECTORYBACKED, fieldLabelsExemplarFactory );
    }

    @After
    public void tearDown() throws IOException {
//        FileManipulation.deleteDirectory(tempStore);
        System.out.println("STORE PATH = " + tempStore.toString() + " has been deleted");
    }

    @Test
    public void checkStructuralEquivalence() throws RepositoryException, BucketException, PersistentObjectException, IOException {

        List<Integer> list = new ArrayList(); list.add(99); list.add(88);

        ClassWithList example = new ClassWithList( 53,list );
        bucket.makePersistent(example);
        long id = example.getId();

        // Now try and read back - avoid all cache etc.

        Path file_path = tempStore.resolve( "REPOS" ).resolve( REPO_NAME ).resolve( classWithListBucketName ).resolve( new Long( id ).toString() );
        BufferedReader reader = Files.newBufferedReader( file_path, FileManipulation.FILE_CHARSET );

        LXP lxp2 = new LXP(id, new JSONReader(reader) );
        assertEquals( lxp2.getId(), id );
        assertEquals( lxp2.getInt( "ORIGINAL_ID" ), 53 );
        List l = lxp2.getList( "MY_LIST" );
        assertEquals( l.get(0), 99 );
        assertEquals( l.get(1), 88 );


    }

}

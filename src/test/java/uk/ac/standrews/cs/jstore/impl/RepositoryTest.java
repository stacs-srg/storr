package uk.ac.standrews.cs.jstore.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.interfaces.BucketKind;
import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.jstore.interfaces.IStore;
import uk.ac.standrews.cs.jstore.util.FileManipulation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RepositoryTest {

    private static final String REPO_NAME = "repo";
    private static String generic_bucket_name1 = "BUCKET1";

    private IStore store;
    private IRepository repo;

    private Path tempStore;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {
        tempStore = Files.createTempDirectory(null);

        StoreFactory.setStorePath( tempStore );
        store = StoreFactory.makeStore();
        System.out.println("STORE PATH = " + tempStore.toString() + " has been created");

        repo = store.makeRepository(REPO_NAME);

    }

    @After
    public void tearDown() throws IOException {
        FileManipulation.deleteDirectory(tempStore);
        System.out.println("STORE PATH = " + tempStore.toString() + " has been deleted");
    }

    @Test
    public void createBucketTest() throws RepositoryException {
        IBucket bucket = repo.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);

        assertTrue(repo.bucketExists(generic_bucket_name1));
        assertEquals(bucket.getName(), repo.getBucket(generic_bucket_name1).getName());
    }

    @Test
    public void deleteBucketTest() throws RepositoryException {
        repo.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);

        assertTrue(repo.bucketExists(generic_bucket_name1));
        repo.deleteBucket(generic_bucket_name1);

        assertFalse(repo.bucketExists(generic_bucket_name1));
    }
}
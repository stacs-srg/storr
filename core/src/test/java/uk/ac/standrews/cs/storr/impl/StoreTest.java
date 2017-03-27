package uk.ac.standrews.cs.storr.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.BucketKind;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.storr.util.FileManipulation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StoreTest {

    private static final String REPO_NAME = "repo";
    private static String generic_bucket_name1 = "BUCKET1";

    private IStore store;
    private IRepository repo;

    private Path tempStore;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {
        tempStore = Files.createTempDirectory(null);

        StoreFactory.setStorePath( tempStore );
        store = StoreFactory.initialiseNewStore();
        System.out.println("STORE PATH = " + tempStore.toString() + " has been created");

        repo = store.makeRepository(REPO_NAME);
        repo.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);
    }

    @After
    public void tearDown() throws IOException {
        FileManipulation.deleteDirectory(tempStore);
        System.out.println("STORE PATH = " + tempStore.toString() + " has been deleted");
    }

    @Test
    public void deleteRepositoryTest() throws RepositoryException {
        store.deleteRepo(REPO_NAME);
        assertFalse(store.repoExists(REPO_NAME));
    }

    @Test
    public void checkRepositoryHasBeenCreatedTest() throws RepositoryException {
        assertTrue(store.repoExists(REPO_NAME));
        assertEquals(repo.getRepoPath(), store.getRepo(REPO_NAME).getRepoPath());
    }

}

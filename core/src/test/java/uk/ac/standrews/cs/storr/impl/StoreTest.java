package uk.ac.standrews.cs.storr.impl;

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.BucketKind;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StoreTest extends CommonTest {

    private static final String REPO_NAME = "repo";
    private static String generic_bucket_name1 = "BUCKET1";

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        super.setUp();
        repo.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);
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

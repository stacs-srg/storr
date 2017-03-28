package uk.ac.standrews.cs.storr.impl;

import org.junit.After;
import org.junit.Before;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.storr.util.FileManipulation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommonTest {

    protected static final String REPO_NAME = "repo";

    private static boolean DEBUG = false;

    protected IStore store;
    protected IRepository repo;

    protected Path tempStore;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        tempStore = Files.createTempDirectory(null);

        StoreFactory.setStorePath(tempStore);
        store = StoreFactory.initialiseNewStore();

        if (DEBUG) {
            System.out.println("STORE PATH = " + tempStore + " has been created");
        }

        repo = store.makeRepository(REPO_NAME);
    }

    @After
    public void tearDown() throws IOException {

        FileManipulation.deleteDirectory(tempStore);

        if (DEBUG) {
            System.out.println("STORE PATH = " + tempStore + " has been deleted");
        }
    }
}

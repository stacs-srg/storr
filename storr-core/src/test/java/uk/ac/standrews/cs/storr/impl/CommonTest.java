package uk.ac.standrews.cs.storr.impl;

import org.junit.After;
import org.junit.Before;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.utilities.FileManipulation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CommonTest {

    static final String REPOSITORY_NAME = "repo";

    private static boolean DEBUG = false;

    protected IStore store;
    protected IRepository repository;

    Path store_path;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        store_path = Files.createTempDirectory(null);
        store = new Store(store_path);

        if (DEBUG) {
            System.out.println("STORE PATH = " + store_path + " has been created");
        }

        repository = store.makeRepository(REPOSITORY_NAME);
    }

    @After
    public void tearDown() throws IOException {

        FileManipulation.deleteDirectory(store_path);

        if (DEBUG) {
            System.out.println("STORE PATH = " + store_path + " has been deleted");
        }
    }
}

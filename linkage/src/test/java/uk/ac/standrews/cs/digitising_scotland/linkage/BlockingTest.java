package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.StoreException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IStore;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.BlockingPFPLMFFFoverBDMrecords;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by al on 02/05/2014.
 */
public class BlockingTest {

    private static final String bucket_name = "BLOCKED-BUCKETS";
    private static final String BIRTH_RECORDS_PATH = "src/test/resources/1000_TEST_BIRTH_RECORDS.txt";
    private static final String repo_path = "test_buckets";
    private static final String source_base_path = "src/test/resources/BDMSet1";
    private static final String births_name = "birth_records";
    private static final String deaths_name = "death_records";
    private static final String marriages_name = "marriage_records";
    private static final String births_source_path = source_base_path + "/" + births_name + ".txt";
    private static final String deaths_source_path = source_base_path + "/" + deaths_name + ".txt";
    private static final String marriages_source_path = source_base_path + "/" + marriages_name + ".txt";

    private static String store_path = "src/test/resources/STORE";

    private static IStore store;
    private static IBucket births;
    private static IBucket deaths;
    private static IBucket marriages;

    private IRepository repo;


    @Before
    public void setUpEachTest() throws RepositoryException, StoreException, IOException {

        store = new Store(store_path);

        repo = store.makeRepository("repo_name");

        births = repo.makeBucket(births_name);
        deaths = repo.makeBucket(deaths_name);
        marriages = repo.makeBucket(marriages_name);
    }

    @After
    public void afterEachTest() throws IOException {

        if (Files.exists(Paths.get(repo_path))) {
            FileManipulation.deleteDirectory(repo_path);
        }
    }

    @Test
    public synchronized void testPFPLMFFF() throws Exception, RepositoryException {

        EventImporter importer = new EventImporter();

        importer.importBirths(births, births_source_path);
        importer.importDeaths(deaths, deaths_source_path);
        importer.importMarriages(marriages, marriages_source_path);

        BlockingPFPLMFFFoverBDMrecords blocker = new BlockingPFPLMFFFoverBDMrecords( births, deaths, repo);

        blocker.apply();
    }
}

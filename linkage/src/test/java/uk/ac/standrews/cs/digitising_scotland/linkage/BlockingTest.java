package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.StoreException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.FNLFFMFOverBirths;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by al on 02/05/2014.
 */
public class BlockingTest {

    private static final String repo_path = "test_buckets";
    private static final String source_base_path = "src/test/resources/BDMSet1";
    private static final String births_name = "birth_records";
    private static final String deaths_name = "death_records";
    private static final String marriages_name = "marriage_records";
    private static final String types_name = "types";
    private static final String births_source_path = source_base_path + "/" + births_name + ".txt";
    private static final String deaths_source_path = source_base_path + "/" + deaths_name + ".txt";
    private static final String marriages_source_path = source_base_path + "/" + marriages_name + ".txt";

    private static String store_path = "src/test/resources/STORE";

    private static IStore store;
    private static IBucket<Birth> births;
    private static IBucket types;

    private IRepository repo;
    private IReferenceType birthlabel;
    private IReferenceType deathlabel;
    private IReferenceType marriagelabel;


    @Before
    public void setUpEachTest() throws RepositoryException, StoreException, IOException {

        store = new Store(store_path);
        repo = store.makeRepository(repo_path);
        TypeFactory tf = TypeFactory.getInstance();
        birthlabel = tf.createType(Birth.class, "Birth");
        births = repo.makeBucket(births_name, BucketKind.DIRECTORYBACKED, new BirthFactory(birthlabel.getId()));
    }


    @After
    public void afterEachTest() throws IOException {

        if (Files.exists(Paths.get(store_path))) {
            FileManipulation.deleteDirectory(store_path);
        }
    }

    @Test
    public synchronized void testPFPLMFFF() throws Exception, RepositoryException {

        EventImporter.importDigitisingScotlandRecords(births, births_source_path, birthlabel);
        FNLFFMFOverBirths blocker = new FNLFFMFOverBirths(births, repo);

        blocker.apply();
    }
}


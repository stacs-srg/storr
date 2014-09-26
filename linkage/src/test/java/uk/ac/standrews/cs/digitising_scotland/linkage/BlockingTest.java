package uk.ac.standrews.cs.digitising_scotland.linkage;

import factory.TypeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.StoreException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.FNLFFMFOverBirths;
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

    private static final String BIRTHRECORDTYPETEMPLATE = "src/test/resources/BirthRecord.jsn";
    private static final String DEATHRECORDTYPETEMPLATE = "src/test/resources/DeathRecord.jsn";
    private static final String MARRIAGERECORDTYPETEMPLATE = "src/test/resources/MarriageRecord.jsn";

    private static IStore store;
    private static IBucketTypedOLD<ILXP> births;
    private static IBucketTypedOLD<ILXP> deaths;
    private static IBucketTypedOLD<ILXP> marriages;
    private static IBucketTypedOLD<ILXP> types;

    private IRepository repo;
    private ITypeLabel birthlabel;
    private ITypeLabel deathlabel;
    private ITypeLabel marriagelabel;


    @Before
    public void setUpEachTest() throws RepositoryException, StoreException, IOException {

        store = new Store(store_path);

        repo = store.makeRepository(repo_path);

        births = repo.makeBucket(births_name, LXP.getInstance());
        deaths = repo.makeBucket(deaths_name, LXP.getInstance());
        marriages = repo.makeBucket(marriages_name, LXP.getInstance());
        types =  repo.makeBucket(types_name, LXP.getInstance());
        initialiseTypes( types );
    }

    private void initialiseTypes( IBucketTypedOLD types_bucket ) {

        TypeFactory tf = TypeFactory.getInstance();
        birthlabel = tf.createType(BIRTHRECORDTYPETEMPLATE, "BIRTH", types_bucket);
        deathlabel = tf.createType(DEATHRECORDTYPETEMPLATE, "DEATH", types_bucket);
        marriagelabel = tf.createType(MARRIAGERECORDTYPETEMPLATE, "MARRIAGE", types_bucket);
    }

    @After
    public void afterEachTest() throws IOException {

        if (Files.exists(Paths.get(store_path))) {
            FileManipulation.deleteDirectory(store_path);
        }
    }

    @Test
    public synchronized void testPFPLMFFF() throws Exception, RepositoryException {


        EventImporter.importDigitisingScotlandRecords(births, births_source_path,birthlabel);
        EventImporter.importDigitisingScotlandRecords(deaths, deaths_source_path, deathlabel);
        EventImporter.importDigitisingScotlandRecords(marriages, marriages_source_path, marriagelabel);

        FNLFFMFOverBirths blocker = new FNLFFMFOverBirths( births, deaths, repo);

        blocker.apply();
    }
}

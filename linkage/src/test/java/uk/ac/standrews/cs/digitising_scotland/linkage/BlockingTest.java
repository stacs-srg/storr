package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.junit.After;
import org.junit.Before;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.StoreException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.DeathFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.MarriageFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
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
    private static IBucket<Birth> births;
    private static IBucket<Death> deaths;
    private static IBucket<Marriage> marriages;
    private static IBucketLXP types;

    private IRepository repo;
    private ITypeLabel birthlabel;
    private ITypeLabel deathlabel;
    private ITypeLabel marriagelabel;


    @Before
    public void setUpEachTest() throws RepositoryException, StoreException, IOException {

        store = new Store(store_path);

        repo = store.makeRepository(repo_path);

        births = repo.makeBucket(births_name, BucketKind.DIRECTORYBACKED, new BirthFactory(birthlabel.getId()));
        deaths = repo.makeBucket(deaths_name, BucketKind.DIRECTORYBACKED, new DeathFactory(deathlabel.getId()));
        marriages = repo.makeBucket(marriages_name, BucketKind.DIRECTORYBACKED, new MarriageFactory(marriagelabel.getId()));
        types =  repo.makeLXPBucket(types_name, BucketKind.DIRECTORYBACKED);
        initialiseTypes( types );
    }

    private void initialiseTypes( IBucketLXP types_bucket ) {

        TypeFactory tf = TypeFactory.getInstance();
        birthlabel = tf.createType(BIRTHRECORDTYPETEMPLATE, "BIRTH", types);
        deathlabel = tf.createType(DEATHRECORDTYPETEMPLATE, "DEATH", types);
        marriagelabel = tf.createType(MARRIAGERECORDTYPETEMPLATE, "MARRIAGE", types);
    }

    @After
    public void afterEachTest() throws IOException {

        if (Files.exists(Paths.get(store_path))) {
            FileManipulation.deleteDirectory(store_path);
        }
    }
}

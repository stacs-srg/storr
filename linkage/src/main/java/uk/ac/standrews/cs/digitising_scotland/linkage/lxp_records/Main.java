package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.StoreException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.Types;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.PersonFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.interfaces.IPair;

import java.io.IOException;

/**
 * Created by al on 21/11/14.
 */
public class Main {

    //    // Repositories and stores

    private static String store_path = "src/test/resources/STORE";
    private static String repo_name = "repo";

    private static final String PERSONRECORDTYPETEMPLATE = "src/test/resources/personType.jsn";   // TODO write this spec.

    private Store store;
    private IRepository repo;
    // Bucket declarations

    private IBucket types;
    private IIndexedBucket<IPair<Person>> lineage;             // Bucket containing pairs of potentially linked parents and child_ids


    private static String types_name = "types";


    private IReferenceType personLabel;
    private TypeFactory tf;

    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {
        store = new Store(store_path);

        repo = store.makeRepository(repo_name);

        tf = TypeFactory.getInstance();
        initialiseTypes(types);

        initialiseFactories();

    }

    private void initialiseTypes(IBucket types) {

        personLabel = tf.createType(PERSONRECORDTYPETEMPLATE, "Person");

    }

    private void initialiseFactories() {
        PersonFactory personFactory = new PersonFactory(personLabel.getId());
    }


    public static void main(String[] args) throws StoreException, IOException, RecordFormatException, RepositoryException, JSONException {

        new Main().initialise();

        LXP l = Types.getTypeRep(Person.class);
    }
}

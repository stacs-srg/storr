package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.linkage.factory.TypeFactory;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class Person extends AbstractLXP {

    private static final int this_label = TypeFactory.getInstance().typeWithname( "Person" ).getId();


    public Person() {
        super();
        required_type_labelID = this_label;
    }

    public Person(JSONReader reader) throws PersistentObjectException {

        super(reader );
        required_type_labelID = this_label;
    }

}

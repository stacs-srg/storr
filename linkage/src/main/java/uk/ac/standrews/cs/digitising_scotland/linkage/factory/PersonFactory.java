package uk.ac.standrews.cs.digitising_scotland.linkage.factory;


import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Person;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class PersonFactory extends TFactory<Person> implements ILXPFactory<Person> {


    public PersonFactory(int personlabelID) {
        this.required_type_labelID = personlabelID;
    }


    @Override
    public Person create(int label_id, JSONReader reader) throws PersistentObjectException {
        return new Person( reader );
    }

}

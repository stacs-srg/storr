package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IInputStream;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IOutputStream;
import uk.ac.standrews.cs.digitising_scotland.linkage.interfaces.IPair;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Person;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.SameAs;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.AbstractPairwiseLinker;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

/**
 * Created by al on 19/06/2014.
 * <p/>
 * Links ILXP records with labels drawn from @link uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels
 * Attempts to find birth records with the same person in different roles (e.g. mother-baby and father-baby).
 */
public class BirthBirthLinker extends AbstractPairwiseLinker<Person> {

    public BirthBirthLinker(final IInputStream<Person> input, final IOutputStream<IPair<Person>> output) {

        super(input, output);
    }

    @Override
    public boolean compare(final IPair<Person> pair) {

        ILXP first = pair.first();
        ILXP second = pair.second();

        // Return true if we have person in different roles

        try {
            if ((first.get(Person.ROLE).equals("baby") && second.get(Person.ROLE).equals("mother")) ||
                    (first.get(Person.ROLE).equals("mother") && second.get(Person.ROLE).equals("baby")) ||
                    (first.get(Person.ROLE).equals("baby") && second.get(Person.ROLE).equals("father")) ||
                    (first.get(Person.ROLE).equals("father") && second.get(Person.ROLE).equals("baby"))) {
                return true;
            }
        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
        }
        return false;
    }

    @Override
    public void addToResults(final IPair pair, final IOutputStream results) { // TODO these are not typed properly - look at USES OF PAIR

        Person first = (Person) pair.first();  // TODO check dynamic casting
        Person second = (Person) pair.second();

        // get the people in the right order parent first

        ILXP result_record = new SameAs(first, second, "???", 1.0f);

        results.add(result_record);


    }
}

package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Person;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.IOException;

/**
 * This class blocks based on persons' first name, last name over person records
 * Created by al on 03/06/2014. x
 */
public class FNLNSOverPerson<T extends ILXP> extends Blocker<T> {

    public FNLNSOverPerson(final IBucket<T> people, final IRepository output_repo, ILXPFactory<T> tFactory) throws RepositoryException, IOException {

        super(people.getInputStream(), output_repo, tFactory);
    }

    @Override
    public String[] determineBlockedBucketNamesForRecord(final ILXP record) {

        try {
            if (record.containsKey("TYPE") && record.get("TYPE").equals(TypeFactory.getInstance().typeWithname("person"))) {

                // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

                String forename = record.get(Person.FORENAME);
                String surname = record.get(Person.SURNAME);
                String mmsurname = record.get(Person.MOTHERS_MAIDEN_SURNAME);
                String sex = record.get(Person.SEX);

                StringBuilder builder = new StringBuilder();

                builder.append(forename);

                if (sex.equals("F") && mmsurname != null && !mmsurname.equals("")) {
                    builder.append(mmsurname);
                } else {
                    builder.append(surname);
                }
                builder.append(sex);

                return new String[]{removeNasties(builder.toString())};

            } else {
                ErrorHandling.error("Record with unknown type in input Stream - ignoring");
                return new String[]{};
            }
        } catch (KeyNotFoundException e) {
            ErrorHandling.error("Record found with unknown key");
            return new String[]{};
        }
    }

    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(final String key) {
        return key.replace("/", "");
    }
}


package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonTypeLabel;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * This class blocks based on persons' first name, last name over person records
 * Created by al on 03/06/2014. x
 */
public class FNLNSOverPerson<T extends ILXP> extends Blocker<T> {

    public FNLNSOverPerson(final IBucket<T> people, final IRepository output_repo, ILXPFactory<T> tFactory) throws RepositoryException {

        super(people.getInputStreamT(), output_repo, tFactory);
    }

    @Override
    public String[] determineBlockedBucketNamesForRecord(final ILXP record) {

        if (record.containsKey("TYPE") && record.get("TYPE").equals(PersonTypeLabel.TYPE)) {

            // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

            String forename = record.get(PersonTypeLabel.FORENAME);
            String surname = record.get(PersonTypeLabel.SURNAME);
            String mmsurname = record.get(PersonTypeLabel.MOTHERS_MAIDEN_SURNAME);
            String sex = record.get(PersonTypeLabel.SEX);

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
    }

    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(final String key) {
        return key.replace("/", "");
    }
}


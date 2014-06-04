package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operations.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * This class blocks based on persons' first name, last name over person records
 * Created by al on 03/06/2014. x
 */
public class BlockingFirstLastSexOverPerson extends Blocker {

    public BlockingFirstLastSexOverPerson(final IBucket people, final IRepository output_repo) throws RepositoryException {

        super( people.getInputStream(), output_repo);
    }

    @Override
    public String[] determineBlockedBucketNamesForRecord(final ILXP record) {

        if ( record.containsKey("TYPE") && (record.get("TYPE").equals(PersonLabels.TYPE) ) ) {

            // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

            String forename = record.get(PersonLabels.FORENAME);
            String surname = record.get(PersonLabels.SURNAME);
            String mmsurname = record.get(PersonLabels.MOTHERS_MAIDEN_SURNAME);
            String sex = record.get(PersonLabels.SEX);

            StringBuilder builder = new StringBuilder();

            builder.append(forename);
            if( sex.equals("F") && mmsurname != null && ! mmsurname.equals("") ) {
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


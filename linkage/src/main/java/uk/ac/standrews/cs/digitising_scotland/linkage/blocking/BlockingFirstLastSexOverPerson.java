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

            StringBuilder builder = new StringBuilder();

            builder.append(record.get(PersonLabels.FORENAME));
            builder.append(record.get(PersonLabels.SURNAME));
            builder.append(record.get(PersonLabels.SEX));

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


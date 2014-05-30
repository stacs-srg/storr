package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operations.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.Birth;

/**
 * This class blocks based on baby's first name, baby's last name and father's first name, father's last name over streams of Birth records.
 * Created by al on 02/05/2014. x
 */
public class BlockingBFF_BFL_MPF_MPL extends Blocker {

    public BlockingBFF_BFL_MPF_MPL(final IBucket birthsBucket, final IRepository output_repo) throws RepositoryException {

        super(birthsBucket.getInputStream(), output_repo);
    }

    /**
     * @param record - a record to be blocked
     * @return the blocking keys - one for baby and one for father
     */
    public String[] determineBlockedBucketNamesForRecord(final ILXP record) {

        // Only operates over birth records

        // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.
        String key1 = record.get(Birth.FORENAME);
        String baby_surname = record.get(Birth.SURNAME);
        key1 = key1 + baby_surname;
        key1 = removeNasties(key1);

        String key2 = record.get(Birth.FATHERS_FORENAME);
        String fathers_surname = record.get(Birth.FATHERS_SURNAME);
        key2 = key2 + (fathers_surname.equals("0") ? baby_surname : fathers_surname); // fathers surname coded as "0" if same as baby
        key2 = removeNasties(key2);

        return new String[]{key1, key2};
    }

    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(String key) {
        return key.replace("/", "");
    }
}


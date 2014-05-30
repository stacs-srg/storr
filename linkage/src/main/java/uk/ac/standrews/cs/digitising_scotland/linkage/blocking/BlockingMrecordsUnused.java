package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.merger.TailToTailMergedStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operations.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * This class blocks based on persons' first name, last name and first name of parents over streams of BDM Marriage records.
 * Created by al on 02/05/2014. x
 */
public class BlockingMrecordsUnused extends Blocker {

    public BlockingMrecordsUnused(final IBucket birthsBucket, final IBucket deathsBucket, final IBucket marriagesBucket, final IRepository output_repo) throws RepositoryException {

        super(new TailToTailMergedStream(new ILXPInputStream[]{birthsBucket.getInputStream(), deathsBucket.getInputStream(), marriagesBucket.getInputStream()}), output_repo);
    }

    public String[] determineBlockedBucketNamesForRecord(final ILXP record) {

        if (record.containsKey("TYPE") && record.get("TYPE").equals("marriage")) {
            // make groom and bride separately.

            String bride_key = record.get("bride_forename"); // TODO - if we use this fix the labels
            bride_key = bride_key + record.get("bride_surname");
            bride_key = bride_key + record.get("bride_fathers_forename");
            bride_key = bride_key + record.get("bride_mothers_forename");
            bride_key = removeNasties(bride_key);

            String groom_key = record.get("groom_forename");
            groom_key = groom_key + record.get("groom_surname");
            groom_key = groom_key + record.get("groom_fathers_forename");
            groom_key = groom_key + record.get("groom_mothers_forename");
            groom_key = removeNasties(groom_key);

            return new String[]{bride_key, groom_key};

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


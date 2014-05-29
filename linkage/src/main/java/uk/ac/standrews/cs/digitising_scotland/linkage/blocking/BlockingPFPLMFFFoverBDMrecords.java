package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.merger.TailToTailMergedStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operations.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.CommonLabels;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * This class blocks based on persons' first name, last name and first name of parents over streams of BDM Marriage records.
 * Created by al on 02/05/2014. x
 */
public class BlockingPFPLMFFFoverBDMrecords extends Blocker {

    public BlockingPFPLMFFFoverBDMrecords(IBucket birthsBucket, IBucket deathsBucket, IBucket marriagesBucket, IRepository output_repo) throws RepositoryException {

        super(new TailToTailMergedStream(new ILXPInputStream[]{birthsBucket.getInputStream(), deathsBucket.getInputStream()}), output_repo);
    }

    @Override
    public String[] determineBlockedBucketNamesForRecord(ILXP record) {
        if (record.containsKey("TYPE") && (record.get("TYPE").equals("birth") || record.get("TYPE").equals("death"))) {

            // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.
            String key = record.get(CommonLabels.FORENAME);
            key = key + record.get(CommonLabels.SURNAME);
            key = key + record.get(CommonLabels.FATHERS_FORENAME);
            key = key + record.get(CommonLabels.MOTHERS_FORENAME);
            key = removeNasties(key);
            return new String[]{key};
        } else {
            ErrorHandling.error("Record with unknown type in input Stream - ignoring");
            return null;
        }
    }

    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(String key) {
        return key.replace("/", "");
    }
}


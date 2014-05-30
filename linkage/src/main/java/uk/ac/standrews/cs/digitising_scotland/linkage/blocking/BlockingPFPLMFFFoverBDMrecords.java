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

    public BlockingPFPLMFFFoverBDMrecords(final IBucket birthsBucket, final IBucket deathsBucket, final IRepository output_repo) throws RepositoryException {

        super(new TailToTailMergedStream(new ILXPInputStream[]{birthsBucket.getInputStream(), deathsBucket.getInputStream()}), output_repo);
    }

    @Override
    public String[] determineBlockedBucketNamesForRecord(final ILXP record) {

        if (record.containsKey("TYPE") && (record.get("TYPE").equals("birth") || record.get("TYPE").equals("death"))) {

            // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

            StringBuilder builder = new StringBuilder();

            builder.append(record.get(CommonLabels.FORENAME));
            builder.append(record.get(CommonLabels.SURNAME));
            builder.append(record.get(CommonLabels.FATHERS_FORENAME));
            builder.append(record.get(CommonLabels.MOTHERS_FORENAME));

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


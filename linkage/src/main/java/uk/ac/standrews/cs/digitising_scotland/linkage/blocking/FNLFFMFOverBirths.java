package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.merger.TailToTailMergedStreamTypedOld;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucketTypedOLD;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStreamTypedOld;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.BirthTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.CommonTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.DeathTypeLabel;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * This class blocks based on persons' first name, last name and first name of parents over streams of Birth records
 * Created by al on 02/05/2014. x
 */
public class FNLFFMFOverBirths extends Blocker {

    public FNLFFMFOverBirths(final IBucketTypedOLD birthsBucket, final IBucketTypedOLD deathsBucket, final IRepository output_repo) throws RepositoryException {

        super(new TailToTailMergedStreamTypedOld(new ILXPInputStreamTypedOld[]{birthsBucket.getInputStream(), deathsBucket.getInputStream()}), output_repo, LXP.getInstance());
    }

    @Override
    public String[] determineBlockedBucketNamesForRecord(final ILXP record) {

        if (record.containsKey(CommonTypeLabel.TYPE_LABEL)) {
            if (record.get(CommonTypeLabel.TYPE_LABEL).equals(BirthTypeLabel.TYPE) ||
                    record.get(CommonTypeLabel.TYPE_LABEL).equals(DeathTypeLabel.TYPE)) {

                // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

                StringBuilder builder = new StringBuilder();

                builder.append(record.get(BirthTypeLabel.FORENAME));
                builder.append(record.get(BirthTypeLabel.SURNAME));
                builder.append(record.get(BirthTypeLabel.FATHERS_FORENAME));
                builder.append(record.get(BirthTypeLabel.MOTHERS_FORENAME));

                return new String[]{removeNasties(builder.toString())};
            } else {
                ErrorHandling.error("Record with unknown type in input Stream: " + record.get(CommonTypeLabel.TYPE_LABEL) + " - ignoring");
                return new String[]{};
            }

        } else {
            ErrorHandling.error("Record with no specified type in input Stream - ignoring");
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


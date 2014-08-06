package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels;

/**
 * This class blocks on streams of Person records.
 * The categories of blocking are:
 *
 *  1.  FNLN     First name, Last name
 *  2.	FNLNMF   First name, Last name, Mothers First name
 *  3.	FNLNFF   First name, Last name, Fathers First name
 *  4.	FNLNMFFF First name, Last name, Mothers First name, Fathers First name
 *  5. 	FNMF     First name, Mothers First name
 *  6.  FNFF     First name, Fathers First name
 *  7.  FNFL     First name, Fathers Last name
 *  8.  MFMMFF   Mothers Fist name, Mothers Maiden name, Fathers First name  (not marriage)
 *
 * Created by al on 01/08/2014.
 */
public class MultipleBlockerOverPerson extends Blocker {

    public MultipleBlockerOverPerson(final IBucket birthsBucket, final IRepository output_repo) throws RepositoryException {

        super(birthsBucket.getInputStream(), output_repo);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for father
     */
    public String[] determineBlockedBucketNamesForRecord(final ILXP record) {

        // Only operates over person records

        // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

        String FN = record.get(PersonLabels.FORENAME);
        String LN = record.get(PersonLabels.SURNAME);
        String FF = record.get(PersonLabels.FATHERS_FORENAME);
        String FL = record.get(PersonLabels.FATHERS_SURNAME);
        FL = FL.equals("0") ? LN : FL; // fathers surname coded as "0" if same as baby
        String MF = record.get(PersonLabels.MOTHERS_FORENAME);
        String MM = record.get(PersonLabels.MOTHERS_MAIDEN_SURNAME);

        String FNLN = FN + LN;
        String FNLNMF = FNLN + MF;
        String FNLNFF = FNLN + FF;
        String FNLNMFFF = FNLN + MF + FF;
        String FNMF = FN + MF;
        String FNFF = FN + FF;
        String FNFL = FN + FL;
        String MFMMFF = MF + MM + FF;

        return new String[]{FNLN,FNLNMF,FNLNFF,FNLNMFFF,FNMF,FNFF,FNFL,MFMMFF };
    }

    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(final String key) {
        return key.replace("/", "");
    }
}


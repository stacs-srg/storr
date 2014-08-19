package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.util.ArrayList;

/**
 * This class blocks on streams of Person records.
 * The categories of blocking are:
 * <p/>
 * 1.  FNLN     First name, Last name
 * 2.	FNLNMF   First name, Last name, Mothers First name
 * 3.	FNLNFF   First name, Last name, Fathers First name
 * 4.	FNLNMFFF First name, Last name, Mothers First name, Fathers First name
 * 5. 	FNMF     First name, Mothers First name
 * 6.  FNFF     First name, Fathers First name
 * 7.  FNFL     First name, Fathers Last name
 * 8.  MFMMFF   Mothers Fist name, Mothers Maiden name, Fathers First name  (not marriage)
 * <p/>
 * Created by al on 01/08/2014.
 */
public class MultipleBlockerOverPerson extends Blocker {

    public MultipleBlockerOverPerson(final IBucket peopleBucket, final IRepository output_repo) throws RepositoryException {

        super(peopleBucket.getInputStream(), output_repo);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for father
     */
    public String[] determineBlockedBucketNamesForRecord(final ILXP record) {

        // Only operates over person records

        if (record.containsKey("TYPE") ) {

            if( record.get("TYPE").equals(PersonLabels.TYPE) ) {


                // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

                String FN = record.get(PersonLabels.FORENAME);
                String LN = record.get(PersonLabels.SURNAME);
                String FF = record.get(PersonLabels.FATHERS_FORENAME);
                String FL = record.get(PersonLabels.FATHERS_SURNAME);
                FL = (FL == null || FL.equals("0")) ? LN : FL; // fathers surname coded as "0" if same as baby
                String MF = record.get(PersonLabels.MOTHERS_FORENAME);
                String MM = record.get(PersonLabels.MOTHERS_MAIDEN_SURNAME);

                String FNLN = removeNasties(FN + LN);
                String FNLNMF = removeNasties(FNLN + MF);
                String FNLNFF = removeNasties(FNLN + FF);
                String FNLNMFFF = removeNasties(FNLN + MF + FF);
                String FNMF = removeNasties(FN + MF);
                String FNFF = removeNasties(FN + FF);
                String FNFL = removeNasties(FN + FL);
                String MFMMFF = removeNasties(MF + MM + FF);

                String[] blocked_names = new String[]{FNLN, FNLNMF, FNLNFF, FNLNMFFF, FNMF, FNFF, FNFL, MFMMFF};
                return dedup(blocked_names);
            } else {
                ErrorHandling.error("Non person record in input Stream - type is: " + record.get("TYPE") );
                return new String[]{};
            }

        } else {
            ErrorHandling.error("Record with no type label in input Stream - ignoring");
            return new String[]{};
        }
    }

    private String[] dedup(String[] blocked_names) {

        ArrayList<String> deduped = new ArrayList<String>();
        for( String name : blocked_names ) {
            if( ! deduped.contains(name ) ) {
                deduped.add(name);
            }
        }

        return deduped.toArray( new String[0] );
    }

    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(final String key) {
        return key.replace("/", "");
    }
}


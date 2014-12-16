package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Person;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.IOException;
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
public class MultipleBlockerOverPerson extends Blocker<Person> {

    public MultipleBlockerOverPerson(final IBucket peopleBucket, final IRepository output_repo, ILXPFactory<Person> tFactory) throws BucketException, RepositoryException, IOException {

        super(peopleBucket.getInputStream(), output_repo, tFactory);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for father
     */
    public String[] determineBlockedBucketNamesForRecord(final Person record) {

        // Only operates over person records

        try {
            String FN = record.getString(Person.FORENAME);
            String LN = record.getString(Person.SURNAME);
            String FF = record.getString(Person.FATHERS_FORENAME);
            String FL = record.getString(Person.FATHERS_SURNAME);
            FL = (FL == null || FL.equals("0")) ? LN : FL; // TODO fix these - fathers surname coded as "0" if same as baby
            String MF = record.getString(Person.MOTHERS_FORENAME);
            String MM = record.getString(Person.MOTHERS_MAIDEN_SURNAME);

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
        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            return new String[]{};
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            return new String[]{};
        }
    }

    private String[] dedup(String[] blocked_names) {

        ArrayList<String> deduped = new ArrayList<String>();
        for (String name : blocked_names) {
            if (!deduped.contains(name)) {
                deduped.add(name);
            }
        }

        return deduped.toArray(new String[0]);
    }

    /**
     * @param key - a String key to be made into an acceptable bucket name
     * @return the cleaned up String
     */
    private String removeNasties(final String key) {
        return key.replace("/", "");
    }

}


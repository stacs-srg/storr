package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.BirthTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Blocker;

import java.io.IOException;

/**
 * This class blocks based on persons' first name, last name and first name of parents over streams of Birth records
 * Created by al on 02/05/2014. x
 */
public class FNLFFMFOverBirths extends Blocker<Birth> {

    public FNLFFMFOverBirths(final IBucket<Birth> birthsBucket,
                             final IRepository output_repo) throws RepositoryException, IOException {

        super( birthsBucket.getInputStream(), output_repo, new BirthFactory(TypeFactory.getInstance().typeWithname("Birth").getId()));
    }

    @Override
    public String[] determineBlockedBucketNamesForRecord(final Birth record) {

        // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

        StringBuilder builder = new StringBuilder();

        try {
            builder.append(record.get(BirthTypeLabel.FORENAME));            // TODO this is still not good enough
            builder.append(record.get(BirthTypeLabel.SURNAME));
            builder.append(record.get(BirthTypeLabel.FATHERS_FORENAME));
            builder.append(record.get(BirthTypeLabel.MOTHERS_FORENAME));
            return new String[]{removeNasties(builder.toString())};

        } catch (KeyNotFoundException e) {
            e.printStackTrace(); // TODO fix
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


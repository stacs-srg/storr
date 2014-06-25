package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operations.sharder.AbstractPairwiseLinker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IPair;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.SameAsLabels;

/**
 * Created by al on 19/06/2014.
 *
 * Links ILXP records with labels drawn from @link uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels
 * Attempts to find birth records with the same person in different roles (e.g. mother-baby and father-baby).
 *
 */
public class BirthBirthLinker extends AbstractPairwiseLinker {

    public BirthBirthLinker(final ILXPInputStream input, final ILXPOutputStream output) {

        super(input, output);
    }

    @Override
    public boolean compare(final IPair pair) {

        ILXP first = pair.first();
        ILXP second = pair.second();

        // Return true if we have person in different roles

        if( ( first.get(PersonLabels.ROLE).equals("baby") && second.get(PersonLabels.ROLE).equals("mother") ) ||
            ( first.get(PersonLabels.ROLE).equals("mother") && second.get(PersonLabels.ROLE).equals("baby") ) ||
            ( first.get(PersonLabels.ROLE).equals("baby") && second.get(PersonLabels.ROLE).equals("father") ) ||
            ( first.get(PersonLabels.ROLE).equals("father") && second.get(PersonLabels.ROLE).equals("baby") ) ) {
            return true;
        }
        return false;
    }

    @Override
    public void addToResults(final IPair pair, final ILXPOutputStream results) {

        ILXP first = pair.first();
        ILXP second = pair.second();

        ILXP result_record = new LXP();
        result_record.put(SameAsLabels.first, Integer.toString(first.getId()));
        result_record.put(SameAsLabels.second, Integer.toString(second.getId()));
        result_record.put(SameAsLabels.relationship, first.get(PersonLabels.ROLE) + "-" + second.get(PersonLabels.ROLE) );
        result_record.put(SameAsLabels.resolver, this.getClass().toString());

        results.add(result_record);
    }
}

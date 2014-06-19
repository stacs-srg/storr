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
 */
public class PersonLinker extends AbstractPairwiseLinker {

    public PersonLinker(final ILXPInputStream input, final ILXPOutputStream output) {

        super(input, output);
    }

    @Override
    public boolean compare(final IPair pair) {

        ILXP first = pair.first();
        ILXP second = pair.second();

        // Cannot match baby-baby: only one birth only baby-father and baby-mother - different roles.

        if( first.get(PersonLabels.ROLE).equals("baby") && second.get(PersonLabels.ROLE).equals("baby") ) {
            return false;
        }
        return true;  // make rest match for now.
    }

    @Override
    public void addToResults(final IPair pair, final ILXPOutputStream results) {

        ILXP first = pair.first();
        ILXP second = pair.second();

        System.out.println("Matched : " + first + "with:" + second);

        ILXP result_record = new LXP();
        result_record.put(SameAsLabels.first, first.getId() );
        result_record.put(SameAsLabels.second, second.getId() );
        result_record.put(SameAsLabels.relationship, first.get(PersonLabels.ROLE) + "-" + second.get(PersonLabels.ROLE) );
        result_record.put(SameAsLabels.resolver, this.getClass().toString());

        results.add(result_record);
    }
}

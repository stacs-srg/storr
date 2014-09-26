package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.sharder.AbstractPairwiseLinker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStreamTypedOld;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStreamTypedOLD;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IPair;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.SameAsTypeLabel;

/**
 * Created by al on 19/06/2014.
 *
 * Links ILXP records with labels drawn from @link uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels
 * Attempts to find birth records with the same person in different roles (e.g. mother-baby and father-baby).
 *
 */
public class BirthBirthLinker extends AbstractPairwiseLinker<ILXP> {

    public BirthBirthLinker(final ILXPInputStreamTypedOld input, final ILXPOutputStreamTypedOLD output) {

        super(input, output);
    }

    @Override
    public boolean compare(final IPair<ILXP> pair) {

        ILXP first = pair.first();
        ILXP second = pair.second();

        // Return true if we have person in different roles

        if( ( first.get(PersonTypeLabel.ROLE).equals("baby") && second.get(PersonTypeLabel.ROLE).equals("mother") ) ||
            ( first.get(PersonTypeLabel.ROLE).equals("mother") && second.get(PersonTypeLabel.ROLE).equals("baby") ) ||
            ( first.get(PersonTypeLabel.ROLE).equals("baby") && second.get(PersonTypeLabel.ROLE).equals("father") ) ||
            ( first.get(PersonTypeLabel.ROLE).equals("father") && second.get(PersonTypeLabel.ROLE).equals("baby") ) ) {
            return true;
        }
        return false;
    }

    @Override
    public void addToResults(final IPair pair, final ILXPOutputStreamTypedOLD results) {

        ILXP first = pair.first();
        ILXP second = pair.second();

        // get the people in the right order parent first

        if( ! second.get(PersonTypeLabel.ROLE).equals("baby") ) {
            ILXP temp = second;
            second = first;
            first = temp;
        }

        ILXP result_record = new LXP();
        result_record.put(SameAsTypeLabel.first, Integer.toString(first.getId()));
        result_record.put(SameAsTypeLabel.second, Integer.toString(second.getId()));
        result_record.put(SameAsTypeLabel.relationship, first.get(PersonTypeLabel.ROLE) + "-" +second.get(PersonTypeLabel.ROLE) );
    //    result_record.put(SameAsLabels.resolver, this.getClass().toString());

        results.add(result_record);
    }
}

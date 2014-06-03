package factory;

import interfaces.ILabels;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;

/**
 * Created by al on 30/05/2014.
 */
public class ILXPfactory {

    public static ILXP makeLXP(int id, ILabels labels) {

        Iterable<String> field_names = labels.get_field_names();
        LXP record = new LXP(id);

        for ( String field : field_names ) {    // Populate the LXP with fields all set to null.
            record.put( field,"" );
        }
        return record;
    }
}

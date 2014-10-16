package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.TypeLabel;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ITypeLabel;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by al on 14/10/2014.
 */
public abstract class AbstractLXP extends LXP {

    protected int required_type_labelID;

    public AbstractLXP() {

        super();
    }

    public AbstractLXP(int object_id) {

        super(object_id);
    }

    public AbstractLXP(int object_id, JSONReader reader) throws PersistentObjectException {
        super( object_id,reader);
    }

    public AbstractLXP(JSONReader reader) throws PersistentObjectException {
        super(reader);
    }

    /*
     * This says that we can we can create an instance of this type iff the labels supplied in the label_id are present
     */
    @Override
    public boolean checkConsistentWith(int label_id) {
        // do id check first
        if( required_type_labelID == label_id ) {
            return true;
        }
        // if that doesn't work do structural check
        try {
            ILXP record = Store.getInstance().get( label_id );
            ITypeLabel stored_label = new TypeLabel( record  );
            ILXP required_type_label_lxp = Store.getInstance().get( label_id );
            ITypeLabel required_label = new TypeLabel( required_type_label_lxp  );
            Collection<String> required = required_label.getLabels();
            for( String label : required ) {
                if( required_label.getFieldType(label) != stored_label.getFieldType(label)) {
                    return false;
                }
            }
            return true;
        } catch (PersistentObjectException e) {
            ErrorHandling.error("PersistentObjectException - returning false");
            return false;
        } catch (IOException e) {
            ErrorHandling.error( "PersistentObjectException - returning false" );
            return false;
        } catch (KeyNotFoundException e) {
            ErrorHandling.error( "Key not found - returning false" );
            return false;
        }
    }
}

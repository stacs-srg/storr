package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.types.LXPBaseType;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.LXPReferenceType;

/**
 * Created by al on 31/10/14.
 */
public interface IType {

    public boolean isReferenceType();

    public boolean isBaseType();

    public LXPReferenceType getReferenceType();

    public LXPBaseType getBaseType();

}

package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IOutputStream;

/**
 * Created by al on 09/10/2014.
 */
public class TypedOutputStream<T extends ILXP> implements IOutputStream {
    private final ILXPOutputStream outputStream;

    public TypedOutputStream(ILXPOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void add(ILXP record) { // types???
        // We know this is safe
        outputStream.add(record);
    }

}

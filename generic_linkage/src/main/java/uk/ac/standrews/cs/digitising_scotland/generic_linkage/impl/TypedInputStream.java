package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;

import java.util.Iterator;

/**
 * Created by al on 09/10/2014.
 */
public class TypedInputStream<T extends ILXP> implements IInputStream {

    private final Iterator<ILXP> underlying;
    private final ILXPFactory tFactory;

    public TypedInputStream(ILXPInputStream inputStream, ILXPFactory tFactory) {
        underlying = inputStream.iterator();
        this.tFactory = tFactory;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return underlying.hasNext();
            }

            @Override
            public T next() {
                return (T) tFactory.convert(underlying.next());
            }  // types???

            @Override
            public void remove() {
                underlying.remove();
            }
        };
    }
}

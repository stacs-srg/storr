package uk.ac.standrews.cs.digitising_scotland.linkage.old;

import java.util.Iterator;

/**
 * Created by graham on 28/05/2014.
 */
public abstract class RecordIterator2<RecordType> implements Iterator<RecordType>, Iterable<RecordType> {

    protected int size;

    @Override
    public Iterator<RecordType> iterator() {

        return this;
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove");
    }

    public int size() {

        return size;
    }

    public abstract void close();
}

package uk.ac.standrews.cs.digitising_scotland.util;


import java.util.Iterator;

/**
 * Created by graham on 02/05/2014.
 */
public class FilteredIterator<T> implements Iterator<T> {

    private final Iterator<T> iterator;
    private final Condition<T> condition;

    private T next = null;

    public FilteredIterator(final Iterator<T> iterator, final Condition<T> condition) {

        this.iterator = iterator;
        this.condition = condition;

        loadNext();
    }

    private void loadNext()  {

        if (iterator.hasNext()) {
            next = iterator.next();
            while (iterator.hasNext() && !condition.test(next)) {
                next = iterator.next();
            }
            if (!condition.test(next)) {
                next = null;
            }
        }
        else {
            next = null;
        }
    }

    @Override
    public boolean hasNext() {

        return next != null;
    }

    @Override
    public T next() {

        T data = next;
        loadNext();

        return data;
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove");
    }
}

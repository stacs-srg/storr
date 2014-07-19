/**
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module util.
 *
 * util is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * util is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with util. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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

    private void loadNext() {

        if (iterator.hasNext()) {
            next = iterator.next();
            while (iterator.hasNext() && !condition.test(next)) {
                next = iterator.next();
            }
            if (!condition.test(next)) {
                next = null;
            }
        } else {
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

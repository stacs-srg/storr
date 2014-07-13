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
public class MappedIterator<T1, T2> implements Iterator<T2> {

    private final Iterator<T1> iterator;
    private final Map<T1, T2> mapper;

    public MappedIterator(final Iterator<T1> iterator, final Map<T1, T2> mapper) {

        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {

        return iterator.hasNext();
    }

    @Override
    public T2 next() {

        return mapper.map(iterator.next());
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove");
    }
}

/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

/**
 * Generic class representing a pair of objects.
 *
 * @param <L> the generic type held in 'left'
 * @param <R> the generic type held in 'right'
 */
public class Pair<L, R> {

    /** The left object. */
    private L left;
    /** The right object. */
    private R right;

    /**
     * Instantiates a new pair.
     *
     * @param left the left
     * @param right the right
     */
    public Pair(final L left, final R right) {

        this.left = left;
        this.right = right;
    }

    /**
     * Sets the left object.
     *
     * @param left the new left
     */
    public void setLeft(final L left) {

        this.left = left;
    }

    /**
     * Sets the right object.
     *
     * @param right the new right
     */
    public void setRight(final R right) {

        this.right = right;
    }

    /**
     * Gets the left object.
     *
     * @return the left
     */
    public L getLeft() {

        return left;
    }

    /**
     * Gets the right object.
     *
     * @return the right
     */
    public R getRight() {

        return right;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "Pair [left=" + left + ", right=" + right + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return left.hashCode() ^ right.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {

        if (o == null) { return false; }
        if (!(o instanceof Pair)) { return false; }
        Pair<?, ?> pairo = (Pair<?, ?>) o;
        return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
    }

}

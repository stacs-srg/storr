/**
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Represents a partnership between two people, with an optional marriage date.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Ilia Shumailov (is33@st-andrews.ac.uk)
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 */
 class CompactPartnership implements Comparable<CompactPartnership> {

    private final int id;

    private final int partner1_index;
    private final int partner2_index;
    private final int marriage_date;
    private List<Integer> children;
    private boolean marked;

    /**
     * Creates a partnership with a given marriage date.
     *
     * @param partner1_index the index of the first partner
     * @param partner2_index the index of the second partner
     * @param marriage_date  the marriage date
     */
    protected CompactPartnership(final int partner1_index, final int partner2_index, final int marriage_date) {

        this.partner1_index = partner1_index;
        this.partner2_index = partner2_index;
        this.marriage_date = marriage_date;

        id = IDFactory.getNextID();
        marked = false;
    }

    /**
     * Creates a new partnership with the given people, and links each person to the partnership.
     *
     * @param partner1       the first partner
     * @param partner1_index the index of the first partner
     * @param partner2       the second partner
     * @param partner2_index the index of the second partner
     * @param marriage_date  the marriage date
     */
    protected CompactPartnership(final CompactPerson partner1, final int partner1_index, final CompactPerson partner2, final int partner2_index, final int marriage_date) {

        this(partner1_index, partner2_index, marriage_date);

        partner1.addPartnership(this);
        partner2.addPartnership(this);
    }

    /**
     * Gets an id for the partnership.
     *
     * @return the id of this object
     */
    protected int getId() {

        return id;
    }

    /**
     * Gets the partner of the given person.
     *
     * @param p the index of one person in this partnership
     * @return the partner index of the given person, or -1 if the person is not in the partnership
     */
    protected int getPartner(final int p) {

        return partner1_index == p ? partner2_index : partner2_index == p ? partner1_index : -1;
    }

    /**
     * Gets the first member of this partnership.
     *
     * @return the first member of this partnership
     */
    protected int getPartner1() {

        return partner1_index;
    }

    /**
     * Gets the second member of this partnership.
     *
     * @return the second member of this partnership
     */
    protected int getPartner2() {

        return partner2_index;
    }

    public int compareTo(@Nonnull final CompactPartnership other) {

        final int this_date = getMarriageDate();
        final int other_date = other.getMarriageDate();

        if (this_date < other_date) {
            return -1;
        }
        if (this_date > other_date) {
            return 1;
        }
        if (this == other) {
            return 0;
        }

        return hashCode() < other.hashCode() ? -1 : 1;
    }

    @Override
    public boolean equals(final Object o) {

        return this == o || o instanceof CompactPartnership && compareTo((CompactPartnership) o) == 0;
    }

    @Override
    public int hashCode() {

        return id;
    }

    /**
     * Gets the marriage date of this partnership.
     *
     * @return the date.
     */
    protected int getMarriageDate() {

        return marriage_date;
    }

    /**
     * Gets the child_ids associated with this partnership.
     *
     * @return the child_ids.
     */
    protected List<Integer> getChildren() {

        return children;
    }

    /**
     * Set the child_ids to be the child_ids of this partnership.
     *
     * @param children to associate with the partnership.
     */
    protected void setChildren(final List<Integer> children) {

        this.children = children;
    }

    /**
     * Tests whether this partnership has been marked.
     * @return true if this partnership has been marked
     */
    protected boolean isMarked() {
        return marked;
    }

    /**
     * Sets the marked status of this partnership.
     * @param marked the new marked status
     */
    protected void setMarked(final boolean marked) {
        this.marked = marked;
    }
}

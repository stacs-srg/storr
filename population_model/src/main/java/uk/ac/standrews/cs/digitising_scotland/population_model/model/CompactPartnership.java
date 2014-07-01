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
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a partnership between two people, with an optional marriage date.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Ilia Shumailov (is33@st-andrews.ac.uk)
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 */
public class CompactPartnership implements Comparable<CompactPartnership> {

    private int id;

    protected int partner1_index;
    protected int partner2_index;
    protected int marriage_date;
    protected List<Integer> children;
    private boolean marked;  // TODO remove eventually

    /**
     * Creates a partnership with a given marriage date.
     *
     * @param partner1_index      the first partner's index in the People array
     * @param partner2_index      the second partner's index in the People array
     * @param marriage_date the marriage date
     */
    public CompactPartnership(final int partner1_index, final int partner2_index, final int marriage_date) {

        this.partner1_index = partner1_index;
        this.partner2_index = partner2_index;
        this.marriage_date = marriage_date;

        id = IDFactory.getNextID();
        marked = false;
    }

    /**
     * Creates a new partnership with the given people, and links each person to the partnership.
     *
     * @param partner1      the index of first partner
     * @param partner2      the index of second partner
     * @param marriage_date the marriage date
     */
    public CompactPartnership(final CompactPerson partner1, final int partner1_index, final CompactPerson partner2, final int partner2_index, final int marriage_date) {

        this(partner1_index, partner2_index, marriage_date);
        addPartnershipToPartner(partner1);
        addPartnershipToPartner(partner2);
    }

    /**
     * Gets an id for the partnership.
     *
     * @return the id of this object
     */
    public int getId() {

        return id;
    }

    /**
     * Setter for Id.
     *
     * @param id the id to be set
     */
    public void setId(final int id) {

        this.id = id;
    }

    /**
     * Gets the partner of the given person.
     *
     * @param p the index of one person in this partnership
     * @return the partner index of the given person, or -1 if the person is not in the partnership
     */
    public int getPartner(final int p) {

        return partner1_index == p ? partner2_index : partner2_index == p ? partner1_index : -1;
    }

    /**
     * Gets the first member of this partnership.
     *
     * @return the first member of this partnership
     */
    public int getPartner1() {

        return partner1_index;
    }

    /**
     * Gets the second member of this partnership.
     *
     * @return the second member of this partnership
     */
    public int getPartner2() {

        return partner2_index;
    }

    /**
     * Setter for partner.
     *
     * @param partner1 partner to set
     */
    public void setPartner1(final int partner1) {

        this.partner1_index = partner1;
    }

    /**
     * Setter for partner.
     *
     * @param partner2 partner to set
     */
    public void setPartner2(final int partner2) {

        this.partner2_index = partner2;
    }

    /**
     * Tests whether a given person is a child of this partnership.
     * Uses binary search. Replace with sequential search if order preservation is lost.
     *
     * @param p the person
     * @return true if the person is a child of this partnership
     */
    public boolean includesChild(final int p) {

        if (children != null) {

            int number_of_children = children.size();
            if (number_of_children == 0) {
                return false;
            }

            int binary_step = 1;
            while (binary_step < number_of_children) {
                binary_step <<= 1;
            }

            int index;
            for (index = 0; binary_step != 0; binary_step >>= 1) {
                if (index + binary_step < number_of_children && children.get(index + binary_step) <= p) {
                    index += binary_step;
                }
            }

            if (children.get(index) == p) {
                return true;
            }
        }

        return false;
    }

    public int compareTo(final CompactPartnership other) {

        // No need to override hashCode() since this does conform to the assumption that (p1.compareTo(p2) == 0) == (p1.equals(p2)) i.e. it only returns zero for equal objects.

        final int date_difference = getMarriageDate() - other.getMarriageDate();
        return date_difference != 0 ? date_difference : hashCode() - other.hashCode();
    }

    @Override
    public boolean equals(final Object o) {

        return this == o || !(o == null || !(o instanceof CompactPartnership)) && compareTo((CompactPartnership) o) == 0;
    }

    @Override
    public int hashCode() {

        return id;
    }

    private void addPartnershipToPartner(final CompactPerson person) {

        synchronized (person) {
            if (person.getPartnerships() == null) {
                person.setPartnerships(new ArrayList<CompactPartnership>());
            }
            List<CompactPartnership> partnerships = person.getPartnerships();
            partnerships.add(this);
            Collections.sort(partnerships);
        }
    }

    /**
     * Gets the marriage date of this partnership.
     *
     * @return the date.
     */
    public int getMarriageDate() {

        return marriage_date;
    }

    /**
     * Sets the marriage date of this partnership.
     *
     * @param marriage_date the date of marriage to set
     */
    public void setMarriageDate(final int marriage_date) {

        this.marriage_date = marriage_date;
    }

    /**
     * Gets the children associated with this partnership.
     *
     * @return the children.
     */
    public List<Integer> getChildren() {

        return children;
    }

    /**
     * Set the children to be the children of this partnership.
     *
     * @param children to associate with the partnership.
     */
    public void setChildren(final List<Integer> children) {

        this.children = children;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("{");
        builder.append(getMarriageDate());

        int pcount = 0;
        if (getPartner1() != -1) {
            pcount++;
        }
        if (getPartner2() != -1) {
            pcount++;
        }
        builder.append(", p:" + pcount);
        builder.append(", c:");
        builder.append(children == null ? 0 : children.size());
        builder.append("}");

        return builder.toString();
    }
}

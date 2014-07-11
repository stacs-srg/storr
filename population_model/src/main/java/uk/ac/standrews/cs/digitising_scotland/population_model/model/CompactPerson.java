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

import java.util.List;
import uk.ac.standrews.cs.digitising_scotland.util.BitManipulation;

/**
 * A compact representation of a person, designed for minimal space overhead.
 * Encodes multiple attributes into a field wherever possible.
 * Dates are encoded as integers.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class CompactPerson {


    private static final int POSITION_OF_MALE_BIT = 0;
    private static final int POSITION_OF_PARENTS_BIT = 1;
    private static final int POSITION_OF_INCOMERS_BIT = 2;
    private static final int POSITION_OF_MARKED_BIT = 3;

    protected int id;
    protected int birth_date = -1;
    protected int death_date = -1;

    private List<CompactPartnership> partnership_list;
    private byte bits = 0; // Used to store various boolean properties.

    /**
     * Creates a person.
     *
     * @param birth_date the date of birth represented in days elapsed from the start of the simulation
     * @param male       true if the person is male
     */
    public CompactPerson(final int birth_date, final boolean male) {

        this.birth_date = birth_date;
        setMale(male);
    }

    public CompactPerson(final int birth_date, final boolean male, final int id) {

        this(birth_date, male);
        this.id = id;
    }

    /**
     * Gets an id for the person.
     *
     * @return the id of this person
     */
    public int getId() {

        return id;
    }

    public CompactPartnership mostRecentPartnership() {

        return partnership_list != null ? partnership_list.get(getPartnerships().size() - 1) : null;
    }

    public char getSex() {

        return isMale() ? IPerson.MALE : IPerson.FEMALE;
    }

    /**
     * Tests whether the given people are of opposite sex.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return true if the people are of opposite sex
     */
    public static boolean oppositeSex(final CompactPerson person1, final CompactPerson person2) {

        return person1.isMale() != person2.isMale();
    }

    /**
     * Tests whether this person is male.
     *
     * @return true if this person is male
     */
    public boolean isMale() {

        return BitManipulation.readBit(bits, POSITION_OF_MALE_BIT);
    }

    /**
     * Sets the sex of this person.
     *
     * @param male true if this person is male
     */
    public void setMale(final boolean male) {

        bits = BitManipulation.writeBit(bits, male, POSITION_OF_MALE_BIT);
    }

    public boolean isMarked() {

        return BitManipulation.readBit(bits, POSITION_OF_MARKED_BIT);
    }

    /**
     * Records if a record has been visited.
     *
     * @param marked true if a record has been visited
     */
    public void setMarked(final boolean marked) {

        bits = BitManipulation.writeBit(bits, marked, POSITION_OF_MARKED_BIT);
    }

    public boolean hasParents() {

        return BitManipulation.readBit(bits, POSITION_OF_PARENTS_BIT);
    }

    /**
     * Records this person as having parents.
     */
    public void setHasParents() {

        bits = BitManipulation.writeBit(bits, true, POSITION_OF_PARENTS_BIT);
    }

    public boolean isIncomer() {

        return BitManipulation.readBit(bits, POSITION_OF_INCOMERS_BIT);
    }

    /**
     * Records this person as being an incomer.
     */
    public void setIsIncomer() {

        bits = BitManipulation.writeBit(bits, true, POSITION_OF_INCOMERS_BIT);
    }

    /**
     * Get the date of birth of this person.
     *
     * @return the date of birth.
     */
    public int getBirthDate() {

        return birth_date;
    }

    public int getDeathDate() {

        return death_date;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("{");
        builder.append(getBirthDate());
        builder.append("-");
        if (getDeathDate() != -1) {
            builder.append(getDeathDate());
        }
        if (getPartnerships() != null) {
            builder.append(", p:");
            builder.append(getPartnerships().size());
        }
        builder.append("}");

        return builder.toString();
    }

    /**
     * Get the list of partnerships in which this person has been a member.
     *
     * @return the partnerships
     */
    public List<CompactPartnership> getPartnerships() {

        return partnership_list;
    }

    /**
     * Setter for partnership_list.
     *
     * @param partnership_list list to set
     */
    public void setPartnerships(final List<CompactPartnership> partnership_list) {

        this.partnership_list = partnership_list;
    }
}

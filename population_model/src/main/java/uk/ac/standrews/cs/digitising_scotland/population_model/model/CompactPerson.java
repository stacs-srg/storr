package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.util.BitManipulation;

import java.util.List;

/**
 * A compact representation of a person, designed for minimal space overhead.
 * Encodes multiple attributes into a field wherever possible.
 * Dates are encoded as integers.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class CompactPerson {

    private int id;

    private static final int POSITION_OF_MALE_BIT = 0;
    private static final int POSITION_OF_PARENTS_BIT = 1;
    private static final int POSITION_OF_INCOMERS_BIT = 2;
    private static final int POSITION_OF_MARKED_BIT = 3;

    protected int date_of_birth = -1;
    protected int date_of_death = -1;

    private List<CompactPartnership> partnership_list;
    private byte bits = 0; // Used to store various boolean properties.

    // -------------------------------------------------------------------------------------------------------

    /**
     * Creates a person.
     * @param date_of_birth the date of birth represented in days elapsed from the start of the simulation
     * @param male true if the person is male
     */
    public CompactPerson(final int date_of_birth, final boolean male) {

        this.date_of_birth = date_of_birth;
        setMale(male);
        id = IDFactory.getNextID();
    }

    // ------------------------------------------------------------------------------------------------------

    /**
     * Gets an id for the person.
     * @return the id of this person
     */
    public int getId() {

        return id;
    }

    public CompactPartnership mostRecentPartnership() {

        return getPartnerships().get(getPartnerships().size() - 1);
    }

    public String getSex() {

        return isMale() ? "M" : "F";
    }

    /**
     * Tests whether the given people are of opposite sex.
     * @param person1 the first person
     * @param person2 the second person
     * @return true if the people are of opposite sex
     */
    public static boolean oppositeSex(final CompactPerson person1, final CompactPerson person2) {

        return person1.isMale() != person2.isMale();
    }

    /**
     * Tests whether this person is male.
     * @return true if this person is male
     */
    public boolean isMale() {

        return BitManipulation.readBit(bits, POSITION_OF_MALE_BIT);
    }

    /**
     * Sets the sex of this person.
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
     * @return the date of birth.
     */
    public int getDateOfBirth() {

        return date_of_birth;
    }

    public int getDateOfDeath() {

        return date_of_death;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("{");
        builder.append(getDateOfBirth());
        builder.append("-");
        if (getDateOfDeath() != -1) builder.append(getDateOfDeath());
        if (getPartnerships() != null) {
            builder.append(", p:");
            builder.append(getPartnerships().size());
        }
        builder.append("}");

        return builder.toString();
    }

    /**
     * Get the list of partnerships in which this person has been a member.
     * @return the partnerships
     */
    public List<CompactPartnership> getPartnerships() {

        return partnership_list;
    }

    /**
     * Setter for partnership_list.
     * @param partnership_list list to set
     */
    public void setPartnerships(final List<CompactPartnership> partnership_list) {

        this.partnership_list = partnership_list;
    }
}

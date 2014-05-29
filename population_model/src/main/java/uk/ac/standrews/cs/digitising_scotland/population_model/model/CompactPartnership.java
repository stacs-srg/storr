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
 */
public class CompactPartnership implements Comparable<CompactPartnership> {

    private int id;

    protected int partner1;
    protected int partner2;
    protected int marriage_date;
    protected List<Integer> children;
    private boolean marked;

    /**
     * Creates a partnership with a given marriage date.
     * @param partner1 the first partner's index in the People array
     * @param partner2 the second partner's index in the People array
     * @param marriage_date the marriage date
     */
    public CompactPartnership(final int partner1, final int partner2, final int marriage_date) {

        this.partner1 = partner1;
        this.partner2 = partner2;
        this.marriage_date = marriage_date;

        id = IDFactory.getNextID();
        marked = false;
    }

    /**
     * Gets an id for the partnership.
     * @return the id of this object
     */
    public int getId() {

        return id;
    }

    /**
     * Setter for Id.
     * @param id the id to be set
     */
    public void setId(final int id) {

        this.id = id;
    }

    /**
     * Gets the partner of the given person.
     * @param p the index of one person in this partnership
     * @return the partner index of the given person, or -1 if the person is not in the partnership
     */
    public int getPartner(final int p) {

        return partner1 == p ? partner2 : partner2 == p ? partner1 : -1;
    }

    /**
     * Gets the first member of this partnership.
     * @return the first member of this partnership
     */

    public int getPartner1() {

        return partner1;
    }

    /**
     * Gets the second member of this partnership.
     * @return the second member of this partnership
     */
    public int getPartner2() {

        return partner2;
    }

    /**
     * Setter for partner.
     * @param partner1 partner to set
     */
    public void setPartner1(final int partner1) {

        this.partner1 = partner1;
    }

    /**
     * Setter for partner.
     * @param partner2 partner to set
     */
    public void setPartner2(final int partner2) {

        this.partner2 = partner2;
    }

    /**
     * Tests whether a given person is a child of this partnership.
     * @param p the person
     * @return true if the person is a child of this partnership
     */
    public boolean includesChild(final int p) {

        if (getChildren() != null) {
            for (final int child : getChildren()) {
                if (child == p) { return true; }
            }
        }

        return false;
    }

    /**
     * Creates a new partnership with the given people, and links each person to the partnership.
     * @param partner1 the index of first partner
     * @param partner2 the index of second partner
     * @param marriage_date the marriage date
     */
    public static void createPartnership(final CompactPerson partner1, final int partner1_index, final CompactPerson partner2, final int partner2_index, final int marriage_date) {

        final CompactPartnership partnership = new CompactPartnership(partner1_index, partner2_index, marriage_date);
        addPartnership(partner1, partnership);
        addPartnership(partner2, partnership);
    }

    @Override
    public int compareTo(final CompactPartnership other) {

        // No need to override hashCode() since this does conform to the assumption that (p1.compareTo(p2) == 0) == (p1.equals(p2)) i.e. it only returns zero for equal objects.

        final int date_difference = getMarriageDate() - other.getMarriageDate();
        return date_difference != 0 ? date_difference : hashCode() - other.hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return compareTo((CompactPartnership) o) == 0;
    }

    private static void addPartnership(final CompactPerson person, final CompactPartnership partnership) {

        if (person.getPartnerships() == null) {
            person.setPartnerships(new ArrayList<CompactPartnership>());
        }
        person.getPartnerships().add(partnership);
        Collections.sort(person.getPartnerships());
    }

    // -------------------------------------------------------------------------------------------------------

    /**
     * Gets the marriage date of this partnership.
     * @return the date.
     */
    public int getMarriageDate() {

        return marriage_date;
    }

    /**
     * Sets the marriage date of this partnership.
     * @param marriage_date the date of marriage to set
     */
    public void setMarriageDate(final int marriage_date) {

        this.marriage_date = marriage_date;
    }

    /**
     * Gets the children associated with this partnership.
     * @return the children.
     */
    public List<Integer> getChildren() {

        return children;
    }

    /**
     * Set the children to be the children of this partnership.
     * @param children to associate with the partnership.
     */
    public void setChildren(final List<Integer> children) {

        this.children = children;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked() {
        this.marked = true;
    }

    /**
     * {@inheritDoc}
     */
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
        builder.append(getChildren().size());
        builder.append("}");

        return builder.toString();
    }
}

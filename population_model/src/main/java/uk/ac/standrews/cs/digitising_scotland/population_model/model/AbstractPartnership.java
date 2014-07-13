package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import java.util.Date;
import java.util.List;

/**
 * Created by graham on 04/07/2014.
 */
public abstract class AbstractPartnership implements IPartnership {

    protected int id;
    protected int partner1_id;
    protected int partner2_id;
    protected Date marriage_date;
    protected List<Integer> children;
    protected List<Integer> partner_ids;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getPartner1Id() {
        return partner1_id;
    }

    @Override
    public int getPartner2Id() {
        return partner2_id;
    }

    @Override
    public int getPartnerOf(int id) {
        return id == partner1_id ? partner2_id : id == partner2_id ? partner1_id : -1;
    }

    @Override
    public Date getMarriageDate() {
        return marriage_date;
    }

    @Override
    public List<Integer> getChildIds() {
        return children;
    }

    @Override
    public List<Integer> getPartnerIds() {
        return partner_ids;
    }

    @Override
    public int compareTo(final IPartnership other) {
        return id - other.getId();
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof IPartnership && compareTo((IPartnership) other) == 0;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

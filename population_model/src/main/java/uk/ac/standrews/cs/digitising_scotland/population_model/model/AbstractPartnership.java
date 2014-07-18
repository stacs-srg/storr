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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by graham on 04/07/2014.
 */
public abstract class AbstractPartnership implements IPartnership {

    protected int id;
    protected int male_partner_id;
    protected int female_partner_id;
    protected Date marriage_date;
    protected List<Integer> children;
    protected List<Integer> partner_ids;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getMalePartnerId() {
        return male_partner_id;
    }

    @Override
    public int getFemalePartnerId() {
        return female_partner_id;
    }

    @Override
    public int getPartnerOf(final int id) {
        return id == male_partner_id ? female_partner_id : id == female_partner_id ? male_partner_id : -1;
    }

    @Override
    public Date getMarriageDate() {
        return (Date) marriage_date.clone();
    }

    @Override
    public List<Integer> getChildIds() {
        return children;
    }

    @Override
    public List<Integer> getPartnerIds() {

        if (partner_ids == null) {
            partner_ids = new ArrayList<>();
            partner_ids.add(male_partner_id);
            partner_ids.add(female_partner_id);
        }
        return partner_ids;
    }

    @Override
    @SuppressWarnings("CompareToUsesNonFinalVariable")
    public int compareTo(@Nonnull final IPartnership other) {

        if (id < other.getId()) {
            return -1;
        }
        if (id > other.getId()) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof IPartnership && compareTo((IPartnership) other) == 0;
    }

    @Override
    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    public int hashCode() {
        return id;
    }
}

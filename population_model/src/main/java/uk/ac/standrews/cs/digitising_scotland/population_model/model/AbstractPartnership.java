/*
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
import java.util.Date;
import java.util.List;

/**
 * Abstract partnership implementation.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public abstract class AbstractPartnership implements IPartnership {

    protected int id;
    protected int male_partner_id;
    protected int female_partner_id;
    protected Date marriage_date;
    protected String marriage_place;
    protected List<Integer> child_ids;

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
        return marriage_date != null ? (Date) marriage_date.clone() : null;
    }

    @Override
    public String getMarriagePlace() {
        return marriage_place;
    }

    @Override
    public List<Integer> getChildIds() {
        return child_ids;
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

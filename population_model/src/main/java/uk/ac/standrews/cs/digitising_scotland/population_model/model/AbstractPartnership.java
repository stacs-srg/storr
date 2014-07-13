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

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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by victor on 08/07/14.
 */
public class OrganicPartnership implements IPartnership {

    private Integer id;
    private Integer husband;
    private Integer wife;
    private OrganicTimeline timeline;
    private Date marriageDate;
    private List<Integer> childrenIds = null;

    public OrganicPartnership(final int id, final int husbandId, final int wifeId, Date marriageDate) {

        this.id = id;
        this.husband = husbandId;
        this.wife = wifeId;
        this.marriageDate = marriageDate;
        timeline = createPartnershipTimeline();
    }

    public OrganicTimeline createPartnershipTimeline() {

        OrganicTimeline timeline = new OrganicTimeline();
        // This needs a distribution creating
        timeline.addEvent(400, new OrganicEvent(EventType.BIRTH));
        // Need a divorce event as well

        return timeline;
    }

    public OrganicTimeline getTimeline() {
        return timeline;
    }

    @Override
    public int compareTo(final IPartnership arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getFemalePartnerId() {
        return wife;
    }

    @Override
    public int getMalePartnerId() {
        return husband;
    }

    @Override
    public int getPartnerOf(final int id) {

        if (id == husband)
            return wife;
        else if (id == wife)
            return husband;
        else
            return -1;
    }

    @Override
    public Date getMarriageDate() {
        return marriageDate;
    }

    @Override
    public List<Integer> getChildIds() {
        return childrenIds;
    }

    @Override
    public List<Integer> getPartnerIds() {
        return Arrays.asList(husband, wife);
    }
}

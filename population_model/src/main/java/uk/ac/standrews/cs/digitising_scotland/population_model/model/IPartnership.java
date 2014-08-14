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

import java.util.Date;
import java.util.List;

/**
 * Interface for partnership objects.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPartnership extends Comparable<IPartnership> {

    /**
     * Gets the partnership's unique identifier.
     * @return the partnership's unique identifier
     */
    int getId();

    /**
     * Gets the identifier of the female in the partnership.
     * @return the identifier of the female
     */
    int getFemalePartnerId();

    /**
     * Gets the identifier of the male in the partnership.
     * @return the identifier of the male
     */
    int getMalePartnerId();

    /**
     * Gets the identifier of the partner of the person with the given identifier, or -1 if neither member
     * of this partnership has the given identifier.
     * @param id the identifier
     * @return the identifier of the partner of the person with the given identifier
     */
    int getPartnerOf(int id);

    /**
     * Gets the date of the marriage between the partners in this partnership, or null if they are not married.
     * @return the date of the marriage of this partnership
     */
    Date getMarriageDate();

    /**
     * Gets the place of marriage, or null if not recorded.
     * @return the place of marriage
     */
    String getMarriagePlace();

    /**
     * Gets the identifiers of the partnership's child_ids, or null if none are recorded.
     * @return the identifiers of the partnership's child_ids
     */
    List<Integer> getChildIds();
}

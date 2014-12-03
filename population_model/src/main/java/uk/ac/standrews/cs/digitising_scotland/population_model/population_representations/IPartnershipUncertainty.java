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
package uk.ac.standrews.cs.digitising_scotland.population_model.population_representations;

import java.util.Date;

/**
 * Interface for partnership objects.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface IPartnershipUncertainty extends Comparable<IPartnershipUncertainty> {

    /**
     * Gets the partnership's unique identifier.
     * @return the partnership's unique identifier
     */
    int getId();

    /**
     * Returns and array of possible identifiers for the female in the partnership.
     * @return the possible identifiers of the female with the associated heuritisic for linkage
     */
    int[][] getFemalePartnerId();

    /**
     * Returns and array of possible identifiers for the male in the partnership.
     * @return the possible identifiers of the male with the associated heuritisic for linkage
     */
    int[][] getMalePartnerId();

    
    // TODO This method?
    /**
     * Gets the set of identifiers of the possible parters of the given person identifier., or -1 if neither member
     * of this partnership has the given identifier.
     * @param id the identifier
     * @return he set of possible identifiers of the partner of the person with the given identifier
     */
    int[] getPartnerOf(int id);

    
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

    
    // TODO Do we want a list of integers here or do we want to have a single child per instance. Giving the approach of 
    //      for each child a parthership object exists which has any multiple parentship options?
    /**
     * Gets the identifiers of this partnership object's child_id, or null if none are recorded.
     * @return the identifier of the partnership's child_id
     */
    int getChildId();
}

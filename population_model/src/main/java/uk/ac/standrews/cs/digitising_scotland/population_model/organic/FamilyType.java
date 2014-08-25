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
package uk.ac.standrews.cs.digitising_scotland.population_model.organic;

/**
 * Used to represent differing family types.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public enum FamilyType {
    /**
     * Indicates the individual will not partake in a cohabitation or marriage based partnership, but may be involved in an affair partnership.
     */
    SINGLE,
    /**
     * Indicates the individuals first partnership will be a cohabitation.
     */
    COHABITATION,
    /**
     * Indicates the individuals first partnership will be a cohabitation leading to marriage.
     */
    COHABITATION_THEN_MARRIAGE,
    /**
     * Indicates the individuals first partnership will be marriage.
     */
    MARRIAGE,

    AFFAIR,
    //    MALE_AFFAIR,
    //    FEMALE_AFFAIR,

    /**
     * Used as returns from affairWithMarriedOrSingleDistribution - Indicates the affair will occur with a single third party.
     */
    SINGLE_AFFAIR,
    /**
     * Used as returns from affairWithMarriedOrSingleDistribution - Indicates the affair will occur with a married third party.
     */
    INTER_MARITAL_AFFAIR,

    /**
     * Used to indicate the combinations of internal affairs queues - in this case identifies the MaleSingleAffairsQueue and the FemaleSinglesQueue.
     */
    MALE_SINGLE_AFFAIR,
    /**
     * Used to indicate the combinations of internal affairs queues - in this case identifies the MaleMaritalAffairsQueue and the FemaleMaritalAffairsQueue.
     */
    MALE_MARITAL_AFFAIR,
    /**
     * Used to indicate the combinations of internal affairs queues - in this case identifies the FemaleSingleAffairsQueue and the maleSinglesQueue.
     */
    FEMALE_SINGLE_AFFAIR,
    /**
     * Used to indicate the combinations of internal affairs queues - in this case identifies the MaleMaritalAffairsQueue and the FemaleMaritalAffairsQueue.
     */
    FEMALE_MARITAL_AFFAIR,

    /**
     * NOT CURRENTLY FULLY USED
     */
    LONE_MOTHER,
    /**
     * NOT CURRENTLY FULLY USED
     */
    LONE_FATHER
}

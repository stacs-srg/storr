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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;

/**
 * Created by victor on 11/06/14.
 */
public class OrganicPopulation {

    /**
     * Seed parameters.
     */
    CompactPopulation seedPopulation;
    public static final int SEED_SIZE = 1000;

    /**
     * The approximate average number of days per year.
     */
    public static final float DAYS_PER_YEAR = 365.25f;

    /**
     * The start year of the simulation.
     */
    public static final int START_YEAR = 1780;

    /**
     * The end year of the simulation.
     */
    public static final int END_YEAR = 2013;
    public static final int NUMBER_OF_STAGES_IN_POPULATION_GENERATION = 5;

    private static final int DAYS_IN_DECEMBER = 31;
    private static final int DECEMBER_INDEX = 11;

    private static final int AGE_AT_FIRST_MARRIAGE_STD_DEV = 3;
    private static final int AGE_AT_FIRST_MARRIAGE_MEAN = 18;
    private static final int MARRIAGE_SEPARATION_MEAN = 7;
    private static final int MARRIAGE_SEPARATION_STD_DEV = 2;
    private static final int INTER_CHILD_INTERVAL = 3;
    private static final int TIME_BEFORE_FIRST_CHILD = 1;
    private static final int MINIMUM_PERIOD_BETWEEN_PARTNERSHIPS = 7;
    private static final int PARTNERSHIP_AGE_DIFFERENCE_LIMIT = 5;
    private static final int MAX_CHILDREN = 6;
    private static final int MAX_MARRIAGES = 3;

    private static final int MINIMUM_MOTHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH = 50;
    private static final int MAX_GESTATION_IN_DAYS = 300;
    private static final int MINIMUM_FATHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_FATHER_AGE_AT_CHILDBIRTH = 70;

    private static final int[] NUMBER_OF_CHILDREN_DISTRIBUTION = new int[]{2, 3, 2, 1, 1, 1, 1};
    private static final int[] NUMBER_OF_MARRIAGES_DISTRIBUTION = new int[]{4, 20, 2, 1};

    private static final double PROBABILITY_OF_BEING_INCOMER = 0.125;
    private ProgressIndicator progress_indicator;

    @SuppressFBWarnings(value = "URF_UNREAD_FIELD")
    private CompactPerson[] people;
    @SuppressFBWarnings(value = "UUF_UNUSED_FIELD")
    private CompactPerson[] peopleToBeMarriedToday;
    @SuppressFBWarnings(value = "UUF_UNUSED_FIELD")
    private CompactPartnership[] partnershipsToHaveOffspringToday;


}

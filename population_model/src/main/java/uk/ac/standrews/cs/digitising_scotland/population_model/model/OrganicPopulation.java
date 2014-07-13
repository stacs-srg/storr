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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.AgeAtDeathDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformSexDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by victor on 11/06/14.
 */
public class OrganicPopulation implements IPopulation{

    /**
     * Seed parameters.
     */
    CompactPopulation seedPopulation;
    public static final int DEFAULT_SEED_SIZE = 1000;

    /**
     * The approximate average number of days per year.
     */
    public static final float DAYS_PER_YEAR = 365.25f;

    /**
     * The start year of the simulation.
     */
    public static final int START_YEAR = 1780;
    private int currentDay = 0;

    /**
     * The end year of the simulation.
     */
    public static final int END_YEAR = 2013;
    public int totalNumberOfDays = DateManipulation.dateToDays(END_YEAR,1,1) - DateManipulation.dateToDays(START_YEAR,1,1);

    /**
     * Additional parameters
     */
    private static final int DAYS_IN_DECEMBER = 31;
    private static final int DECEMBER_INDEX = 11;

    private static int DEFAULT_STEP_SIZE = 1 ;

    Random random = RandomFactory.getRandom();
    private Distribution<Integer> seed_age_distribution = new UniformDistribution(0,70,random);
    private Distribution<Boolean> sex_distribution = new UniformSexDistribution(random);
    private Distribution<Integer> age_at_death_distribution = new AgeAtDeathDistribution(random);


    private List<OrganicPerson> people = new ArrayList<OrganicPerson>();
    private List<OrganicPartnership> partnerships = new ArrayList<OrganicPartnership>();

    public void makeSeed(int size){

        for(int i=0; i< size; i++) {
            if (sex_distribution.getSample())
                people.add(new OrganicPerson(seed_age_distribution.getSample(), 'M'));
            else
                people.add(new OrganicPerson(seed_age_distribution.getSample(), 'F'));
        }
    }

    public void makeSeed(){
        makeSeed(DEFAULT_SEED_SIZE);
    }

    public void mainIteration(){
        mainIteration(DEFAULT_STEP_SIZE);
    }

    public void mainIteration(int timeStepSizeInDays){

    }

    /**
     *
     * Methods from interface.
     *
     */

    @Override
    public Iterable<IPerson> getPeople() {
        return null;
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {
        return null;
    }

    @Override
    public IPerson findPerson(int id) {
        return null;
    }

    @Override
    public IPartnership findPartnership(int id) {
        return null;
    }

    @Override
    public int getNumberOfPeople() {
        return 0;
    }

    @Override
    public int getNumberOfPartnerships() {
        return 0;
    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public void setConsistentAcrossIterations(boolean consistent_across_iterations) {

    }
}

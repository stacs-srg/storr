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

import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;

/**
 * Converts a population from one representation to another.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PopulationConverter implements AutoCloseable {

    private final IPopulation population;
    private final IPopulationWriter population_writer;

    private ProgressIndicator progress_indicator;

    /**
     * Initialises the population converter.
     *
     * @param population        the population to be converted
     * @param population_writer the population writer to be used to create the new representation
     */
    public PopulationConverter(final IPopulation population, final IPopulationWriter population_writer) {

        this.population = population;
        this.population_writer = population_writer;
        progress_indicator = null;
    }

    /**
     * Initialises the population converter.
     *
     * @param population         the population to be converted
     * @param population_writer  the population writer to be used to create the new representation
     * @param progress_indicator a progress indicator
     * @throws Exception if the progress indicator cannot be initialised
     */
    public PopulationConverter(final IPopulation population, final IPopulationWriter population_writer, final ProgressIndicator progress_indicator) throws Exception {

        this(population, population_writer);

        this.progress_indicator = progress_indicator;
        initialiseProgressIndicator();
    }

    /**
     * Creates a new population representation by passing each person and partnership in the population to the population writer.
     *
     * @throws Exception if a person or partnership cannot be converted
     */
    public void convert() throws Exception {

        for (final IPerson person : population.getPeople()) {

            population_writer.recordPerson(person);
            progressStep();
        }

        for (final IPartnership partnership : population.getPartnerships()) {

            population_writer.recordPartnership(partnership);
            progressStep();
        }
    }

    @Override
    public void close() throws Exception {

        population_writer.close();
    }

    private void initialiseProgressIndicator() throws Exception {

        if (progress_indicator != null) {
            progress_indicator.setTotalSteps(population.getNumberOfPeople() + population.getNumberOfPartnerships());
        }
    }

    private void progressStep() {

        if (progress_indicator != null) {
            progress_indicator.progressStep();
        }
    }
}

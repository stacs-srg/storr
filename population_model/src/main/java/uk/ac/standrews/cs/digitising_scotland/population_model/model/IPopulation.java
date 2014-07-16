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

/**
 * Interface for all population models.
 *
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPopulation {

    /**
     * Allows iteration over the people in the population.
     * The order is determined by the underlying population implementation.
     * The unique identifiers of people are allocated in temporal order.
     *
     * @return an iterable sequence of people
     */
    Iterable<IPerson> getPeople();

    /**
     * Allows iteration over the partnerships in the population.
     * The order is determined by the underlying population implementation.
     *
     * @return an iterable sequence of partnerships
     */
    Iterable<IPartnership> getPartnerships();

    /**
     * Retrieves a person by id.
     * @param id the id
     * @return the corresponding person
     * @throws Exception if there is an error retrieving the person
     */
    IPerson findPerson(int id);

    /**
     * Retrieves a partnership by id.
     * @param id the id
     * @return the corresponding partnership
     * @throws Exception if there is an error retrieving the partnership
     */
    IPartnership findPartnership(int id);

    /**
     * Returns the number of people in the population.
     * @return the number of people in the population
     * @throws Exception if there is an error determining the number of people
     */
    int getNumberOfPeople() throws Exception;

    /**
     * Returns the number of partnerships in the population.
     * @return the number of partnerships in the population
     * @throws Exception if there is an error determining the number of partnerships
     */
    int getNumberOfPartnerships() throws Exception;

    /**
     * Sets a description for the population, which may be useful for testing and debugging.
     * @param description the description
     */
    void setDescription(String description);

    /**
     * Sets a flag controlling whether person attributes are consistent across iterations.
     *
     * If set to true, person objects will be cached, which may be problematic for scalability in implementations
     * where the population is initially generated using a compact person representation that is expanded on
     * the fly during iteration.
     *
     * If set to false, attributes such as name and occupation may differ when a person with a given id
     * is retrieved multiple times.
     * @param consistent_across_iterations true if person attributes should remain consistent across iterations
     */
    void setConsistentAcrossIterations(boolean consistent_across_iterations);
}

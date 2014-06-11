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
 * Provides an interface for all population models.
 * Provides a way to iterate over people and partnerships in a given population.
 *
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPopulation {

    /**
     * Allows iteration over the people in the population.
     * The order is determined by the underlying population implementation.
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
     * Allows iteration over the people and partnerships in the population.
     * Each object returned is an instance of either {@link IPerson} or {@link IPartnership}.
     * The order is determined by the underlying population implementation.
     * Where it is required to retrieve all the people and partnerships,
     * this method may be considerably more efficient than the others.
     *
     * @return an iterable sequence of people and partnerships
     */
    Iterable<Object> getPopulation();

    /**
     * Retrieves a person by id.
     * @param id the id
     * @return the corresponding person
     */
    IPerson findPerson(int id);

    /**
     * Retrieves a partnership by id.
     * @param id the id
     * @return the corresponding person
     */
    IPartnership findPartnership(int id);

    /**
     * Returns the number of people in the population.
     * @return the number of people in the population
     */
    int size();
}

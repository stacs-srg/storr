package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import java.util.Iterator;

/**
 * Provides an interface for all population models.
 * Provides a way to iterate over people and partnerships in a given population.
 *
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPopulation {

    Iterator<IPerson> peopleIterator();
    Iterator<IPartnership> partnershipIterator();
}

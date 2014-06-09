package uk.ac.standrews.cs.digitising_scotland.population_model.transform;

import java.util.Iterator;

/**
 * Provides an interface for all population models.
 * Provides a way to iterate over people and partnerships in a given population.
 *
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 */
public interface IPopulation{

    public Iterator peopleIterator();
    public Iterator partnershipIterator();
}

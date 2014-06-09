package uk.ac.standrews.cs.digitising_scotland.population_model.model;

/**
 * Provides an interface for all population models.
 * Provides a way to iterate over people and partnerships in a given population, and to retrieve a person or
 * partnership with a specific id.
 *
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public interface IPopulationWithRandomAccess extends IPopulation {

    IPerson getPersonById(int id);
    IPartnership getPartnershipById(int id);

    int size();
}

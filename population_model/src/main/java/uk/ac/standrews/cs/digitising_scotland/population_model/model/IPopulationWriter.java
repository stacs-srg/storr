package uk.ac.standrews.cs.digitising_scotland.population_model.model;

/**
 * Created by graham on 11/06/2014.
 */
public interface IPopulationWriter extends AutoCloseable {

    void recordIndividual(IPerson member) throws Exception;

    void recordPartnership(IPartnership member) throws Exception;
}

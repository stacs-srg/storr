package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

/**
 * Created by al on 04/07/2014.
 */
public interface IStoreIndex {

    /**
     *
     * @param id - a record to be found in the store
     * @return the bucket containing the specified id
     */
    public IBucket get(int id);

}

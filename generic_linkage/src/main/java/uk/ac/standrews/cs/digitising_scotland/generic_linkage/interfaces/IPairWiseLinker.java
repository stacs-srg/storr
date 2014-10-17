package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

/**
 * Created by al on 21/05/2014.
 */
public interface IPairWiseLinker<T extends ILXP> {

    void pairwiseLink();

    boolean compare(IPair<T> pair);

    /**
     * Adds a matched result to a result collection.
     * @param pair
     */
    void addToResults(final IPair<T> pair, final IOutputStream<IPair<T>> results);
}

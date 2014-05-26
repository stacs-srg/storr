package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;

/**
 * Created by al on 21/05/2014.
 */
public interface IPairWiseLinker {

    public void pairwise_link();

    public boolean compare(Pair pair);

    /**
     * Adds a matched result to a result collection
     * @param pair
     */
    public void add_to_results(Pair pair, ILXPOutputStream results);
}

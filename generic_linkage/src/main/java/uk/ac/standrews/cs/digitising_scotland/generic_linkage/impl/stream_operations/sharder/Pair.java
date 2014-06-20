package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operations.sharder;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IPair;

/**
 * Created by al on 19/06/2014.
 */
public class Pair implements IPair {
    private ILXP first;
    private ILXP second;

    public Pair( ILXP first, ILXP second ) {
        this.first = first;
        this.second = second;
    }

    public ILXP first() {
        return first;
    }

    public ILXP second() {
        return second;
    }
}

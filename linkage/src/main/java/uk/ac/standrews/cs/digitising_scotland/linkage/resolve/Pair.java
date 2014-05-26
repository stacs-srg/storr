package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;

/**
 * Created by al on 12/05/2014.
 */
public class Pair {
    private final ILXP first;
    private final ILXP second;

    public Pair(ILXP first, ILXP second) {
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

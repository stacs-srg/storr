package uk.ac.standrews.cs.digitising_scotland.linkage.interfaces;


import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;

/**
 * Created by al on 19/06/2014.
 */
public interface IPair<T extends ILXP> extends ILXP {
    T first();

    T second();

}

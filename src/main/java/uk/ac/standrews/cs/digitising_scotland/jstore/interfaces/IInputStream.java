package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;


/**
 * Provides an input stream of labelled cross product records.
 * Does not implement any functionality other than that provided by Iterable.
 * Provided for competeness to match @class IOutputStream
 *
 * @author al
 */
public interface IInputStream<T extends ILXP> extends Iterable<T> {

}

package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

/**
 * Class used to represent a bucket implementation kind.
 * Each kind of bucket has a subtly different semantics/functionality
 * Created by al on 01/08/2014.
 */
public enum BucketKind {
    UNKNOWN, // an error case
    DIRECTORYBACKED,
    INDIRECT,
    INDEXED
}

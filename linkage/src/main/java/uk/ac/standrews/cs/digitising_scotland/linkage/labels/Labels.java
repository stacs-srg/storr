package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import interfaces.ILabels;

/**
 * Created by al on 30/05/2014.
 */
public abstract class Labels implements ILabels {

    public abstract Iterable<String> get_field_names();
}

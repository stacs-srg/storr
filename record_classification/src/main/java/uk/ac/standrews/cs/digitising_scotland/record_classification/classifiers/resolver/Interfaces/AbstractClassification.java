package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces;

import java.io.Serializable;

/**
 *
 * Created by fraserdunlop on 08/10/2014 at 17:36.
 */
public abstract class AbstractClassification<Code, Threshold> implements HasProperty<Code>, Comparable<Threshold>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5527932079726079681L;

}

package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;

/**
 * Created by al on 23/03/15.
 */
public interface IStoreReference<T extends ILXP> {

    String getBucketName();

    String getRepositoryName();

    Long getOid();

    T getReferend() throws BucketException;
}

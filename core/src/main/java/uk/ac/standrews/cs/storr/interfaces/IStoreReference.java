package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;

/**
 * Created by al on 23/03/15.
 */
public interface IStoreReference<T extends ILXP> {

    public String getBucketName();
    public String getRepoName();
    public Long getOid();
    public T getReferend() throws BucketException;
}

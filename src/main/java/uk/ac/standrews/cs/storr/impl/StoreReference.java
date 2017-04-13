/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.ReferenceException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.lang.ref.WeakReference;

/**
 * Created by al on 23/03/15.
 */
public class StoreReference<T extends ILXP> extends LXP implements IStoreReference {

    protected final static String $INDIRECTION$ = "$INDIRECTION$";
    protected final static String REPOSITORY = "repository";
    protected final static String BUCKET = "bucket";
    protected final static String OID = "oid";

    private static final String SEPARATOR = "/";

    private WeakReference<T> ref = null;
    private IStore store;

    /**
     * @param serialized - a String of form repo_name SEPARATOR bucket_name SEPARATOR oid
     */
    public StoreReference(IStore store, String serialized) throws ReferenceException {

        this.store = store;

        try {
            String[] tokens = serialized.split(SEPARATOR);
            put($INDIRECTION$, "true");
            put(REPOSITORY, tokens[0]);
            put(BUCKET, tokens[1]);
            put(OID, Long.parseLong(tokens[2]));
            // don't bother looking up cache reference on demand
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new ReferenceException(e.getMessage());
        }
    }

    public StoreReference(IStore store, String repo_name, String bucket_name, long oid) {

        super();
        this.store = store;
        this.put($INDIRECTION$, "true");
        this.put(REPOSITORY, repo_name);
        this.put(BUCKET, bucket_name);
        this.put(OID, oid);
        // don't bother looking up cache reference on demand or by caller
    }

    public StoreReference(IRepository repo, IBucket bucket, T reference) {
        this(repo.getStore(), repo.getName(), bucket.getName(), reference);
    }

    private StoreReference(IStore store, String repo_name, String bucket_name, T reference) {
        this(store, repo_name, bucket_name, reference.getId());
        ref = new WeakReference<T>(reference);
    }

    public StoreReference(IStore store, ILXP record) {
        this(store, record.getString(REPOSITORY), record.getString(BUCKET), record.getLong(OID));
        // don't bother looking up cache reference on demand
    }

    @Override
    public String getRepositoryName() {
        return getString(REPOSITORY);
    }

    @Override
    public String getBucketName() {
        return getString(BUCKET);
    }

    @Override
    public Long getOid() {
        return getLong(OID);
    }

    @Override
    public T getReferend() throws BucketException {

        // First see if we have a cached reference.
        if (ref != null) {
            T result = ref.get();
            if (result != null) {
                return result;
            }
        }
        try {
            return (T) store.getRepository(getRepositoryName()).getBucket(getBucketName()).getObjectById(getOid());
        } catch (RepositoryException | StoreException e) {
            throw new BucketException(e);
        }
    }

    public String toString() {
        return getRepositoryName() + SEPARATOR + getBucketName() + SEPARATOR + getOid();
    }
}

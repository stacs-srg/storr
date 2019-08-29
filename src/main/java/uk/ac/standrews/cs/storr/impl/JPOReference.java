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


import uk.ac.standrews.cs.storr.impl.exceptions.ReferenceException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;

import java.lang.ref.WeakReference;

public class JPOReference<T extends JPO> implements IStoreReference<T> {

    private WeakReference<T> ref = null;
    private IStore store;
    private String repository;
    private String bucket;
    private long oid;

    private static final String SEPARATOR = "/";

    public JPOReference(IStore store, String serialized) throws ReferenceException {

        this.store = store;

        try {
            String[] tokens = serialized.split(SEPARATOR);
            repository =  tokens[0];
            bucket =  tokens[1];
            oid = Long.parseLong(tokens[2]);
            // don't bother looking up cache reference on demand
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new ReferenceException(e);
        }
    }

    public JPOReference(IStore store, String repo_name, String bucket_name, long oid) {

        super();
        this.store = store;
        this.repository = repo_name;
        this.bucket = bucket_name;
        this.oid = oid;
        // don't bother looking up cache reference on demand or by caller
    }

    private JPOReference(IStore store, String repo_name, String bucket_name, T reference) {
        this(store, repo_name, bucket_name, reference.getId());
        ref = new WeakReference<T>(reference);   // TODO was weakRef - make softRef??
    }

    public JPOReference(IRepository repo, IBucket bucket, T reference) {
        this(repo.getStore(), repo.getName(), bucket.getName(), reference);
    }

    public String getRepositoryName() {
        return this.repository;
    }

    public String getBucketName() {
        return this.bucket;
    }

    public Long getOid() {
        return this.oid;
    }

    public T getReferend() {
       return ref.get();
    }

    public String toString() {
        return getRepositoryName() + SEPARATOR + getBucketName() + SEPARATOR + getOid();
    }

}

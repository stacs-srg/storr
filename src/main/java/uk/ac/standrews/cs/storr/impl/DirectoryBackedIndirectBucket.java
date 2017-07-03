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
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;

public class DirectoryBackedIndirectBucket<T extends ILXP> extends DirectoryBackedBucket<T> {

    DirectoryBackedIndirectBucket(IRepository repository, String bucket_name, boolean create_bucket) throws RepositoryException {
        super(repository, bucket_name, BucketKind.INDIRECT, create_bucket);
    }

    DirectoryBackedIndirectBucket(IRepository repository, String bucket_name, ILXPFactory<T> tFactory, boolean create_bucket) throws RepositoryException {
        super(repository, bucket_name, BucketKind.INDIRECT, tFactory, create_bucket);
    }

    @Override
    // Writes an indirection record into the file system
    public void makePersistent(final T record) throws BucketException {

        try {
            writeLXP((ILXP) record.getThisRef(), filePath(record.getId()));
        } catch (IllegalKeyException | PersistentObjectException e) {
            throw new BucketException("Error creating indirection");
        }
    }

    @Override
    public IInputStream<T> getInputStream() throws BucketException {
        try {
            return new BucketBackedInputStream<>(this);

        } catch (IOException e) {
            throw new BucketException("I/O exception getting stream");
        }
    }

    @Override
    public IOutputStream<T> getOutputStream() {
        return new BucketBackedOutputStream<>(this);
    }

    @Override
    public BucketKind getKind() {
        return BucketKind.INDIRECT;
    }
}

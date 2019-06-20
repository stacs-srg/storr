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
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.storr.types.LXPReferenceType;
import uk.ac.standrews.cs.storr.types.Types;

import java.util.HashMap;

/**
 * Created by al on 12/09/2014.
 */
public class TypeFactory {

    private static final String type_repo_name = "Types_repository";
    private static final String type_Rep_bucket_name = "Type_reps";
    private static final String type_names_bucket_name = "Type_names";

    private IBucket type_reps_bucket;
    private IBucket type_name_bucket;
    private IRepository type_repository;
    private IStore store;

    private HashMap<String, IReferenceType> names_to_type_cache = new HashMap<>();
    private HashMap<Long, IReferenceType> ids_to_type_cache = new HashMap<>();

    protected TypeFactory(final IStore store) throws RepositoryException {

        this.store = store;

        setupTypeRepository(type_repo_name);
        final boolean type_repo_initialised_already = type_repository.bucketExists(type_Rep_bucket_name);
        type_reps_bucket = getBucket(type_Rep_bucket_name);
        type_name_bucket = getBucket(type_names_bucket_name);
        loadCaches();

        if (!type_repo_initialised_already) {
            // initialise predefined types - only 1 for now
            createAnyType();
        }
    }

    private void createAnyType() {
        final DynamicLXP typerep = Types.getTypeRep(StaticLXP.class);
        final LXPReferenceType lxp_type = new LXPReferenceType(typerep);
        doHousekeeping("lxp", lxp_type);
    }

    public IReferenceType createType(final String json_encoded_type_descriptor_file_name, final String type_name) {
        final LXPReferenceType ref_type = new LXPReferenceType(json_encoded_type_descriptor_file_name, type_repository, type_reps_bucket);
        doHousekeeping(type_name, ref_type);
        return ref_type;
    }

    public IReferenceType createType(final Class c, final String type_name) {
        final DynamicLXP typerep = Types.getTypeRep(c);
        final LXPReferenceType ref_type = new LXPReferenceType(typerep);
        doHousekeeping(type_name, ref_type);
        return ref_type;
    }

    public IReferenceType getTypeWithName(final String name) {
        return names_to_type_cache.get(name);
    }

    public boolean containsKey(final String name) {
        return names_to_type_cache.containsKey(name);
    }

    public IReferenceType typeWithId(final long id) {
        return ids_to_type_cache.get(id);
    }

    public boolean containsId(final long id) {
        return ids_to_type_cache.containsKey(id);
    }

    //****************** private methods ******************//

    private void loadCaches() {

        try {
            for (final DynamicLXP lxp : (Iterable<DynamicLXP>) type_name_bucket.getInputStream()) {
                // as set up in @code nameValuePair below.
                final String name = (String) lxp.get("name");
                final long type_key = (long) lxp.get("key");

                final LXP type_rep = type_reps_bucket.getObjectById(type_key);
                final LXPReferenceType reference = new LXPReferenceType((DynamicLXP) (type_rep));

                names_to_type_cache.put(name, reference);
                ids_to_type_cache.put(type_key, reference);
            }
        } catch (final BucketException e) {
            throw new RuntimeException("IO exception getting iterator over type name field_storage", e);
        } catch (final KeyNotFoundException e) {
            throw new RuntimeException("Could not find key whilst re-establishing caches", e);
        } catch (final TypeMismatchFoundException e) {
            throw new RuntimeException("Type mismatch", e);
        }
    }

    private void doHousekeeping(final String type_name, final LXPReferenceType ref_type) {

        try {
            final LXP type_rep = ref_type.getRep();
            final LXP name_value = nameValuePair(type_name, type_rep.getId());
            type_reps_bucket.makePersistent(type_rep);
            type_name_bucket.makePersistent(name_value);

        } catch (final BucketException e) {
            throw new RuntimeException("Bucket exception adding type " + type_name + " to types bucket", e);
        }

        names_to_type_cache.put(type_name, ref_type);
        ids_to_type_cache.put(ref_type.getId(), ref_type);
    }

    private LXP nameValuePair(final String type_name, final long typekey) {

        final DynamicLXP lxp = new DynamicLXP();

        lxp.put("name", type_name);
        lxp.put("key", typekey);

        return lxp;
    }

    private void setupTypeRepository(final String type_repo_name) throws RepositoryException {

        if (store.repositoryExists(type_repo_name)) {
            type_repository = store.getRepository(type_repo_name);
        } else {
            type_repository = store.makeRepository(type_repo_name);
        }
    }

    private IBucket getBucket(final String bucket_name) throws RepositoryException {

        if (type_repository.bucketExists(bucket_name)) {
            return type_repository.getBucket(bucket_name);
        } else {
            return type_repository.makeBucket(bucket_name, BucketKind.DIRECTORYBACKED);
        }
    }
}

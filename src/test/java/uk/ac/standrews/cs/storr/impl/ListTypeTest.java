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

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.FileManipulation;
import uk.ac.standrews.cs.utilities.JSONReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListTypeTest extends CommonTest {

    private static final String lxpBucketName = "lxpBucket";
    private static final String classWithListOfScalarsBucketName = "classWithListOFScalarsBucket";
    private static final String classWithListOfRefsBucketName = "classWithListOFRefsBucket";

    private IBucket lxp_bucket;
    private IBucket<ClassWithListOfScalars> scalar_list_bucket;
    private IBucket<ClassWithListOfRefs> ref_list_bucket;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        super.setUp();

        lxp_bucket = repository.makeBucket(lxpBucketName, BucketKind.DIRECTORYBACKED);
        scalar_list_bucket = repository.makeBucket(classWithListOfScalarsBucketName, BucketKind.DIRECTORYBACKED);
        ref_list_bucket = repository.makeBucket(classWithListOfRefsBucketName, BucketKind.DIRECTORYBACKED);
    }

    @Test
    public void checkStructuralEquivalenceWithListOfScalars() throws RepositoryException, BucketException, PersistentObjectException, IOException {

        final List<Integer> list = Arrays.asList(99, 88);

        final ClassWithListOfScalars example = new ClassWithListOfScalars(53, list);
        scalar_list_bucket.makePersistent(example);
        final long id = example.getId();

        // Now try and read back - avoid all cache etc.

        final Path file_path = store_path.resolve("REPOS").resolve(REPOSITORY_NAME).resolve(classWithListOfScalarsBucketName).resolve(Long.toString(id));
        try (final BufferedReader reader = Files.newBufferedReader(file_path, FileManipulation.FILE_CHARSET)) {

            final DynamicLXP lxp2 = new DynamicLXP(id, new JSONReader(reader), lxp_bucket);

            assertEquals(id, lxp2.getId());
            assertEquals(53, (int) lxp2.get("AN_INT"));

            final List l = (List) lxp2.get("A_LIST");
            assertEquals(99, (int) l.get(0));
            assertEquals(88, (int) l.get(1));
        }
    }

    @Test
    public void checkStructuralEquivalenceWithListOfRefs() throws RepositoryException, BucketException, PersistentObjectException, IOException {

        final DynamicLXP lxp1 = new DynamicLXP();
        lxp1.put("a", 99);
        lxp_bucket.makePersistent(lxp1);
        final IStoreReference ref1 = lxp1.getThisRef();

        final DynamicLXP lxp2 = new DynamicLXP();
        lxp2.put("b", 88);
        lxp_bucket.makePersistent(lxp2);
        final IStoreReference ref2 = lxp2.getThisRef(); // <<<<<< TODO problem here with JSON EXCEPTION - if not persistent

        final List<DynamicLXP> list = new ArrayList<>();
        list.add(lxp1);
        list.add(lxp2);

        final ClassWithListOfRefs example = new ClassWithListOfRefs(53, list);
        ref_list_bucket.makePersistent(example);
        final long id = example.getId();

        // Now try and read back - avoid all cache etc.

        final Path file_path = store_path.resolve("REPOS").resolve(REPOSITORY_NAME).resolve(classWithListOfRefsBucketName).resolve(Long.toString(id));
        try (final BufferedReader reader = Files.newBufferedReader(file_path, FileManipulation.FILE_CHARSET)) {

            final DynamicLXP lxp3 = new DynamicLXP(id, new JSONReader(reader), lxp_bucket);
            assertEquals(id, lxp3.getId());
            assertEquals(53, (int) lxp3.get("AN_INT"));
            final List l = (List) lxp3.get("A_LIST");

            assertEquals(ref1, (new StoreReference(Store.getInstance(), (String) l.get(0))));
            assertEquals(ref2, (new StoreReference(Store.getInstance(), (String) l.get(1))));
        }
    }
}

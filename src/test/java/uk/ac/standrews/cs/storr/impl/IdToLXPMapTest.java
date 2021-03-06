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
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Al  al@st-andrews.ac.uk on 12/05/2017.
 *
 * @author al@st-andrews.ac.uk 12/05/2017
 */
public class IdToLXPMapTest extends CommonTest {

    private static final String BUCKET_NAME = "$$$bucket$$$bucket$$$";  // Test $$$bucket$$$bucket$$$ name
    private IBucket<SimpleLXP> bucket;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        super.setUp();

        final IRepository testrepo = store.makeRepository("testrepo");
        new IdtoILXPMap<>("testmap", testrepo, SimpleLXP.class, true);
        bucket = testrepo.makeBucket(BUCKET_NAME, BucketKind.DIRECTORYBACKED);
    }

    @Ignore
    @Test
    public void checkRetrievalById() throws RepositoryException, BucketException, PersistentObjectException, IOException {

        final IRepository testrepo = store.getRepository("testrepo");

        final IdtoILXPMap<SimpleLXP> pmap = new IdtoILXPMap<>("testmap", testrepo, SimpleLXP.class, false);
        final Map<Long, SimpleLXP> tempmap = new HashMap<>();

        // create some data and put into persistent and transient field_storage
        for (int i = 1; i < 10; i++) {
            final SimpleLXP lxp = new SimpleLXP();
            lxp.put("FIELD", i);
            bucket.makePersistent(lxp);

            pmap.put(lxp.getId(), lxp.getThisRef());
            tempmap.put(lxp.getId(), lxp);
        }

        // now look it up.

        final Collection<Long> tempkeys = tempmap.keySet();

        for (final Long key : tempkeys) {
            final LXP value = (LXP) pmap.lookup(key);
            assertEquals(value, tempmap.get(key));
        }
    }
}

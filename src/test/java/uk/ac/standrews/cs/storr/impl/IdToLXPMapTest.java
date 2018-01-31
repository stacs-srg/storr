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
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Al  al@st-andrews.ac.uk on 12/05/2017.
 * @author al@st-andrews.ac.uk 12/05/2017
 */


public class IdToLXPMapTest extends CommonTest {

    private static final String bucketName = "bucket";  // Test bucket name
    private IBucket<SimpleLXP> bucket;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        super.setUp();

        IRepository testrepo = store.makeRepository("testrepo");
        new IdtoILXPMap( "testmap", testrepo, SimpleLXP.class, true );
        bucket = testrepo.makeBucket( bucketName, BucketKind.DIRECTORYBACKED );
    }

    @Test
    public void checkXXX() throws RepositoryException, BucketException, PersistentObjectException, IOException {

        IRepository testrepo = store.getRepository("testrepo");
        IdtoILXPMap pmap = new IdtoILXPMap<SimpleLXP>( "testmap", testrepo, SimpleLXP.class, false );

        HashMap<Long, SimpleLXP> tempmap = new HashMap<Long, SimpleLXP>();

        // create some data and put into persistent and transient field_storage
        for (int i = 1; i < 10; i++) {
            SimpleLXP lxp = new SimpleLXP();
            lxp.put("FIELD", i);
            bucket.makePersistent(lxp);

            pmap.put( lxp.getId(), lxp.getThisRef() );
            tempmap.put( lxp.getId(), lxp );
        }

        // now look it up.

        Collection<Long> tempkeys = tempmap.keySet();

        for( Long key : tempkeys ) {
            LXP value = pmap.lookup(key);
            assertTrue( value.equals( tempmap.get(key)));
        }

    }
}

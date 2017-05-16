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
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
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


public class StringToLXPMapTest extends CommonTest {

    private static final String bucketName = "bucket";

    private IBucket<LXP> bucket;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        super.setUp();

        TypeFactory tf = store.getTypeFactory();
        IRepository testrepo = store.makeRepository("testrepo");
        PersistentIdtoILXPMap.createMap( "testmap", testrepo );
        bucket = testrepo.makeBucket( bucketName, BucketKind.DIRECTORYBACKED );
    }

    @Test
    public void checkXXX() throws RepositoryException, BucketException, PersistentObjectException, IOException {

        IRepository testrepo = store.getRepository("testrepo");
        PersistentStringtoILXPMap pmap = new PersistentStringtoILXPMap<LXP>( testrepo,"testmap",false );

        HashMap<String, LXP> tempmap = new HashMap();

        // create some data and put into persistent and transient map
        for (int i = 1; i < 10; i++) {
            String key = Integer.toString(i);
            LXP lxp = new LXP();
            lxp.put("fieldname",key );
            bucket.makePersistent(lxp);

            pmap.put( key, lxp.getThisRef() );
            tempmap.put( key, lxp );
        }

        // now look it up.

        Collection<String> tempkeys = tempmap.keySet();

        for( String key : tempkeys ) {
            try {
                ILXP value = pmap.lookup(key);
                assertTrue(value.equals(tempmap.get(key)));
                System.out.println("value class: " + value.getClass().toString());
            } catch( TypeMismatchFoundException e ) {
                System.out.println( "Type Exception: " + e.getMessage() );
                throw e;
            }
        }

    }
}

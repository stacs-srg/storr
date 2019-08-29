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

import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;
import uk.ac.standrews.cs.utilities.JSONReader;

/**
 * Created by al on 8/12/2017.
 */
public class SimpleLXP extends StaticLXP {

    private static LXPMetadata static_md;

    static {
        try {
            static_md = new LXPMetadata( SimpleLXP.class,"SimpleLXP" );

        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    @LXP_SCALAR(type = LXPBaseType.INT)
    public static int FIELD;


    public SimpleLXP() {
        put( FIELD, 1);
    }

    public SimpleLXP(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException {
        super( persistent_object_id,reader,bucket );
    }

    @Override
    public LXPMetadata getMetaData() {
        return static_md;
    }
}
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

import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_LIST;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;
import uk.ac.standrews.cs.utilities.JSONReader;

import java.util.List;

/**
 * Created by al on 22/11/2016.
 */
public class ClassWithListOfScalars extends LXP {

    @LXP_SCALAR(type = LXPBaseType.INT)
    public static final String some_int = "S_INT";

    @LXP_LIST(basetype = LXPBaseType.INT)
    public static final String alist = "S_LIST";

    public ClassWithListOfScalars(long persistent_Object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        super(persistent_Object_id, reader, repository, bucket);
    }

    public ClassWithListOfScalars(int an_int, List<Integer> list) {
        this.put(some_int, an_int);
        this.put(alist, list);
    }
}

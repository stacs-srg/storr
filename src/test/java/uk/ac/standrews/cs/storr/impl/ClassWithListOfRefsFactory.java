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
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.types.Types;
import uk.ac.standrews.cs.utilities.JSONReader;

import java.util.List;

/**
 * Created by al on 03/10/2014.
 */
public class ClassWithListOfRefsFactory extends TFactory<ClassWithListOfRefs> implements ILXPFactory<ClassWithListOfRefs> {


    public ClassWithListOfRefsFactory(long required_label_id) {
        this.required_type_labelID = required_label_id;
    }

    @Override
    public ClassWithListOfRefs create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        return new ClassWithListOfRefs(persistent_object_id, reader, repository, bucket);
    }

    public ClassWithListOfRefs create(int id, List<LXP> list) {
        ClassWithListOfRefs result = new ClassWithListOfRefs(id, list);
        result.put(Types.LABEL, required_type_labelID);
        return result;
    }

}

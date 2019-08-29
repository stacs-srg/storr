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
package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;


/**
 * Created by al on 20/06/2014.
 */
public interface IReferenceType extends IType {

    /**
     * @return the labels present in the type.
     * For example for a type [name: string, age: int] would return {name,age}
     */
    java.util.Collection<String> getLabels();

    /**
     * @param label - the label whose type is being looked up
     * @return the field type associated with the specified label
     * e.g. for a type [name: string, age: int] and the label "name" this method would return the
     * rep for @class LXPBaseType(INT).
     * @throws KeyNotFoundException if the key is not found
     */
    IType getFieldType(String label) throws KeyNotFoundException, TypeMismatchFoundException;

    /**
     * @return the $$$$id$$$$id$$$$ of this typerep - this is the $$$$id$$$$id$$$$ of the underlying rep implementation.
     */
    long getId();

    /**
     * @return the OID used to encode the reference type - e.g. [name: string, age: int]
     */
    LXP getRep();
}

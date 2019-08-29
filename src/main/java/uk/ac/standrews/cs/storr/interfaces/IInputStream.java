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


import uk.ac.standrews.cs.storr.impl.PersistentObject;

/**
 * Provides an input stream of labelled cross product records.
 * Does not implement any functionality other than that provided by Iterable.
 * Provided for competeness to match @class IOutputStream
 *
 * @author al
 */
public interface IInputStream<T extends PersistentObject> extends Iterable<T> {

}

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
package uk.ac.standrews.cs.storr.impl.transaction.interfaces;

import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.impl.transaction.exceptions.TransactionFailedException;

/**
 * Modelled on com.google.appengine.api.datastore API
 * <p>
 * Created by al on 05/01/15.
 */
public interface ITransaction {

    void commit() throws TransactionFailedException, StoreException;

    void rollback() throws IllegalStateException;

    boolean isActive();

    String getId();

}

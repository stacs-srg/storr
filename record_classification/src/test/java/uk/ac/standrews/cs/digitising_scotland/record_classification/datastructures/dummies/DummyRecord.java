/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.dummies;

import java.util.ArrayList;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * Dummy record - fields set null and stubs with no implementation for all methods.
 * Created by fraserdunlop on 19/06/2014 at 10:48.
 */

public class DummyRecord extends Record {

    public DummyRecord() {

        super((int) Math.rint(Math.random() * 1000000), null);
    }

    @Override
    public OriginalData getOriginalData() {

        return null;
    }

    @Override
    public ArrayList<String> getDescription() {

        return null;
    }

    @Override
    public Set<Classification> getGoldStandardClassificationSet() {

        return null;
    }

    @Override
    public boolean isCoDRecord() {

        return true;
    }

    @Override
    public String toString() {

        return "";
    }

    @Override
    public Set<Classification> getClassifications() {

        return null;
    }

}

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
package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Classification comparator. Compares classifications
 * Created by fraserdunlop on 06/10/2014 at 14:15.
 */
public class ClassificationComparator implements Comparator<Classification>, Serializable {

    private static final long serialVersionUID = -2746182512036694544L;

    @Override
    public int compare(final Classification o1, final Classification o2) {

        double measure1 = o1.getTokenSet().size() * Math.abs(o1.getConfidence());
        double measure2 = o2.getTokenSet().size() * Math.abs(o2.getConfidence());
        if (measure1 < measure2) {
            return 1;
        }
        else if (measure1 > measure2) {
            return -1;
        }
        else {
            return 0;
        }
    }
}

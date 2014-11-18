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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver;


import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;

/**
 * Loss function that sums the confidences but weights each confidence by the length of the tokenset.
 * 
 * @author jkc25
 *
 */
public class LengthWeightedLossFunction implements LossFunction<Multiset<Classification>, Double> {

    @Override
    public Double calculate(final Multiset<Classification> set) {

        double confidenceSum = 0;

        for (Classification triple : set) {
            confidenceSum += Math.abs(triple.getConfidence()) * triple.getTokenSet().size();
        }

        return confidenceSum;
    }

}

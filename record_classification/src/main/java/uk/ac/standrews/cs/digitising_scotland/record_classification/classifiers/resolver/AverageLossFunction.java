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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;

/**
 * Calculates the loss for a set of CodeTriples.
 * The loss function is defined as the average confidence of all the {@link uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification} in the set.
 * @author jkc25
 *
 */
public class AverageLossFunction implements LossFunction<Multiset<Classification>, Double> {

    @Override
    public Double calculate(final Multiset<Classification> set) {

        List<Double> confidences = new ArrayList<>();
        for (Classification triple : set) {
            confidences.add(Math.abs(triple.getConfidence()));
        }
        Double confidenceSum = 0.;
        for (Double conf : confidences) {
            confidenceSum += conf;
        }

        return confidenceSum / (double) confidences.size();
    }

}

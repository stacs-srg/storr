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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/**
 * Adapter for Converting Simmetrics string similarity metrics to conform
 * to our SimilarityMetric interface for use in ClosestMatchMaps
 * Created by fraserdunlop on 02/10/2014 at 16:50.
 */
public class SimilarityMetricFromSimmetricFactory {

    public SimilarityMetric<String> create(final AbstractStringMetric stringMetric) {

        return new StringMetric(stringMetric);
    }

    private class StringMetric implements SimilarityMetric<String> {

        private final AbstractStringMetric stringMetric;

        @Override
        public double getSimilarity(final String o1, final String o2) {

            Float sim = stringMetric.getSimilarity(o1, o2);
            if (sim.isNaN()) {
                System.out.println("NaN returned by similarity metric :- o1: " + o1 + " o2: " + o2);
                return 0;
            }
            return sim;
        }

        public StringMetric(final AbstractStringMetric stringMetric) {

            this.stringMetric = stringMetric;
        }

    }

}

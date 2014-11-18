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
package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * This is the 'classic' true confusion matrix. Predictions are only correct if they
 * exactly match the gold standard code.
 * Created by fraserdunlop on 02/07/2014 at 10:50.
 */
public class StrictConfusionMatrix extends AbstractConfusionMatrix {

    /**
     * Instantiates a new strict confusion matrix.
     *
     * @param bucket the bucket
     */
    public StrictConfusionMatrix(final Bucket bucket, final CodeIndexer index) {

        super(bucket, index);
    }

    /**
     * True pos and false neg.
     *
     * @param predictedClassifications the set code triples
     * @param goldStandardClassifications the gold standard triples
     */
    protected void truePosAndFalseNeg(final Set<Classification> predictedClassifications, final Set<Classification> goldStandardClassifications) {

        for (Classification goldStandardCode : goldStandardClassifications) {
            final Code code = goldStandardCode.getCode();
            int codeID = index.getID(code);
            if (contains(code, predictedClassifications)) {
                truePositive[codeID]++;
            }
            else {
                falseNegative[codeID]++;
            }
        }

    }

    /**
     * Total and false pos.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void totalAndFalsePos(final Set<Classification> setCodeTriples, final Set<Classification> goldStandardTriples) {

        for (Classification predictedCode : setCodeTriples) {
            final Code code = predictedCode.getCode();
            int codeID = index.getID(code);
            totalPredictions[codeID]++;
            if (!contains(code, goldStandardTriples)) {
                falsePositive[codeID]++;
            }
        }
    }

    /**
     * Returns true is a code is in the specified set of CodeTriples.
     * @param code code to check for
     * @param setCodeTriples set to check in
     * @return true if present
     */
    public boolean contains(final Code code, final Set<Classification> setCodeTriples) {

        return Utils.contains(code, setCodeTriples);
    }

}

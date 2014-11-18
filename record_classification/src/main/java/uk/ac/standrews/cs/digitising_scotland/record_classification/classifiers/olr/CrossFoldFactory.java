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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.mahout.math.NamedVector;

/**
 * Abstract class for the creation of cross folded data structures.
 * @author frjd2
 */
public abstract class CrossFoldFactory {

    /**
     * Creates a cross folded data structure.
     * @param trainingVectorList training vector list
     * @param folds number of folds
     * @return ArrayList<NamedVector>[][]
     */
    public static ArrayList<NamedVector>[][] make(final List<NamedVector> trainingVectorList, final int folds) {

        ArrayList<NamedVector>[][] crossFoldedData = initialize(folds);
        int size = trainingVectorList.size();
        int[] splitPoints = calculateSplitPoints(folds, size);

        for (int i = 0; i < folds + 1; i++) {
            for (int j = 0; j < size; j++) {
                if (j >= splitPoints[i] && j <= splitPoints[i + 1]) {
                    crossFoldedData[i][1].add(new NamedVector(trainingVectorList.get(j).clone()));
                }
                else {
                    crossFoldedData[i][0].add(new NamedVector(trainingVectorList.get(j).clone()));
                }
            }
        }
        return crossFoldedData;
    }

    protected static ArrayList<NamedVector>[][] initialize(final int folds) {

        if (folds < 1) { throw new NumberIsTooSmallException(folds, 1, true); }

        ArrayList<NamedVector>[][] crossFoldedData = new ArrayList[folds + 1][2];

        for (int i = 0; i < folds + 1; i++) {
            for (int j = 0; j < 2; j++) {
                crossFoldedData[i][j] = new ArrayList<>();
            }
        }
        return crossFoldedData;
    }

    protected static int[] calculateSplitPoints(final int folds, final int size) {

        if (folds < 1) { throw new NumberIsTooSmallException(folds, 1, true); }

        if (size < folds + 1) { throw new NumberIsTooSmallException(size, folds + 1, true); }

        int[] splitPoints = new int[folds + 2];
        for (int i = 0; i < folds + 2; i++) {
            splitPoints[i] = (size * i) / (folds + 1);
        }

        return splitPoints;
    }

}

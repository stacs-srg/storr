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

import static org.junit.Assert.assertEquals;

import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.Matrix;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * Created by fraserdunlop on 07/08/2014 at 15:11.
 */
public class MatrixEnlargerTest {

    private Matrix matrix;
    private Matrix largerMatrix;
    private static final int additional = 13;

    @Before
    public void setup() {

        matrix = new DenseMatrix(5, 3);
        fillWithRandomDoubles(matrix);
        largerMatrix = MatrixEnlarger.enlarge(matrix, additional, additional);
    }

    @Test
    public void testCardinalityChange() {

        assertEquals(matrix.columnSize() + additional, largerMatrix.columnSize());
        assertEquals(matrix.rowSize() + additional, largerMatrix.rowSize());
    }

    @Test
    public void testInitialMatrixContainedInNew() {

        assertInitialMatrixContainedInNew(matrix, largerMatrix);
    }

    @Test
    public void testClone() {

        Matrix cloned = matrix.clone();
        Assert.assertFalse(matrix == cloned);
        Assert.assertEquals(matrix.rowSize(), cloned.rowSize());
        Assert.assertEquals(matrix.columnSize(), cloned.columnSize());
        assertInitialMatrixContainedInNew(cloned, matrix);

    }

    protected static void fillWithRandomDoubles(final Matrix matrix) {

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                matrix.set(i, j, Math.random());
            }
        }
    }

    protected static void assertInitialMatrixContainedInNew(final Matrix matrix, final Matrix largerMatrix) {

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                if (withinBounds(matrix, i, j)) {
                    assertEquals(matrix.get(i, j), largerMatrix.get(i, j), 0.00000001);
                }
                else {
                    assertEquals(0, largerMatrix.get(i, j), 0.00000001);
                }
            }
        }
    }

    private static boolean withinBounds(final Matrix matrix, final int i, final int j) {

        return i < matrix.numRows() && j < matrix.numCols();
    }

}

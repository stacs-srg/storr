package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.Matrix;

/**
* Utility class that can be used to enlarge the size of a {@link Matrix}.
* Created by fraserdunlop on 07/08/2014 at 15:44.
*/
final class MatrixEnlarger {

    private MatrixEnlarger() {

    }

    /**
     * Makes an existing matrix bigger by the amount of units specified in additionalColds and additionalRows parameters.
     * All of the content of the original matrix is preserved.
     * Rows and columns are appended on the right and bottom of the existing matrix.
     * @param matrix The {@link Matrix} to enlarge
     * @param additionalCols amount to expand the number of columns by
     * @param additionalRows amount to expand the number of rows by
     * @return the new expanded matrix
     */
    public static Matrix enlarge(final Matrix matrix, final int additionalCols, final int additionalRows) {

        Matrix largerMatrix = new DenseMatrix(matrix.numRows() + additionalRows, matrix.numCols() + additionalCols);
        return copyInto(matrix, largerMatrix);
    }

    private static Matrix copyInto(final Matrix matrix, final Matrix largerMatrix) {

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                largerMatrix.set(i, j, matrix.get(i, j));
            }
        }
        return largerMatrix;
    }
}

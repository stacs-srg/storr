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

    public static Matrix enlarge(final Matrix matrix, final int additionalCols, final int additionalRows) {

        Matrix largerMatrix = new DenseMatrix(matrix.numRows() + additionalRows, matrix.numCols() + additionalCols);
        return copyInto(largerMatrix, matrix);
    }

    private static Matrix copyInto(final Matrix largerMatrix, final Matrix matrix) {

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                largerMatrix.set(i, j, matrix.get(i, j));
            }
        }
        return largerMatrix;
    }
}

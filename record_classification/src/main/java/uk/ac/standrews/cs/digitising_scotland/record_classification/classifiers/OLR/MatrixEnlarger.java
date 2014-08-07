package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR;

import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.Matrix;

/**
* Created by fraserdunlop on 07/08/2014 at 15:44.
*/
class MatrixEnlarger {
    MatrixEnlarger() {
    }

    public static Matrix enlarge(Matrix matrix, int additionalCols, int additionalRows) {
        Matrix largerMatrix =  new DenseMatrix(matrix.numRows() + additionalRows, matrix.numCols() + additionalCols);
        return copyInto(largerMatrix, matrix);
    }

    private static Matrix copyInto(Matrix largerMatrix, Matrix matrix) {
        for(int i = 0; i < matrix.numRows(); i++){
            for(int j = 0; j < matrix.numCols(); j++ ){
                largerMatrix.set(i, j, matrix.get(i, j));
            }
        }
        return largerMatrix;
    }
}

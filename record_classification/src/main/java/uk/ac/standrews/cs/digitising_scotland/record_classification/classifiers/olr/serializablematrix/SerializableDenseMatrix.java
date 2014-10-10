package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.serializablematrix;


import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.Matrix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * Created by fraserdunlop on 10/10/2014 at 13:00.
 */
public class SerializableDenseMatrix implements Serializable {


    private DenseMatrix matrix;

    public SerializableDenseMatrix(double[][] values){
        this.matrix = new DenseMatrix(values);
    }

    public DenseMatrix getMatrix(){
        return matrix;
    }

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        double[][] asArray = (double[][]) inputStream.readObject();
        matrix = new DenseMatrix(asArray);
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(asArray(matrix));
    }

    private double[][] asArray(Matrix matrix) {
        double[][] array = new double[matrix.numRows()][matrix.numCols()];
        for (int i = 0 ; i < matrix.numRows() ; i++)
            for(int j = 0 ; j < matrix.numCols() ; j++)
                array[i][j] = matrix.get(i,j);
        return array;
    }

    public int numCols() {
        return matrix.numCols();
    }

    public int numRows() {
        return matrix.numRows();
    }

    public double get(int i, int j) {
        return matrix.get(i,j);
    }
}

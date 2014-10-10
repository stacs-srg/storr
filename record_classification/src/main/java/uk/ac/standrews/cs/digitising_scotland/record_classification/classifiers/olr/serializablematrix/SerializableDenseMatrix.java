package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.serializablematrix;


import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.Vector;

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

    public SerializableDenseMatrix(Matrix matrix){
        this.matrix = new DenseMatrix(asArray(matrix));
    }

    public SerializableDenseMatrix(int i, int j) {
        this.matrix = new DenseMatrix(i,j);
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


    //TODO put this helper method in a sensible place - it's own class perhaps?
    public double[][] asArray(Matrix matrix) {
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

    public double getQuick(int category, int feature) {
        return matrix.get(category,feature);
    }

    public void setQuick(int category, int feature, double newValue) {
        matrix.setQuick(category,feature,newValue);
    }

    public Vector times(Vector instance) {
        return matrix.times(instance);
    }

    public void set(int category, int feature, double newValue) {
        matrix.set(category,feature,newValue);
    }
}

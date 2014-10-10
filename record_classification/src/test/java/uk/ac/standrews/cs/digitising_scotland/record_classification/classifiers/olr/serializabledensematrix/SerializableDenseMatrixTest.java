package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.serializabledensematrix;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.serializablematrix.SerializableDenseMatrix;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * Created by fraserdunlop on 10/10/2014 at 13:33.
 */
@RunWith(Parameterized.class)
public class SerializableDenseMatrixTest {

    private int numRows;
    private int numCols;

    public SerializableDenseMatrixTest(int numRows, int numCols){
        this.numRows = numRows;
        this.numCols = numCols;
    }

    @Parameterized.Parameters
    public static Collection<Integer[]> params() {
        return Arrays.asList(new Integer[][]{
                {1,2},
                {10,8},
                {45,34},
                {7,7},
        });
    }

    @Test
    public void test() throws IOException, ClassNotFoundException {
        double[][] values = generateValues(numRows, numCols);
        SerializableDenseMatrix matrix = new SerializableDenseMatrix(values);
        FileOutputStream fileOutputStream = new FileOutputStream("target/SerializableDenseMatrix.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(matrix);
        FileInputStream fileInputStream = new FileInputStream("target/SerializableDenseMatrix.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        SerializableDenseMatrix matrix1 = (SerializableDenseMatrix) objectInputStream.readObject();
        assertSame(matrix,matrix1);

    }

    private void assertSame(SerializableDenseMatrix matrix, SerializableDenseMatrix matrix1) {
        Assert.assertEquals(matrix.numCols(),matrix1.numCols());
        Assert.assertEquals(matrix.numRows(),matrix1.numRows());
        for(int i = 0 ; i < matrix.numRows() ; i++)
            for (int j = 0 ; j < matrix.numCols() ; j++)
                Assert.assertEquals(matrix.get(i,j),matrix1.get(i,j),0.0001);
    }

    private double[][] generateValues(int numRows, int numCols) {
        double[][] array = new double[numRows][numCols];
        for(int i = 0 ; i < numRows; i++)
            for (int j = 0 ; j < numCols; j++)
                array[i][j] = (i + Math.random())*(j + Math.random());
        return array;
    }
}

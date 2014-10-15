package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.util.ArrayList;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.CrossFoldedDataStructure;

public class CrossFoldedDataStructureTest {

    @Test(expected = NumberIsTooSmallException.class)
    public void testCalculateSplitPoints() {

        int folds = 0;
        int size = 1;

        CrossFoldedDataStructure.calculateSplitPoints(folds, size);

    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testCalculateSplitPoints3() {

        int folds = 1;
        int size = 0;

        CrossFoldedDataStructure.calculateSplitPoints(folds, size);

    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testCalculateSplitPoints4() {

        int folds = 1;
        int size = 1;

        CrossFoldedDataStructure.calculateSplitPoints(folds, size);

    }

    @Test
    public void testArraySize() {

        testResultSize(1, 2);
        testResultSize(2, 3);
        testResultSize(10, 100);

    }

    private void testResultSize(final int folds, final int size) {

        int[] result = CrossFoldedDataStructure.calculateSplitPoints(folds, size);
        Assert.assertEquals(folds + 2, result.length);
    }

    @Test
    public void testCalculateSplitPointsFirstLastElements() {

        checkFirstLast(1, 2);
        checkFirstLast(2, 10);
        checkFirstLast(5, 100);

    }

    private void checkFirstLast(final int fold, final int size) {

        int[] result = CrossFoldedDataStructure.calculateSplitPoints(fold, size);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(size, result[result.length - 1]);
    }

    @Test
    public void checkIntervals() {

        checkIntervals(1, 2);
        checkIntervals(2, 10);
        checkIntervals(10, 100);

    }

    private void checkIntervals(final int folds, final int size) {

        int[] result = CrossFoldedDataStructure.calculateSplitPoints(folds, size);

        for (int i = 0; i < result.length - 1; i++) {
            Assert.assertEquals((double) size / (folds + 1), (double) result[i + 1] - result[i], 2.0);
        }
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testInitalizeroFolds() {

        CrossFoldedDataStructure.initialize(0);

    }

    @Test
    public void testInitalizeAllSizes() {

        checkInit(1);
        checkInit(2);
        checkInit(10);

    }

    private void checkInit(final int folds) {

        ArrayList<NamedVector>[][] arr = CrossFoldedDataStructure.initialize(folds);
        Assert.assertEquals(2, arr[0].length);
        Assert.assertEquals(folds + 1, arr.length);
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testMakeZeroFolds() {

        ArrayList<NamedVector> trainingVectorList = new ArrayList<>();
        int folds = 0;
        ArrayList<NamedVector>[][] arr = CrossFoldedDataStructure.make(trainingVectorList, folds);

    }

    @Test
    public void testMake() {

        checkMake(1, testTrainingVectors(2));
        checkMake(2, testTrainingVectors(10));
        checkMake(10, testTrainingVectors(100));

    }

    private void checkMake(final int folds, final ArrayList<NamedVector> trainingVectorList) {

        ArrayList<NamedVector>[][] arr = CrossFoldedDataStructure.make(trainingVectorList, folds);
        Assert.assertEquals(2, arr[0].length);
        Assert.assertEquals(folds + 1, arr.length);
    }

    private ArrayList<NamedVector> testTrainingVectors(final int size) {

        ArrayList<NamedVector> trainingVectorList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            trainingVectorList.add(new NamedVector(new RandomAccessSparseVector(5), "test"));

        }
        return trainingVectorList;
    }
}

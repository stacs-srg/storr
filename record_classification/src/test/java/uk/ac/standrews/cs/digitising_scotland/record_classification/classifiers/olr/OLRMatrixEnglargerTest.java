package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.util.Properties;

import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.Matrix;
import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

public class OLRMatrixEnglargerTest {

    @Test
    public void testOLRfromBetaConstructor() {

        Matrix beta = createMatrix(5, 3);
        Properties properties = MachineLearningConfiguration.getDefaultProperties();
        OLR olr = new OLR(properties, beta);
        assertSameContentDifferentObject(olr.getBeta(), beta);
        Assert.assertEquals(6, olr.getNumCategories());
        Assert.assertEquals(3, olr.getNumFeatures());
        Assert.assertEquals(3, olr.getUpdateCounts().length);
        Assert.assertEquals(3, olr.getUpdateSteps().length);

    }

    private void assertSameContentDifferentObject(final Matrix matrix, final Matrix cloned) {

        Assert.assertFalse(matrix == cloned);
        Assert.assertEquals(matrix.rowSize(), cloned.rowSize());
        Assert.assertEquals(matrix.columnSize(), cloned.columnSize());
        MatrixEnlargerTest.assertInitialMatrixContainedInNew(cloned, matrix);
    }

    private Matrix createMatrix(final int rows, final int cols) {

        Matrix matrix = new DenseMatrix(rows, cols);
        MatrixEnlargerTest.fillWithRandomDoubles(matrix);
        return matrix;
    }

}

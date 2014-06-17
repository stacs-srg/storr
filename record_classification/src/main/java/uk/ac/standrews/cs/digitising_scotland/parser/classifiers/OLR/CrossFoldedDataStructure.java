package uk.ac.standrews.cs.digitising_scotland.parser.classifiers.OLR;

import java.util.ArrayList;

import org.apache.mahout.math.NamedVector;

/**
 * Abstract class for the creation of cross folded data structures.
 * @author frjd2
 */
public abstract class CrossFoldedDataStructure {

    /**
     * Creates a cross folded data structure.
     * @param trainingVectorList training vector list
     * @param folds number of folds
     * @return ArrayList<NamedVector>[][]
     */
    public static ArrayList<NamedVector>[][] make(final ArrayList<NamedVector> trainingVectorList, final int folds) {

        //TODO refactor
        ArrayList<NamedVector>[][] crossFoldedData = initialize(folds);
        int size = trainingVectorList.size();
        int[] splitPoints = calculateSplitPoints(folds, size);

        splitPoints[0] -= 1;

        for (int i = 0; i < folds; i++) {
            for (int j = 0; j < size; j++) {
                if ((j > splitPoints[i] && j <= splitPoints[i + 1])) {
                    crossFoldedData[i][1].add(trainingVectorList.get(j));
                }
                else {
                    crossFoldedData[i][0].add(trainingVectorList.get(j));
                }
            }
        }
        return crossFoldedData;
    }

    private static ArrayList<NamedVector>[][] initialize(final int folds) {

        ArrayList[][] crossFoldedData = new ArrayList[folds][2];

        for (int i = 0; i < folds; i++) {
            for (int j = 0; j < 2; j++) {
                crossFoldedData[i][j] = new ArrayList<NamedVector>();
            }
        }
        return crossFoldedData;
    }

    private static int[] calculateSplitPoints(final int folds, final int size) {

        int[] splitPoints = new int[folds + 1];
        for (int i = 0; i <= folds; i++) {
            splitPoints[i] = (size * i) / folds;
        }
        return splitPoints;
    }

}

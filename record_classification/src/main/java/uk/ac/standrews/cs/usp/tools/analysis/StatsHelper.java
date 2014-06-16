package uk.ac.standrews.cs.usp.tools.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import uk.ac.standrews.cs.usp.tools.Utils;

/**
 * Basic machine learning statistical analysis code.
 * 
 * @author jkc25
 * 
 */
public final class StatsHelper {

    private StatsHelper() {

        //private constructor - utility class
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws FileNotFoundException the file not found exception
     */
    public static void main(final String[] args) throws FileNotFoundException {

        numberCrunch(new File("thresholdtest.csv"));
    }

    /**
     * Calculates term frequency.
     * 
     * @param in
     *            FIle to read.
     * @return true if successful.
     * @throws FileNotFoundException
     *             if file is not found.
     */
    public static boolean numberCrunch(final File in) throws FileNotFoundException {

        Scanner s = new Scanner(in, "UTF-8");

        int maxRange = getMaxRange(in) + 1;
        int[][] data = new int[maxRange][2];

        while (s.hasNextLine()) {
            String line = s.nextLine();
            int range = Math.abs(Integer.parseInt(line.split(",")[0]));
            int val = Integer.parseInt(line.split(",")[1]);

            if (val == 0) {
                data[range][0] = data[range][0] + 1;
            }
            if (val == 1) {
                data[range][1] = data[range][1] + 1;
            }
        }
        s.close();
        Utils.writeToFile(data, "ttest.csv");

        return true;
    }

    private static int getMaxRange(final File in) throws FileNotFoundException {

        Scanner s = new Scanner(in, "UTF-8");
        int maxRange = 0;
        while (s.hasNextLine()) {
            String line = s.nextLine();
            int range = Math.abs(Integer.parseInt(line.split(",")[0]));
            if (range > maxRange) {
                maxRange = range;
            }

        }
        s.close();
        return maxRange;
    }

}

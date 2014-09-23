package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Basic machine learning statistical analysis code.
 * 
 * @author jkc25
 * 
 */
public final class StatsHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatsHelper.class);

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
     * @param inputFile
     *            FIle to read.
     * @return true if successful.
     * @throws FileNotFoundException
     *             if file is not found.
     */
    public static boolean numberCrunch(final File inputFile) throws FileNotFoundException {

        BufferedReader in = initReader(inputFile);

        int maxRange = getMaxRange(inputFile) + 1;
        int[][] data = new int[maxRange][2];
        String line = "";

        try {
            while ((line = in.readLine()) != null) {
                int range = Math.abs(Integer.parseInt(line.split(",")[0]));
                int val = Integer.parseInt(line.split(",")[1]);

                if (val == 0) {
                    data[range][0] = data[range][0] + 1;
                }
                if (val == 1) {
                    data[range][1] = data[range][1] + 1;
                }
            }
        }
        catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            LOGGER.error(e.getMessage(), e.getCause());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.error(e.getMessage(), e.getCause());
        }
        closeReader(in);
        Utils.writeToFile(data, "ttest.csv");

        return true;
    }

    private static void closeReader(Reader in) {

        try {
            in.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.error(e.getMessage(), e.getCause());
        }
    }

    private static int getMaxRange(final File inputFile) throws FileNotFoundException {

        BufferedReader in = initReader(inputFile);
        String line = "";
        int maxRange = 0;
        try {
            while ((line = in.readLine()) != null) {
                int range = Math.abs(Integer.parseInt(line.split(",")[0]));
                if (range > maxRange) {
                    maxRange = range;
                }

            }
        }
        catch (NumberFormatException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
        closeReader(in);
        return maxRange;
    }

    private static BufferedReader initReader(final File inputFile) throws FileNotFoundException {

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
        }
        catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
        return in;
    }

}

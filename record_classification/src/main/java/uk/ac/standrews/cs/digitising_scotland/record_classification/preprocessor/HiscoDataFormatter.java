package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.analysis.HumanCodingAnalyser;

// TODO: Auto-generated Javadoc
/**
 * The Class HiscoDataFormatter.
 */
public class HiscoDataFormatter {

    /** The input file. */
    private File inputFile;

    /** The input map. */
    private HashMap<String, HashMap<String, Integer>> inputMap;

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {

        File inputFile = new File("htd.txt");
        HiscoDataFormatter hdf = new HiscoDataFormatter(inputFile);
        try {
            hdf.correctClassesRemoveMostVariable();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Instantiates a new hisco data formatter.
     *
     * @param input the input
     */
    public HiscoDataFormatter(final File input) {

        inputFile = input;
        HumanCodingAnalyser hca = new HumanCodingAnalyser(inputFile);
        this.inputMap = hca.getInputMap();
        hca.printContents();
    }

    /**
     * Removes the multiple classes over threshold.
     *
     * @param threshold the threshold
     */
    public void removeMultipleClassesOverThreshold(final int threshold) {

    }

    /**
     * Correct classes to most popular.
     *
     * @return the hash map
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public HashMap<String, Map<String, Integer>> correctClassesToMostPopular() throws IOException {

        StringBuilder sb = new StringBuilder();

        Set<Entry<String, HashMap<String, Integer>>> set = inputMap.entrySet();
        Iterator<Entry<String, HashMap<String, Integer>>> outerIterator = set.iterator();

        HashMap<String, Map<String, Integer>> sortedMap = generateSortedMap(outerIterator);

        Iterator<Entry<String, Map<String, Integer>>> iterator = sortedMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, Map<String, Integer>> codings = iterator.next();
            Set<Entry<String, Integer>> innerMap = codings.getValue().entrySet();
            Iterator<Entry<String, Integer>> innerIterator = innerMap.iterator();
            Entry<String, Integer> best = innerIterator.next();
            String mostPopularClass = best.getKey();
            int numberOfPopularClass = best.getValue();
            for (int i = 0; i < numberOfPopularClass; i++) {
                sb.append(codings.getKey() + "\t" + mostPopularClass + "\n");
            }

            while (innerIterator.hasNext()) {
                Entry<String, Integer> current = innerIterator.next();
                int number = current.getValue();

                for (int i = 0; i < number; i++) {
                    sb.append(codings.getKey() + "\t" + mostPopularClass + "\n");
                }
            }

        }
        System.out.println(sb.toString());

        Utils.writeToFile(sb.toString(), "hiscoNoClassVariance.txt");
        return sortedMap;

    }

    /**
     * Generate sorted map.
     *
     * @param outerIterator the outer iterator
     * @return the hash map
     */
    private HashMap<String, Map<String, Integer>> generateSortedMap(final Iterator<Entry<String, HashMap<String, Integer>>> outerIterator) {

        HashMap<String, Map<String, Integer>> sortedMap = new HashMap<String, Map<String, Integer>>();
        while (outerIterator.hasNext()) {
            Entry<String, HashMap<String, Integer>> element = outerIterator.next();
            if (element.getValue() != null && !element.getKey().toString().trim().equals("")) {

                Map<String, Integer> inner = Utils.sortByValueDescending(inputMap.get(element.getKey()));
                sortedMap.put(element.getKey(), inner);
            }
        }
        return sortedMap;
    }

    /**
     * Analyses the input file map and removes the most variables classes from the input.
     * The resulting class is written to "hiscoRemovedVariableClasses.txt"
     *
     * @return sorted hashmap of new data.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public HashMap<String, Map<String, Integer>> correctClassesRemoveMostVariable() throws IOException {

        int count = 0;
        Set<Entry<String, HashMap<String, Integer>>> set = inputMap.entrySet();
        Iterator<Entry<String, HashMap<String, Integer>>> outerIterator = set.iterator();

        HashMap<String, Map<String, Integer>> sortedMap = generateSortedMap(outerIterator);

        StringBuilder sb = new StringBuilder();

        Iterator<Entry<String, Map<String, Integer>>> iterator = sortedMap.entrySet().iterator();

        //for every input string
        while (iterator.hasNext()) {

            int totalThisClass = 0;
            double variance = 0;
            Entry<String, Map<String, Integer>> codings = iterator.next();
            Set<Entry<String, Integer>> innerMap = codings.getValue().entrySet();
            Iterator<Entry<String, Integer>> innerIterator = innerMap.iterator();

            Entry<String, Integer> best = innerIterator.next();
            String mostPopularClass = best.getKey();
            int numberOfPopularClass = best.getValue();
            totalThisClass += numberOfPopularClass;
            StringBuilder innerStringBuilder = new StringBuilder();

            for (int i = 0; i < numberOfPopularClass; i++) {
                innerStringBuilder.append(codings.getKey() + "\t" + mostPopularClass + "\n");
            }

            while (innerIterator.hasNext()) {
                System.out.println(count);

                count++;

                Entry<String, Integer> current = innerIterator.next();
                int number = current.getValue();
                totalThisClass += number;
                variance = (double) numberOfPopularClass / (double) totalThisClass;
                for (int i = 0; i < number; i++) {
                    innerStringBuilder.append(codings.getKey() + "\t" + current.getKey() + "\n");
                }
            }
            if ((variance < 0.9) && numberOfPopularClass > 25) {
                //do nothing
            }
            else {
                sb.append(innerStringBuilder.toString());

            }

        }
        Utils.writeToFile(sb.toString(), "hiscoRemovedVariableClasses.txt");
        return sortedMap;
    }

    /**
     * Gets the inputFile.
     * @return inputFile.
     */
    public File getInputFile() {

        return inputFile;
    }

    /**
     * Sets the input file.
     * @param inputFile input file.
     */
    public void setInputFile(final File inputFile) {

        this.inputFile = inputFile;
    }

    /**
     * Gets the input map.
     * @return the input map
     */
    public HashMap<String, HashMap<String, Integer>> getInputMap() {

        return inputMap;
    }

    /**
     * Sets the input map.
     * @param inputMap inputmap
     */
    public void setInputMap(final HashMap<String, HashMap<String, Integer>> inputMap) {

        this.inputMap = inputMap;
    }
}

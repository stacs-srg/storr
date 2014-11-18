/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Class contains methods for analyzing the precision and recall stats from an
 * experimental run.
 * 
 * @author jkc25
 * 
 */
public class AnalysisTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisTools.class);

    /** The Constant LUCENE_VERSION. */
    private static final Version LUCENE_VERSION = Version.LUCENE_36;

    /** The input. */
    private File input;

    /** The string input. */
    private String[][] stringInput;

    /** The classification map. */
    private HashMap<String, AccuracyMetrics> classificationMap;

    private static final int MULTIPLIER1000 = 1000;

    /**
     * Constructs a hashmap contains the classification name as the key and the
     * accuracy metrics as the value.
     * 
     * @return <String, AccuracyMetrics>
     */
    public HashMap<String, AccuracyMetrics> getClassificationMap() {

        return classificationMap;
    }

    /**
     * Constructs an AnalysisTools object.
     * 
     * @param fileToBeAnalysed
     *            the file containing the experimental results.
     */
    public AnalysisTools(final File fileToBeAnalysed) {

        input = fileToBeAnalysed;
        classificationMap = new HashMap<String, AccuracyMetrics>();
        try {
            buildMap();
            totalNumberOfClasses();
            getNumberOfLines();
            calculateTN();
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * Builds the map.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void buildMap() throws IOException {

        BufferedReader br = ReaderWriterFactory.createBufferedReader(input);

        int i = 0;
        int noOfLines = getNumberOfLines();
        final int inputLength = 16;
        stringInput = new String[noOfLines][inputLength];
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(Utils.getCSVComma());

                for (int j = 0; j < split.length; j++) {
                    stringInput[i][j] = split[j];
                }

                i++;
            }
        }
        catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
        finally {
            br.close();
        }

    }

    /**
     * Gets the number of lines.
     *
     * @return the number of lines
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private int getNumberOfLines() throws IOException {

        return Utils.getNumberOfLines(input);
    }

    /**
     * Returns both the precision and the recall of the experiment in the form
     * of an array. precision, recall.
     * 
     * @param classification
     *            The classification you want to get the statistics for.
     * @return double array, {precision, recall}
     */
    public double[] getPrecisionAndRecall(final String classification) {

        return new double[]{getPrecision(classification), getRecall(classification)};
    }

    /**
     * Returns the precision of the experiment.
     * 
     * Returns the recall value from the experiment.
     * 
     * In a classification task, the precision for a class is the number of true
     * positives (i.e. the number of items correctly labeled as belonging to the
     * positive class) divided by the total number of elements labeled as
     * belonging to the positive class (i.e. the sum of true positives and false
     * positives, which are items incorrectly labeled as belonging to the
     * class). Recall in this context is defined as the number of true positives
     * divided by the total number of elements that actually belong to the
     * positive class (i.e. the sum of true positives and false negatives, which
     * are items which were not labeled as belonging to the positive class but
     * should have been).
     * 
     * @param classification
     *            The classification you want the precision of.
     * 
     * @return precision
     */
    public double getPrecision(final String classification) {

        int numberOfCorrectItems;
        if (buildPrecisionMap().get(classification) != null) {
            numberOfCorrectItems = buildPrecisionMap().get(classification);
        }
        else {
            numberOfCorrectItems = 0;
        }

        int totalNumberOfElementsLabeledInClass = totalNumberInEachPrediction(classification);

        if (classificationMap.get(classification) != null) {
            classificationMap.get(classification).setTP(numberOfCorrectItems);
        }
        else {
            classificationMap.put(classification, new AccuracyMetrics(classification));
            classificationMap.get(classification).setTP(numberOfCorrectItems);
        }

        classificationMap.get(classification).setFP(totalNumberOfElementsLabeledInClass - numberOfCorrectItems);

        return (double) numberOfCorrectItems / (double) totalNumberOfElementsLabeledInClass;
    }

    /**
     * 
     * Returns the recall of a class.
     * 
     * The instances are documents and the task is to return a set of relevant
     * documents given a search term; or equivalently, to assign each document
     * to one of two categories, "relevant" and "not relevant". In this case,
     * the "relevant" documents are simply those that belong to the "relevant"
     * category. Recall is defined as the number of relevant documents retrieved
     * by a search divided by the total number of existing relevant documents,
     * while precision is defined as the number of relevant documents retrieved
     * by a search divided by the total number of documents retrieved by that
     * search.
     * 
     * @param classification The classification (or label, ie Old Age) that we want the accuracy metrics for.
     * @return recall
     */
    public double getRecall(final String classification) {

        int numberOfCorrectItems;

        if (buildPrecisionMap().get(classification) != null) {
            numberOfCorrectItems = buildPrecisionMap().get(classification);
        }
        else {
            numberOfCorrectItems = 0;
        }

        int totalNumberActuallyInClass;

        if (buildPrecisionMap().get(classification) != null) {
            totalNumberActuallyInClass = totalNumberInEachClass(classification);
        }
        else {
            totalNumberActuallyInClass = 0;
        }

        if (classificationMap.get(classification) != null) {
            classificationMap.get(classification).setFN(totalNumberActuallyInClass - numberOfCorrectItems);
            getPrecision(classification);
            setTN(classification);
        }
        return (double) numberOfCorrectItems / (double) totalNumberActuallyInClass;
    }

    private void setTN(final String classification) {

        try {
            int nl = getNumberOfLines();
            double tp = classificationMap.get(classification).getTP();
            double fn = classificationMap.get(classification).getFN();
            double fp = classificationMap.get(classification).getFP();

            classificationMap.get(classification).setTN(nl - tp - fn - fp);
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * Returns the True Negative of the sample set.
     *
     */
    public final void calculateTN() {

        Set<String> ks = classificationMap.keySet();
        Object[] ksa = ks.toArray();

        for (int i = 0; i < ksa.length; i++) {
            String classification = (String) ksa[i];
            int totalNumberActuallyInClass = totalNumberInEachClass(classification);
            int numberOfCorrectItems = buildPrecisionMap().get(classification);
            classificationMap.get(classification).setFN(totalNumberActuallyInClass - numberOfCorrectItems);
            getPrecision(classification);
            setTN(classification);
        }

    }

    /**
     * Micro average of a set of sample results. This is the sum of the true positives divided
     * by the sum of the true positives and the false positives.
     * @return double micro averaged precision
     */
    public double getMicroAveragePrecision() {

        Object[] mapSet = classificationMap.keySet().toArray();
        double tpTotal = 0;
        double fpTotal = 0;
        for (int i = 0; i < classificationMap.size(); i++) {
            tpTotal += classificationMap.get(mapSet[i]).getTP();
            fpTotal += classificationMap.get(mapSet[i]).getFP();
        }
        return tpTotal / (tpTotal + fpTotal);
    }

    /**
     * Micro average of a set of sample results. This is the sum of the true positives divided
     * by the sum of the true positives and the false negatives.
     * @return double micro averaged recall
     */
    public double getMicroAverageRecall() {

        Object[] mapSet = classificationMap.keySet().toArray();
        double tpTotal = 0;
        double fnTotal = 0;
        for (int i = 0; i < classificationMap.size(); i++) {
            tpTotal += classificationMap.get(mapSet[i]).getTP();
            fnTotal += classificationMap.get(mapSet[i]).getFN();
        }
        return tpTotal / (tpTotal + fnTotal);
    }

    /**
     * Macro average of a set of sample results. This is the average of the precision on all the sets.
     * @return double marco averaged recall
     */
    public double getMacroAveragePrecision() {

        Object[] mapSet = classificationMap.keySet().toArray();
        double precisionTotal = 0;

        for (int i = 0; i < classificationMap.size(); i++) {
            precisionTotal += classificationMap.get(mapSet[i]).getPrecision();
        }
        return precisionTotal / (double) classificationMap.size();
    }

    /**
        * Macro average of a set of sample results. This is the average of the recall on all the sets.
        * @return macro average of the recall
        */
    public double getMacroAverageRecall() {

        double recallTotal = 0;
        Object[] mapSet = classificationMap.keySet().toArray();

        for (int i = 0; i < classificationMap.size(); i++) {
            recallTotal += classificationMap.get(mapSet[i]).getRecall();
        }
        return recallTotal / (double) classificationMap.size();
    }

    /**
     * Returns the total number of unique words in the dataset, or the feature
     * space.
     * 
     * @return int number of unique words.
     */
    public int countUniqueWords() {

        Map<String, Integer> words = buildMapping();
        return words.size();
    }

    /**
     * Returns the total number of unique words in the dataset, or the feature
     * space.
     * 
     * @param word
     *            the word to look up in the table for the bnumber of words.
     * @return int number of unique words.
     */
    public int getWordTotal(final String word) {

        Map<String, Integer> words = buildMapping();

        return words.get(word);
    }

    /**
     * Returns the total number of classes.
     * 
     * @return total number of classes.
     */
    public final int totalNumberOfClasses() {

        Map<String, Integer> words = new HashMap<String, Integer>();
        for (int i = 0; i < stringInput.length; i++) {
            String classification = stringInput[i][1];

            if (words.get(classification) != null) {
                words.put(classification, words.get(classification) + 1);
            }
            else {
                words.put(classification, 1);
                classificationMap.put(classification, new AccuracyMetrics(classification));
            }
        }
        return words.size();
    }

    /**
     * Builds the map that maps precision values to class values.
     * 
     * @return a map of classes and precision values.
     */
    public Map<String, Integer> buildPrecisionMap() {

        Map<String, Integer> words = new HashMap<String, Integer>();

        for (int i = 0; i < stringInput.length; i++) {
            String classification = stringInput[i][1];

            if (words.get(classification) != null) {
                if (stringInput[i][1].equalsIgnoreCase(stringInput[i][2])) {
                    words.put(classification, words.get(classification) + 1);
                }
            }
            else {
                if (stringInput[i][1].equalsIgnoreCase(stringInput[i][2])) {
                    words.put(classification, 1);
                }
                else {
                    words.put(classification, 0);
                }
            }
        }
        return words;
    }

    /**
     * Builds the map that maps recall values to class values.
     * 
     * @return a map of classes and recall values.
     */
    public int buildrecallMap() {

        Map<String, Integer> words = new HashMap<String, Integer>();
        for (int i = 0; i < stringInput.length; i++) {
            String classification = stringInput[i][1];

            if (words.get(classification) != null) {
                words.put(classification, words.get(classification) + 1);
            }
            else {
                words.put(classification, 1);
            }
        }
        return words.size();
    }

    /**
     * Returns the number of words in the given class.
     * 
     * @param classToLookFor
     *            The class you want to know the number of words for.
     * @return Number of words.
     */
    public int totalNumberInEachClass(final String classToLookFor) {

        Map<String, Integer> words = new HashMap<String, Integer>();
        for (int i = 0; i < stringInput.length; i++) {
            String classification = stringInput[i][1];

            if (words.get(classification) != null) {
                words.put(classification, words.get(classification) + 1);
            }
            else {
                words.put(classification, 1);
            }
        }
        return words.get(classToLookFor);
    }

    /**
     * How many inputs have been assigned to the given prediction.
     * 
     * @param classToLookFor The class to look up.
     * @return The number of words in the provided class, if the word cannot be
     *         found, returns 0.
     */
    public int totalNumberInEachPrediction(final String classToLookFor) {

        Map<String, Integer> words = new HashMap<String, Integer>();
        for (int i = 0; i < stringInput.length; i++) {
            String classification = stringInput[i][2];

            if (words.get(classification) != null) {
                words.put(classification, words.get(classification) + 1);
            }
            else {
                words.put(classification, 1);
            }
        }
        if (words.get(classToLookFor) != null) {
            return words.get(classToLookFor);
        }
        else {
            return 0;
        }

    }

    /**
     * Returns a map of input words ordered by frequency.
     * 
     * @return map of input words ordered by frequency.
     */
    public Map<String, Integer> listWordsOrderedByFrequency() {

        Map<String, Integer> words = buildMapping();
        words.values();
        Map<String, Integer> sortedWords = (HashMap<String, Integer>) sortByValue(words);
        Set<String> k = sortedWords.keySet();
        Object[] ka = k.toArray();
        Collection<Integer> v = sortedWords.values();
        Object[] va = v.toArray();
        for (int i = 0; i < ka.length; i++) {
            LOGGER.info(ka[i] + "\t" + va[i]);
        }
        return sortedWords;
    }

    /**
     * Sort by value.
     *
     * @param map the map
     * @return the map
     */
    static Map<String, Integer> sortByValue(final Map<String, Integer> map) {

        Set<Map.Entry<String, Integer>> entry = map.entrySet();
        Comparator<Map.Entry<String, Integer>> comparator = new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(final Map.Entry<String, Integer> o1, final Map.Entry<String, Integer> o2) {

                return o1.getValue().compareTo(o2.getValue());
            }
        };
        SortedSet<Map.Entry<String, Integer>> sortedSet = new TreeSet<Entry<String, Integer>>(comparator);

        for (Entry<String, Integer> entry2 : entry) {
            sortedSet.add(entry2);
        }

        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry1 : sortedSet) {
            sortedMap.put(entry1.getKey(), entry1.getValue());
        }
        LOGGER.info(sortedMap.toString());

        return sortedMap;
    }

    /**
     * Builds the mapping.
     *
     * @return the hash map
     */
    private Map<String, Integer> buildMapping() {

        Map<String, Integer> words = new HashMap<String, Integer>();
        for (int i = 0; i < stringInput.length; i++) {
            String[] lineWords = stringInput[i][0].split("\\s");

            for (int j = 0; j < lineWords.length; j++) {
                if (words.get(lineWords[j]) != null) {
                    words.put(lineWords[j], words.get(lineWords[j]) + 1);
                }
                else {
                    words.put(lineWords[j], 1);

                }
            }
        }
        return words;
    }

    /**
     * Returns a sorted list of the predictions, sorted by recall.
     * 
     * @return List<AccuracyMetrics> sorted by recall, lowest to highest
     */
    public List<AccuracyMetrics> listByRecall() {

        Map<String, AccuracyMetrics> output = getClassificationMap();

        List<AccuracyMetrics> classbyRecall = new ArrayList<AccuracyMetrics>(output.values());

        Collections.sort(classbyRecall, new Comparator<AccuracyMetrics>() {

            public int compare(final AccuracyMetrics o1, final AccuracyMetrics o2) {

                return (int) ((int) (o1.getRecall() * MULTIPLIER1000) - (int) (o2.getRecall() * MULTIPLIER1000));
            }
        });

        for (AccuracyMetrics p : classbyRecall) {
            LOGGER.info("Recall of " + p.getClassification() + "\t" + p.getRecall());
        }

        return classbyRecall;
    }

    /**
     * Returns a sorted list of the predictions, sorted by precision.
     * 
     * @return List<AccuracyMetrics> sorted by precision, lowest to highest
     */
    public List<AccuracyMetrics> listByPrecision() {

        Map<String, AccuracyMetrics> output = getClassificationMap();

        List<AccuracyMetrics> classbyPrecision = new ArrayList<AccuracyMetrics>(output.values());

        Collections.sort(classbyPrecision, new Comparator<AccuracyMetrics>() {

            public int compare(final AccuracyMetrics o1, final AccuracyMetrics o2) {

                return (int) ((int) (o1.getPrecision() * MULTIPLIER1000) - (int) (o2.getPrecision() * MULTIPLIER1000));
            }
        });

        for (AccuracyMetrics p : classbyPrecision) {
            LOGGER.info("Precision of " + p.getClassification() + "\t" + p.getPrecision());
        }

        return classbyPrecision;
    }

    /**
     * Returns a sorted list of the predictions, sorted by accuracy.
     * 
     * @return List<AccuracyMetrics> sorted by accuracy, lowest to highest.
     */
    public List<AccuracyMetrics> listByAccuracy() {

        Map<String, AccuracyMetrics> output = getClassificationMap();

        List<AccuracyMetrics> classbyAccuracy = new ArrayList<AccuracyMetrics>(output.values());

        Collections.sort(classbyAccuracy, new Comparator<AccuracyMetrics>() {

            public int compare(final AccuracyMetrics o1, final AccuracyMetrics o2) {

                return (int) ((int) (o1.getAccuracy() * MULTIPLIER1000) - (int) (o2.getAccuracy() * MULTIPLIER1000));
            }
        });

        for (AccuracyMetrics p : classbyAccuracy) {
            LOGGER.info("Accuracy of " + p.getClassification() + "\t" + p.getAccuracy());
        }
        return classbyAccuracy;
    }

    /**
     * Returns the ratio of unique strings in he test file that are not in the training file.
     * @param trainingFile traingin file
     * @param testingFile testing file
     * @return double ratio of unique lines in testing file to unique lines in training file
     * @throws IOException indiccate IO error
     */
    public double uniquenessRatio(final File trainingFile, final File testingFile) throws IOException {

        double ratio = 0;
        double uniqueTestingLines = 0;
        Set<String> uniqueLinesTraining = getUniqueLines(trainingFile).keySet();
        Set<String> uniqueLinesTesting = getUniqueLines(testingFile).keySet();
        Iterator<String> iterator = uniqueLinesTraining.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (!uniqueLinesTesting.contains(line)) {
                uniqueTestingLines++;
            }
        }
        LOGGER.info("uniqueTestingLines " + uniqueTestingLines);
        LOGGER.info("uniqueLinesTraining " + uniqueLinesTraining.size());
        ratio = uniqueTestingLines / (double) uniqueLinesTraining.size();
        return ratio;
    }

    /**
     * Returns a hashmap of all the unique lines in the supplied input file.
     * Map has unique string, count as contents.
     *
     * @param trainingFile the training file
     * @return the unique lines
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Map<String, Integer> getUniqueLines(final File trainingFile) throws IOException {

        BufferedReader br = ReaderWriterFactory.createBufferedReader(trainingFile);
        String line = "";
        Map<String, Integer> uniqueLines = new HashMap<String, Integer>();
        while ((line = br.readLine()) != null) {
            line = line.split("\\t")[0].trim();
            line = standardise(line);
            if (!uniqueLines.containsKey(line)) {
                uniqueLines.put(line, 1);
            }
            else {
                uniqueLines.put(line, uniqueLines.get(line) + 1);
            }
        }
        br.close();
        return uniqueLines;
    }

    /**
     * Standardise.
     *
     * @param line the line
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String standardise(final String line) throws IOException {

        Analyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);
        Reader in = new StringReader(line);
        TokenStream ts = analyzer.reusableTokenStream("text", in);
        ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        StringBuilder sb = new StringBuilder();
        while (ts.incrementToken()) {
            String s = ts.getAttribute(CharTermAttribute.class).toString();
            sb.append(s + " ");
        }
        analyzer.close();
        return sb.toString();
    }
}

/*
 * 
 */
package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * The Class DictionaryStatHelper.
 */
public class DictionaryStatHelper {

    private HashMap<String, Integer> originalMap;
    private HashMap<String, Integer> correctedMap;
    private String[] originalDataByLine;
    private String[] correctedDataByLine;

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(final String[] args) throws IOException {

        File input = new File("kilmAll2.txt");
        File newFile = new File("kilmAll2-en-GB-wlist-Med-Arc-Filtered2.txt");
        DictionaryStatHelper dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        newFile = new File("kilmAll2-en-GB-wlist-Med-Arc-Filtered30.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        newFile = new File("kilmAll2-en-GB-wlist-Med-Arc-Filtered10.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        newFile = new File("kilmAll2-en-GB-wlist-Med-Arc-Filtered80.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        input = new File("Tasmania_codcoll.txt");
        newFile = new File("Tasmania_codcoll.-en-GB-wlist.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        newFile = new File("Tasmania_codcoll-en-GB-wlist-Med.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        newFile = new File("Tasmania_codcoll-en-GB-wlist-Arc.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        newFile = new File("Tasmania_codcoll-en-GB-wlist-Med-Arc.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);
        dsh.getMapOfMostCommonWords(dsh.originalDataByLine);

        System.out.println("");
        input = new File("mass.txt");
        newFile = new File("mass-en-GB-wlist.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        newFile = new File("mass-en-GB-wlist-Med.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        input = new File("mass.txt");
        newFile = new File("mass-en-GB-wlist-Arc.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);

        System.out.println("");
        newFile = new File("mass-en-GB-wlist-Med-Arc.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);
        dsh.getMapOfMostCommonWords(dsh.originalDataByLine);
        // dsh.printMostCommonWords(dsh.correctedDataByLine);

        System.out.println("");
        input = new File("kilmAll2.txt");
        newFile = new File("KilmAll2-en-GB-wlist-Med-Arc-Removed-nopun.txt");
        dsh = new DictionaryStatHelper(input, newFile);
        printStats(input, newFile, dsh);
        dsh.getMapOfMostCommonWords(dsh.correctedDataByLine);
    }

    private static void printStats(final File input, final File newFile, final DictionaryStatHelper dsh) {

        System.out.println("File Name: " + newFile.getName());
        System.out.println("Total Number of Words " + dsh.getTotalWords());
        System.out.println("number of corrections: " + dsh.getNumberOfCorrections());
        System.out.println("Total number of lines: " + dsh.getNumberOfLines(input));
        System.out.println("number of lines corrected: " + dsh.getNumberOfLinesCorrected());
        System.out.println("unique words before " + dsh.getUniqueWordsOriginal());
        System.out.println("unique words after " + dsh.getUniqueWordsCorrected());
        System.out.println("Pecentage corrected " + dsh.getPercentageOfWordsCorrected() + "%");
    }

    private int getNumberOfLines(final File input) {

        try {
            return Utils.getNumberOfLines(input);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Calculates the major metrics of a given dictionary compared with the
     * original.
     * 
     * @param originalData
     *            Original dictionary.
     * @param correctedData
     *            New Dictionary.
     * @throws IOException
     *             If we cant read the dictionary.
     */
    public DictionaryStatHelper(final File originalData, final File correctedData) throws IOException {

        originalDataByLine = new String[getNumberOfLines(originalData)];
        correctedDataByLine = new String[getNumberOfLines(correctedData)];
        int i = 0;

        Scanner s = new Scanner(originalData, "UTF-8");
        while (s.hasNextLine()) {
            originalDataByLine[i] = s.nextLine().split("\t")[1];
            i++;
        }

        i = 0;
        s.close();
        s = new Scanner(correctedData, "UTF-8");
        while (s.hasNextLine()) {
            correctedDataByLine[i] = s.nextLine().split("\t")[1];
            i++;
        }

        originalMap = buildMapping(originalDataByLine);
        correctedMap = buildMapping(correctedDataByLine);
        s.close();
    }

    /**
     * Returns the total number of words in the dictionary.
     * 
     * @return total number of words.
     */
    public int getTotalWords() {

        int totalWords = 0;
        Object[] arr = originalMap.keySet().toArray();
        Object[] foo = originalMap.values().toArray();
        for (int i = 0; i < arr.length; i++) {
            totalWords += Integer.parseInt(foo[i].toString());
        }
        return totalWords;
    }

    /**
     * Returns the number of unique words in the original file.
     * 
     * @return number of unique words.
     */
    public int getUniqueWordsOriginal() {

        int uniqueWords = 0;
        uniqueWords = originalMap.size();
        return uniqueWords;
    }

    /**
     * Returns the number of unique words in the corrected file.
     * 
     * @return number of unique words.
     */
    public int getUniqueWordsCorrected() {

        int uniqueWords = 0;
        uniqueWords = correctedMap.size();
        return uniqueWords;
    }

    /**
     * Returns the number of words that have been corrected in the corrected
     * file.
     * 
     * @return number of corrected words.
     */
    public int getNumberOfCorrections() {

        int numberOfCorrections = 0;
        for (int i = 0; i < correctedDataByLine.length; i++) {
            String[] originalWords = originalDataByLine[i].split("\\s");
            String[] correctedWords = correctedDataByLine[i].split("\\s");

            for (int j = 0; j < correctedWords.length; j++) {
                if (j < originalWords.length && j < correctedWords.length) {
                    if (!originalWords[j].equalsIgnoreCase(correctedWords[j])) {
                        numberOfCorrections++;
                    }
                }
            }
        }
        return numberOfCorrections;
    }

    /**
     * Returns the number of lines that have been corrected in the corrected
     * file.
     * 
     * @return number of corrected lines.
     */
    public int getNumberOfLinesCorrected() {

        int numberOfLinesCorrections = 0;
        for (int i = 0; i < correctedDataByLine.length; i++) {
            if (!originalDataByLine[i].equalsIgnoreCase(correctedDataByLine[i])) {
                numberOfLinesCorrections++;
            }
        }
        return numberOfLinesCorrections;
    }

    /**
     * Returns the overall percentage of the words that have been corrected.
     * Calculated as changed words divided by total words.
     * 
     * @return % change.
     */
    public double getPercentageOfWordsCorrected() {

        double percentageCorrected = 100 - ((double) correctedMap.size() / (double) originalMap.size()) * 100;

        return percentageCorrected;
    }

    /**
     * Builds a hashmap of words and their count.
     * 
     * @return
     */
    private HashMap<String, Integer> buildMapping(final String[] stringInput) {

        HashMap<String, Integer> words = new HashMap<String, Integer>();
        for (int i = 0; i < stringInput.length; i++) {
            stringInput[i] = stringInput[i].replaceAll("[\"\\(\\)\\d]", "");
            String[] lineWords = stringInput[i].split("\\s");

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
     * Gets and prints the most common words from the input string array.
     *
     * @param stringInput the string input
     * @return Most common words, sorted.
     */
    public HashMap<String, Integer> getMapOfMostCommonWords(final String[] stringInput) {

        HashMap<String, Integer> map = buildMapping(stringInput);
        LinkedHashMap<String, Integer> sortedMap = sortHashMapByValuesD(map);

        Iterator<?> it = sortedMap.entrySet().iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        return sortedMap;
    }

    /**
     * Sort hash map by values d.
     *
     * @param passedMap the passed map
     * @return the linked hash map
     */
    public LinkedHashMap<String, Integer> sortHashMapByValuesD(final HashMap<String, Integer> passedMap) {

        List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<Integer>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

        Iterator<?> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String) key, (Integer) val);
                    break;
                }

            }

        }
        return sortedMap;
    }
}

package uk.ac.standrews.cs.usp.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.standrews.cs.usp.tools.Utils;

import com.google.common.base.Charsets;

/**
 * Class built to analyse the consistency of the human coders.
 * @author jkc25
 *
 */
public class HumanCodingAnalyser {

    private File inputFile;
    private HashMap<String, HashMap<String, Integer>> inputMap;

    /**
     * Constructs a {@link HumanCodingAnalyser} class witht the given input file.
     * @param inputFile File to analyse.
     */
    public HumanCodingAnalyser(final File inputFile) {

        this.inputFile = inputFile;
        this.inputMap = new HashMap<String, HashMap<String, Integer>>();
        this.inputMap = populateMap(this.inputFile, this.inputMap);
        // printContents();

    }

    /**
     * Returns the inputMap that contains the inputs mapped to the varying output classes.
     * @return input map <input class <outputclass, count>>
     */
    public HashMap<String, HashMap<String, Integer>> getInputMap() {

        return inputMap;
    }

    /**
     * Populates the inputMap. Format of input file must be
     * Class \t Content \t id
     * 
     * Every unique string is stored with a list of classes that they are coded to.
     * @param inputFile file containing the data in the above format.
     * @return a populated hashMap of unique input strings a counts of each output code
     */
    private HashMap<String, HashMap<String, Integer>> populateMap(final File inputFile, final HashMap<String, HashMap<String, Integer>> inputMap) {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), Charsets.UTF_8));
            String line = "";
            String classification;
            String content;

            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split("\t");
                classification = lineSplit[0];
                content = lineSplit[1];

                // The content is either unique or not. I
                // If the content is unique then we need to add to inputMap
                // and create a hashmap with classification and the number 1
                // if not unique then we need to look up to see if it's been classified to this class before:
                // if yes the increment class count, if no create new entry.
                if (inputMap.containsKey(content)) {
                    HashMap<String, Integer> classMap = inputMap.get(content);

                    if (classMap.containsKey(classification)) {
                        classMap.put(classification, classMap.get(classification) + 1);
                    }
                    else {
                        classMap.put(classification, 1);
                    }
                    inputMap.put(content, classMap);
                }
                else {

                    HashMap<String, Integer> newMap = new HashMap<String, Integer>();
                    newMap.put(classification, 1);
                    inputMap.put(content, newMap);
                }
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return inputMap;
    }

    /**
     * Iterates through the mapping of inputs to codes and prints them to the console
     * and writes them to HumanCodingAnalysis.txt.
     */
    public void printContents() {

        Set<Entry<String, HashMap<String, Integer>>> inputSet = inputMap.entrySet();
        Iterator<Entry<String, HashMap<String, Integer>>> it = inputSet.iterator();
        StringBuilder sb = new StringBuilder();

        while (it.hasNext()) {
            Entry<String, HashMap<String, Integer>> element = it.next();
            sb.append(element.getKey() + "\t ");

            Iterator<Entry<String, Integer>> innerIterator = Utils.sortByValueDescending(element.getValue()).entrySet().iterator();

            while (innerIterator.hasNext()) {
                Entry<String, Integer> e = innerIterator.next();
                sb.append(e.getKey() + "\t " + e.getValue() + " \t");
            }

            sb.append("\n");
        }
        Utils.writeToFile(sb.toString(), "HumanCodingAnalysis.txt");

    }

    /**
     * Main class. Runs the analysis on the supplied input file. Please supply input file as argument 0.
     * @param args path/to/file to analyze
     */
    public static void main(final String[] args) {

        File inputFile = new File(args[0]);
        HumanCodingAnalyser hca = new HumanCodingAnalyser(inputFile);
        hca.printContents();
    }

}

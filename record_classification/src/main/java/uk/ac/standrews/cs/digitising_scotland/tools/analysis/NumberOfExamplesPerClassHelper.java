package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class NumberOfExamplesPerClassHelper {

    /**
     * @param args
     */
    public static void main(final String[] args) {

        String baseFolder = "/Users/jkc25/Documents/OccupationData/outputDirectorySetup_20_Output8/";
        String splitOutputTesting = baseFolder + "splitoutputtesting.txt";
        String splitOutputTraining = baseFolder + "splitoutputtraining.txt";

        NumberOfExamplesPerClassHelper n = new NumberOfExamplesPerClassHelper();

        HashMap<String, Integer> testingMap = n.getClassCount(new File(splitOutputTesting));
        System.out.println("/////");
        HashMap<String, Integer> trainingMap = n.getClassCount(new File(splitOutputTraining));

        getComparison(testingMap, trainingMap);

        testingMap = n.getWordCount(new File(splitOutputTesting));
        // n.printMap(testingMap);
        System.out.println("/////");
        trainingMap = n.getWordCount(new File(splitOutputTraining));
        // n.printMap(trainingMap);
        System.out.println("/////");

        getComparison(testingMap, trainingMap);

    }

    private static void getComparison(final HashMap<String, Integer> testingMap, final HashMap<String, Integer> trainingMap) {

        Iterator<Entry<String, Integer>> it = testingMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Integer> pairs = it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue() + " / " + trainingMap.get(pairs.getKey()));
        }

    }

    /**
     * Reads a split testing or training file where the format of the content is
     * 'Key: xxxxx: Value: xxxx'
     * and returns how many strings are in each class.
     * @param input input file, either a splitTesting or splitTraining file
     * @return Map<String, Integer> Class name with number of files
     */
    public HashMap<String, Integer> getClassCount(final File input) {

        BufferedReader reader = null;
        HashMap<String, Integer> countMapping = new HashMap<String, Integer>();

        ClassCounter classCounter = new ClassCounter(input);
        classCounter.count();

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF8"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split(":");
                String key = lineSplit[1];
                String classification = key.split("/")[1];

                if (countMapping.get(classification) == null) {
                    countMapping.put(classification, 1);
                }
                else {
                    countMapping.put(classification, countMapping.get(classification) + 1);
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            closeReader(reader);
        }

        return countMapping;
    }

    private void closeReader(final Reader reader) {

        try {
            if (reader != null) {
                reader.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the map to the console.
     * @param mapToPrint the map we want to print
     */
    public static void printMap(final Map<?, ?> mapToPrint) {

        Iterator it = mapToPrint.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            // it.remove(); // avoids a ConcurrentModificationException
        }
    }

    /**
     * Reads a split testing or training file where the format of the content is
     * 'Key: xxxxx: Value: xxxx'
     * and returns how many strings are in each class.
     * @param input input file, either a splitTesting or splitTraining file
     * @return Map<String, Integer> Class name with number of files
     */
    public HashMap<String, Integer> getWordCount(final File input) {

        HashMap<String, Integer> countMapping = new HashMap<String, Integer>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF8"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(":");
                String value = lineSplit[3];

                Reader reader = new StringReader(value);

                Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);

                Collection<String> words = new ArrayList<String>();
                getWords(analyzer, words, reader);
                for (int j = 0; j < words.size(); j++) {

                    if (countMapping.get(words.toArray()[j]) == null) {
                        countMapping.put((String) words.toArray()[j], 1);
                    }
                    else {
                        countMapping.put((String) words.toArray()[j], countMapping.get(words.toArray()[j]) + 1);
                    }

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
            closeReader(br);
        }

        return countMapping;
    }

    /**
     * Count words in a stream that the Reader contains. All strings are passed through a Lucene Analyzer which removes puncuation etc.
     * Exact behaviour is determined by the type of analzyer passed in.
     *
     * @param analyzer the analyzer
     * @param words the words
     * @param in the in
     * @return words Collection<String> of all the words in the token strea that have not been removed by the analyzer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Collection<String> getWords(final Analyzer analyzer, final Collection<String> words, final Reader in) throws IOException {

        TokenStream ts = analyzer.reusableTokenStream("text", in);
        ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            String s = ts.getAttribute(CharTermAttribute.class).toString();
            words.add(s);
        }
        return words;
    }
}

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberOfExamplesPerClassHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberOfExamplesPerClassHelper.class);

    /**
     * @param args
     */
    public static void main(final String[] args) {

        String baseFolder = "/Users/jkc25/Documents/OccupationData/outputDirectorySetup_20_Output8/";
        String splitOutputTesting = baseFolder + "splitoutputtesting.txt";
        String splitOutputTraining = baseFolder + "splitoutputtraining.txt";

        NumberOfExamplesPerClassHelper n = new NumberOfExamplesPerClassHelper();

        Map<String, Integer> testingMap = n.getClassCount(new File(splitOutputTesting));
        LOGGER.info("/////");
        Map<String, Integer> trainingMap = n.getClassCount(new File(splitOutputTraining));

        getComparison(testingMap, trainingMap);

        testingMap = n.getWordCount(new File(splitOutputTesting));
        LOGGER.info("/////");
        trainingMap = n.getWordCount(new File(splitOutputTraining));
        LOGGER.info("/////");

        getComparison(testingMap, trainingMap);

    }

    private static void getComparison(final Map<String, Integer> testingMap, final Map<String, Integer> trainingMap) {

        Iterator<Entry<String, Integer>> it = testingMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Integer> pairs = it.next();
            LOGGER.info(pairs.getKey() + " = " + pairs.getValue() + " / " + trainingMap.get(pairs.getKey()));
        }

    }

    /**
     * Reads a split testing or training file where the format of the content is
     * 'Key: xxxxx: Value: xxxx'
     * and returns how many strings are in each class.
     * @param input input file, either a splitTesting or splitTraining file
     * @return Map<String, Integer> Class name with number of files
     */
    public Map<String, Integer> getClassCount(final File input) {

        BufferedReader reader = null;
        Map<String, Integer> countMapping = new HashMap<String, Integer>();

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
            LOGGER.error(e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e.getCause());
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
            LOGGER.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * Prints the map to the console.
     * @param mapToPrint the map we want to print
     */
    public static void printMap(final Map<?, ?> mapToPrint) {

        Iterator<?> it = mapToPrint.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            LOGGER.info(pairs.getKey() + " = " + pairs.getValue());
        }
    }

    /**
     * Reads a split testing or training file where the format of the content is
     * 'Key: xxxxx: Value: xxxx'
     * and returns how many strings are in each class.
     * @param input input file, either a splitTesting or splitTraining file
     * @return Map<String, Integer> Class name with number of files
     */
    public Map<String, Integer> getWordCount(final File input) {

        Map<String, Integer> countMapping = new HashMap<String, Integer>();
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
            LOGGER.error(e.getMessage(), e.getCause());
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e.getCause());
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
     * @param reader the in
     * @return words Collection<String> of all the words in the token strea that have not been removed by the analyzer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Collection<String> getWords(final Analyzer analyzer, final Collection<String> words, final Reader reader) throws IOException {

        TokenStream ts = analyzer.reusableTokenStream("text", reader);
        ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            String s = ts.getAttribute(CharTermAttribute.class).toString();
            words.add(s);
        }
        return words;
    }
}

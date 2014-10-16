package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Ad hoc class to read old output files and calculate the number of unique records and the corresponding accuracy values.
 * @author jkc25
 *
 */
public class UniqueRecordAnalyser {

    static Logger LOGGER = LoggerFactory.getLogger(UniqueRecordAnalyser.class);
    static int descriptionPos;
    static int correctPos;
    static int nbPos;
    static int sgdPos;
    static int stringSimPos;

    public static void main(final String[] args) throws IOException {

        File input = new File(args[0]);
        if (!input.exists()) {
            LOGGER.error("File (" + input.getAbsolutePath() + ")does not exsist");
        }

        init(args);

        if (input.isDirectory()) {
            runOnAllFiles(input);
        }
        else {
            UniqueRecordAnalyser instance = new UniqueRecordAnalyser();
            instance.run(input);
        }

    }

    private static void init(final String[] args) {

        descriptionPos = Integer.parseInt(args[1]);
        correctPos = Integer.parseInt(args[2]);
        nbPos = Integer.parseInt(args[3]);
        sgdPos = Integer.parseInt(args[4]);
        stringSimPos = Integer.parseInt(args[5]);
    }

    private static void runOnAllFiles(final File input) throws IOException {

        UniqueRecordAnalyser instance = new UniqueRecordAnalyser();
        File[] files = input.listFiles();
        for (File file : files) {
            instance.run(file);
        }
    }

    public void run(final File input) throws IOException {

        Map<String, String> uniqueMap = new HashMap<>();
        uniqueMap = populateMap(input);
        System.out.println(uniqueMap.size());
        processMap(uniqueMap);
    }

    private void processMap(final Map<String, String> uniqueMap) {

        double total = uniqueMap.size();
        double nbCorrect = 0;
        double sgdCorrect = 0;
        double stringCorrect = 0;
        String nb = "";
        String sgd = "";
        String sim = "";

        Iterator<Entry<String, String>> it = uniqueMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> next = it.next();
            String[] values = next.getValue().split(Utils.getCSVComma());
            String correct = values[correctPos].trim().toLowerCase();
            nb = getValue(values, nbPos);
            sgd = getValue(values, sgdPos);
            sim = getValue(values, stringSimPos);

            if (!nb.equals("") && correct.equals(nb)) {
                nbCorrect++;
            }
            if (!sgd.equals("") && correct.equals(sgd)) {
                sgdCorrect++;
            }
            if (!sim.equals("") && correct.equals(sim)) {
                stringCorrect++;
            }

        }
        LOGGER.info("Total unique records: " + total);
        LOGGER.info("Total nb correct: " + nbCorrect + " (" + nbCorrect / total * 100 + ")");
        LOGGER.info("Total sgd correct: " + sgdCorrect + " (" + sgdCorrect / total * 100 + ")");
        LOGGER.info("Total string sim correct: " + stringCorrect + " (" + stringCorrect / total * 100 + ")");

    }

    private String getValue(final String[] values, final int pos) {

        String string = "";
        if (pos != -1) {
            string = values[pos].trim().toLowerCase();
        }
        return string;
    }

    private Map<String, String> populateMap(final File input) throws IOException {

        BufferedReader br = ReaderWriterFactory.createBufferedReader(input);
        String line = "";
        Map<String, String> uniqueMap = new HashMap<>();
        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split(Utils.getCSVComma());
            String description = lineSplit[0];
            uniqueMap.put(description, line);
        }
        return uniqueMap;
    }
}

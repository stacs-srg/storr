package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Reads an input file, shuffles the output classes to remove any connection between inputs and outputs and writes the new
 * input/output pairs to file.
 * 
 * Based on feedback for Amsterdam Classification paper.
 * @author jkc25
 *
 */
public class OutputClassShuffler {

    private File inputFile;

    /**
     * Creates a new OutputClassShuffler.
     * @param inputFile file to shuffle.
     */
    public OutputClassShuffler(final File inputFile) {

        this.inputFile = inputFile;
    }

    /**
     * Reads the input file passed into the constructor and writes to the output file passed in.
     * Shuffles classifications around so that there is no link between classification and words in document.
     * @param outputFile file to write shuffled dataset to.
     * @return shuffled data.
     * @throws IOException if io error
     */
    public File shuffleAndWriteToFile(final File outputFile) throws IOException {

        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);

        String line = "";
        List<String> cod = new ArrayList<String>();
        List<String> classification = new ArrayList<String>();
        List<String> id = new ArrayList<String>();

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split("\t");

            cod.add(lineSplit[1]);
            classification.add(lineSplit[0]);
            id.add(lineSplit[2]);

        }
        br.close();
        Collections.shuffle(classification);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cod.size(); i++) {
            sb.append(classification.get(i) + "\t" + cod.get(i) + "\t" + id.get(i) + "\n");
        }

        Utils.writeToFile(sb.toString(), outputFile.getAbsolutePath());
        return outputFile;
    }

    /**
     * Main method. Runs the {@link OutputClassShuffler} with which ever file is passed in as arg[0]
     * @param args file to shuffle
     * @throws IOException I/O Error
     */
    public static void main(final String[] args) throws IOException {

        // args[] = "hiscoNoVar.txt";
        OutputClassShuffler o = new OutputClassShuffler(new File(args[0]));
        o.shuffleAndWriteToFile(new File("baselineHiscoNoVar.txt"));
    }
}

package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing.TokenStreamIterator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing.TokenStreamIteratorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * The Class UniqueWordCounter.
 */
public final class UniqueWordCounter {

    /** The Constant LUCENE_VERSION. */
    static final Version LUCENE_VERSION = Version.LUCENE_36;

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(final String[] args) throws IOException {

        if (!new File(args[0]).exists()) {
            System.err.println("Argument 0 must be a path to a file and it must be accessible to the program");
        }

        getNumberOfUniqueWords(new File(args[0]));
    }

    /**
     * Instantiates a new unique word counter.
     */
    private UniqueWordCounter() {

        //utility class - private constructor
    }

    /**
     * Passes each line of a document through an {@link Analyzer} to remove stop words then counts the total number of unique words in that file.
     * This result is then written to file.
     * @param inputFile File to count unique words of.
     * @return number of unique words.
     * @throws IOException io error.
     */
    public static int getNumberOfUniqueWords(final File inputFile) throws IOException {

        Multiset<String> wordMultiset = HashMultiset.create();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));

        String line = "";
        while ((line = br.readLine()) != null) {
            wordMultiset = countWordsInLine(wordMultiset, line);
        }

        StringBuilder sb = new StringBuilder();
        for (String string : wordMultiset.elementSet()) {
            sb.append(string + "\t" + wordMultiset.count(string) + "\n");
        }
        Utils.writeToFile(sb.toString(), "target/wordCounts.txt");
        br.close();

        return 0;
    }

    /**
     * Counts the number of words in a string.
     *
     * @param wordMultiset the word multiset to add words to. This keeps track of how many time each word appears.
     * @param line the line to count. Line is split into tokens and these are added to the multiset.
     * @return the multiset with new words added
     */
    public static Multiset<String> countWordsInLine(final Multiset<String> wordMultiset, final String line) {

        TokenStreamIterator<CharTermAttribute> ts = TokenStreamIteratorFactory.newTokenStreamIterator(line);
        while (ts.hasNext()) {
            wordMultiset.add(ts.next().toString().trim().toLowerCase());
        }
        return wordMultiset;
    }
}

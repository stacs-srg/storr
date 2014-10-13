package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;
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
        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);

        String line = "";
        while ((line = br.readLine()) != null) {
            wordMultiset = countWordsInLine(wordMultiset, new TokenSet(line));
        }

        StringBuilder sb = new StringBuilder();
        for (String string : wordMultiset.elementSet()) {
            sb.append(string).append("\t").append(wordMultiset.count(string)).append("\n");
        }
        Utils.writeToFile(sb.toString(), "target/wordCounts.txt");
        br.close();

        return 0;
    }

    /**
     * Counts the number of words in a string.
     *
     * @param wordMultiset the word multiset to add words to. This keeps track of how many time each word appears.
     * @return the multiset with new words added
     */
    public static Multiset<String> countWordsInLine(final Multiset<String> wordMultiset, final TokenSet tokenSet) {

        for (String token : tokenSet) {
            wordMultiset.add(token.trim().toLowerCase());
        }
        return wordMultiset;
    }
}

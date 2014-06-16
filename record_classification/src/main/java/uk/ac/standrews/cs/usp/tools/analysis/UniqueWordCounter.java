package uk.ac.standrews.cs.usp.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import uk.ac.standrews.cs.usp.tools.Utils;

import com.google.common.base.Charsets;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.io.Files;

// TODO: Auto-generated Javadoc
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

        if (!args[1].trim().toLowerCase().equals("true") || !args[1].trim().toLowerCase().equals("false")) {
            System.err.println("Argument 1 must be a boolean argument, true to write to file, false if not.");
        }

        getNumberOfUniqueWords(new File(args[0]), Boolean.parseBoolean(args[1]));

    }

    private UniqueWordCounter() {

        //utility class - private constructor
    }

    /**
     * Passes each line of a document through an {@link Analyzer} to remove stop words then counts the total number of unique words in that file.
     * This result is then written to file.
     * @param inputFile File to count unique words of.
     * @param writeToFile File to write results to.
     * @return number of unique words.
     * @throws IOException io error.
     */
    public static int getNumberOfUniqueWords(final File inputFile, final boolean writeToFile) throws IOException {

        Analyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);
        HashMap<String, String> overallCounts = new HashMap<String, String>();
        Multiset<String> words = ConcurrentHashMultiset.create();

        BufferedReader reader = Files.newReader(inputFile, Charsets.UTF_8);

        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            Reader in = new StringReader(line.split("\t")[1]);
            if (writeToFile) {
                writeRealWordsToFile("kilmshrunkTest.txt", line, analyzer, words, in, overallCounts);
            }
            else {
                countWords(analyzer, words, in, overallCounts);

            }
            line = reader.readLine();
        }
        System.out.println(inputFile.getName() + " " + overallCounts.size());
        return overallCounts.size();
    }

    /**
     * Count words.
     *
     * @param analyzer the analyzer
     * @param words the words
     * @param in the in
     * @param overallCounts the overall counts
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void countWords(final Analyzer analyzer, final Collection<String> words, final Reader in, final HashMap<String, String> overallCounts) throws IOException {

        TokenStream ts = analyzer.reusableTokenStream("text", in);
        ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            String s = ts.getAttribute(CharTermAttribute.class).toString();
            words.add(s);
            overallCounts.put(s, s);
        }

    }

    /**
     * Writes the real words to a file.
     *
     * @param outputFileName the output file name
     * @param line the line
     * @param analyzer the analyzer
     * @param words the words
     * @param in the in
     * @param overallCounts the overall counts
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void writeRealWordsToFile(final String outputFileName, final String line, final Analyzer analyzer, final Collection<String> words, final Reader in, final HashMap<String, String> overallCounts) throws IOException {

        TokenStream ts = analyzer.reusableTokenStream("text", in);
        ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        StringBuilder sb = new StringBuilder();
        sb.append(line.split("\t")[0] + "\t");
        while (ts.incrementToken()) {
            String s = ts.getAttribute(CharTermAttribute.class).toString();
            words.add(s);
            overallCounts.put(s, s);
            sb.append(s + " ");
        }
        sb.append("\t" + line.split("\t")[2] + "\n");

        Utils.writeToFile(sb.toString(), outputFileName, true);

    }
}

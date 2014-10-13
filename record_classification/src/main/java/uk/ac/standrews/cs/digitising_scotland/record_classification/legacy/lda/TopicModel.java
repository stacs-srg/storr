package uk.ac.standrews.cs.digitising_scotland.record_classification.legacy.lda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.analysis.MemoryMonitor;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

/**
 * This class contains methods to build a Latent Dirichlet Allocation (LDA - see http://machinelearning.wustl.edu/mlpapers/paper_files/BleiNJ03.pdf for more details)
 * on a dataset and tags each tokenized word in the output with a topic number.
 * 
 * Please remember that LDA is a generative model and as such is not guaranteed to give the same allocation of words/topics each time it is run.
 */
public class TopicModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicModel.class);
    static final int ROWS = 4;
    static final int COLS = 10;

    /** The results matrix - for debug/information only. Will show summary of how many topics were assigned/should of been assigned to each record */
    private static int[][] resultsMatrix = new int[ROWS][COLS];

    /**
     * Processes the input file and returns an output file.
     * This class runs the LDA algorimth on the input and tags the output.
     *
     * @param input the input
     * @return the file
     */
    public File process(final File input) {

        File output = new File("target/ldaTagged.txt");
        output = tagInputs(input, output);

        return output;
    }

    /**
     * Tag inputs.
     *
     * @param input the input
     * @param output the output
     * @return the file
     */
    private File tagInputs(final File input, final File output) {

        initResultsMatrix();

        File outputFile = modelTopics(input, output);

        dumpResultsMatrix();

        return outputFile;
    }

    /**
     * Information/debug only.
     */
    private void dumpResultsMatrix() {

        LOGGER.info("LDA with Topics set to " + Integer.parseInt(MachineLearningConfiguration.getDefaultProperties().getProperty("lda.numTopics")));
        LOGGER.info("lda \t 1 \t 2 \t 3 \t 4 \t 5 \t 6 \t 7 \t8 \t 9 \t 10");

        for (int i = 0; i < resultsMatrix.length; i++) {
            for (int j = 0; j < resultsMatrix[i].length; j++) {
                if (j == 0) {
                    System.out.print((i + 1) + "\t");
                }
                System.out.print(resultsMatrix[i][j] + "\t");
            }
            System.out.print("\n");
        }
    }

    private File modelTopics(final File input, final File output) {

        File outputFile = null;
        try {
            outputFile = modelTopics(input.getAbsolutePath(), output.getAbsolutePath());
        }
        catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
        catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
        return outputFile;
    }

    private void initResultsMatrix() {

        for (int i = 0; i < resultsMatrix.length; i++) {
            for (int j = 0; j < resultsMatrix[i].length; j++) {
                resultsMatrix[i][j] = 0;
            }
        }
    }

    /**
     * Model topics with the LDA algorithm.
     * 
     *  Create a model with X topics, alpha_t = 0.01, beta_w = 0.01. The number of topics are set in the properties file.
     *  Note that the first parameter is passed as the sum over topics, while
     *  the second is the parameter for a single dimension of the Dirichlet prior.
     *
     * @param inputFileName the input file name
     * @param outputFileName the output file name
     * @return file with tagged input strings
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private File modelTopics(final String inputFileName, final String outputFileName) throws IOException {

        //MemoryMonitor monitor = new MemoryMonitor();
        // monitorMemoryStart(monitor);
        Timer timer = new Timer();
        timer.start();

        File inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);
        int numberOfLines = Utils.getNumberOfLines(inputFile);

        List<Pipe> pipeList = initPipeList();

        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        Reader fileReader = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
        // data, label, name fields
        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("(^[^\t]*)(.*)"), 2, 0, 0));

        int numTopics = Integer.parseInt(MachineLearningConfiguration.getDefaultProperties().getProperty("lda.numTopics"));

        final double beta = 0.01;
        final double alphaSum = 1.0;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, alphaSum, beta);

        model.addInstances(instances);

        // Use x parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(1);

        model.setNumIterations(Integer.parseInt(MachineLearningConfiguration.getDefaultProperties().getProperty("lda.iterations")));

        model.estimate();

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();

        readAndTagInput(inputFile, outputFile, model, dataAlphabet);

        // Estimate the topic distribution of the first instance,
        //  given the current Gibbs state.
        double[] topicDistribution = model.getTopicProbabilities(0);

        // Get an array of sorted sets of word ID/count pairs
        List<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        // Show top 5 words in topics with proportions for the first document
        printTop5Words(numTopics, dataAlphabet, topicDistribution, topicSortedWords);

        // Create a new instance with high probability of topic 0
        StringBuilder topicZeroText = new StringBuilder();
        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

        int rank = 0;
        final int rankLimit = 5;
        while (iterator.hasNext() && rank < rankLimit) {
            IDSorter idCountPair = iterator.next();
            topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
            rank++;
        }

        // Create a new instance named "test instance" with empty target and source fields.
        InstanceList testing = new InstanceList(instances.getPipe());
        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

        writeModel(model, testing);

        // monitorMemoryStop(monitor);
        timer.stop();

        writeExecutionData(timer, numberOfLines);

        return outputFile;
    }

    private void writeModel(final ParallelTopicModel model, final InstanceList testing) {

        TopicInferencer inferencer = model.getInferencer();
        final int numIterations = 10;
        final int thinning = 1;
        final int burnIn = 5;
        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), numIterations, thinning, burnIn);
        LOGGER.info("0\t" + testProbabilities[0]);
        model.write(new File("target/ldaModel.model"));
    }

    private void writeExecutionData(final Timer timer, final int numberOfLines) {

        final int convertingFactor = 1000000000;
        String data = numberOfLines + " \t" + timer.elapsedTime() + "\t" + (timer.elapsedTime() / convertingFactor) + "\n";
        Utils.writeToFile(data, "target/LDAExecutionTimes.txt", true);
    }

    private void printTop5Words(final int numTopics, final Alphabet dataAlphabet, final double[] topicDistribution, final List<TreeSet<IDSorter>> topicSortedWords) {

        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

            out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
            int rank = 0;
            final int rankLimit = 5;
            while (iterator.hasNext() && rank < rankLimit) {
                IDSorter idCountPair = iterator.next();
                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            LOGGER.info(out.toString());
        }
        out.close();
    }

    private void readAndTagInput(final File inputFile, final File outputFile, final ParallelTopicModel model, final Alphabet dataAlphabet) throws IOException {

        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        final int topicsToPrint = 10;
        for (int i = 0; i < topicsToPrint; i++) {
            Formatter out = new Formatter(new StringBuilder(), Locale.US);
            for (int position = 0; position < tokens.getLength(); position++) {
                out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
            }
            LOGGER.info(out.toString());
            out.close();
        }

        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);

        String line = "";
        for (int i = 0; i < model.getData().size(); i++) {
            line = br.readLine();

            if (line != null && !line.equals("")) {
                tokens = (FeatureSequence) model.getData().get(i).instance.getData();
                topics = model.getData().get(i).topicSequence;
                modelString(outputFile, line, dataAlphabet, tokens, topics);
            }
        }
        closeReader(br);
    }

    private List<Pipe> initPipeList() {

        // Begin by importing documents from text to feature sequences
        List<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add(new CharSequenceLowercase());
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));

        pipeList.add(new TokenSequenceRemoveStopwords(new File(getClass().getResource("/stoplists/en.txt").getFile()), "UTF-8", true, false, false));
        pipeList.add(new TokenSequence2FeatureSequence());
        return pipeList;
    }

    /**
     * Model string.
     *
     * @param outputFile the output file
     * @param line the line
     * @param dataAlphabet the data alphabet
     * @param tokens the tokens
     * @param topics the topics
     * @return the file
     */
    private static File modelString(final File outputFile, final String line, final Alphabet dataAlphabet, final FeatureSequence tokens, final LabelSequence topics) {

        int numberOfTopics = 0;
        HashMap<Integer, Integer> topicNumbers = new HashMap<Integer, Integer>();

        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        String[] lineSplit = line.split("\t");
        for (int position = 0; position < tokens.getLength(); position++) {
            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
            topicNumbers.put(topics.getIndexAtPosition(position), 1);
        }
        numberOfTopics = topicNumbers.size();
        LOGGER.info(out.toString());
        String contents = lineSplit[0] + "\t" + lineSplit[1] + "\t" + lineSplit[2] + "\t" + out.toString() + "\t" + numberOfTopics + "\n";
        Utils.writeToFile(contents, outputFile.getAbsolutePath(), true);
        out.close();
        return outputFile;
    }

    /**
     * Closes a reader.
     *
     * @param bufferedReader the bufferedReader to close
     */
    private static void closeReader(final BufferedReader bufferedReader) {

        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            }
            catch (IOException e) {
                LOGGER.error(e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * Monitor memory start.
     *
     * @param m the m
     */
    private void monitorMemoryStart(final MemoryMonitor m) {

        (new Thread(m)).start();

    }

    /**
     * Monitor memory stop.
     *
     * @param m the m
     */
    private void monitorMemoryStop(final MemoryMonitor m) {

        m.stop();

    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(final String[] args) throws Exception {

        if (args[0].length() != 1) {
            System.err.println("Please supply a file to model. File format should be:  class \tcontent\tID");
        }
        TopicModel tm = new TopicModel();

        tm.process(new File(args[0]));

    }

}

package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

/**
 * Distributes training vectors across {@link OLRPool}s in a cross fold manner. Allows concurrent training
 * of the {@link OLRPool}s and provides a classify method that averages the classifications given by each pool.
 *
 * @author fraserdunlop, jkc25
 */
public class OLRCrossFold {

    /** The Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OLRCrossFold.class);

    /** The isModelTrainable flag. */
    private final boolean modelTrainable;

    /** The OLRPool models. */
    private ArrayList<OLRPool> models = new ArrayList<>();

    /** The number of cross folds. */
    private int folds;

    /** The properties. */
    private Properties properties;

    /** The classifier. */
    private OLR classifier;

    /**
     * Gets the average running log likelihood.
     *
     * @return the average running log likelihood
     */
    public double getAverageRunningLogLikelihood() {

        double ll = 0.;
        for (OLRPool model : models) {
            ll += model.getAverageRunningLogLikelihood();
        }
        ll /= models.size();
        return ll;
    }

    /**
     * Gets the number of records used for training so far across all the models in the pool.
     * @return int the number of training records used so far
     */
    public long getNumTrained() {

        long numTrained = 0;
        for (OLRPool model : models) {
            numTrained += model.getNumTrained();
        }
        return numTrained;
    }

    /**
     * Reset running log likelihoods.
     */
    public void resetRunningLogLikelihoods() {

        for (OLRPool model : models) {
            model.resetRunningLogLikelihoods();
        }
    }

    /**
     * Constructor.
     *
     * @param trainingVectorList training vectors
     * @param properties         properties
     */
    public OLRCrossFold(final ArrayList<NamedVector> trainingVectorList, final Properties properties) {

        this.properties = properties;
        getConfigOptions();
        ArrayList<NamedVector>[][] trainingVectors = CrossFoldedDataStructure.make(trainingVectorList, folds);
        for (int i = 0; i < this.folds; i++) {
            OLRPool model = new OLRPool(properties, trainingVectors[i][0], trainingVectors[i][1]);
            models.add(model);
        }
        modelTrainable = true;
    }

    /**
     * Stops training on all models in the {@link OLRPool}.
     */
    public void stop() {

        for (OLRPool model : models) {
            model.stop();
        }
    }

    /**
     * trains models.
     */
    public void train() {

        checkTrainable();
        trainIfPossible();
    }

    /**
     * Train all models.
     *
     * @throws InterruptedException the interrupted exception
     */
    private void trainAllModels() throws InterruptedException {

        StopListener stopListener = new StopListener();
        ExecutorService stopService = Executors.newFixedThreadPool(1);
        ExecutorService executorService = Executors.newFixedThreadPool(folds);
        stopService.submit(stopListener);
        for (OLRPool model : models) {
            executorService.submit(model);
        }
        final int timeout = 365;
        executorService.awaitTermination(timeout, TimeUnit.DAYS);
        executorService.shutdown();
        stopListener.terminateProcess();
        stopService.shutdown();
        prepareClassifier();
    }

    /**
     * Prepare classifier.
     */
    private void prepareClassifier() {

        List<OLRShuffled> survivors = getSurvivors();
        Matrix classifierMatrix = getClassifierMatrix(survivors);
        classifier = new OLR(classifierMatrix);
    }

    /**
     * Gets the survivors.
     *
     * @return the survivors
     */
    private List<OLRShuffled> getSurvivors() {

        List<OLRShuffled> survivors = new ArrayList<>();
        for (OLRPool model : models) {
            survivors.addAll(model.getSurvivors());
        }
        return survivors;
    }

    /**
     * Gets the classifier matrix.
     *
     * @param survivors the survivors
     * @return the classifier matrix
     */
    private Matrix getClassifierMatrix(final List<OLRShuffled> survivors) {

        Stack<Matrix> matrices = new Stack<>();
        for (OLRShuffled model : survivors) {
            matrices.add(model.getBeta());
        }
        Matrix classifierMatrix = matrices.pop();
        while (!matrices.empty()) {
            classifierMatrix.plus(matrices.pop());
        }
        classifierMatrix.divide(survivors.size());
        return classifierMatrix;
    }

    /**
     * Trains all the models if it possible to do so. Models may be untrainable if the have be
     * read back from file. These models can still be used for classification.
     */
    private void trainIfPossible() {

        checkTrainable();
        try {
            this.trainAllModels();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a model is trainable. Models may be untrainable if the have be
     * read back from file. These models can still be used for classification.
     */
    private void checkTrainable() {

        if (!modelTrainable) { throw new UnsupportedOperationException("This model has no files to train " + "on and may only be used for classification."); }
    }

    /**
     * Classifies a vector.
     *
     * @param instance vector
     * @return vector encoding probability distribution over output classes
     */
    public Vector classifyFull(final Vector instance) {

        return classifier.classifyFull(instance);
    }

    /**
     * Gets the log likelihood averaged over the models in the pool.
     * @param actual the actual classification
     * @param instance the instance vector
     * @return log likelihood
     */
    public double logLikelihood(final int actual, final Vector instance) {

        return classifier.logLikelihood(actual, instance);
    }

    /**
     * Gets the configuration options.
     *
     * @return the . Models may be untrainable if the have be
     * read back from file. These models can still be used for classification. options
     */
    private void getConfigOptions() {

        folds = Integer.parseInt(properties.getProperty("OLRFolds"));
    }

    /**
     * Allows serialization of the model to file.
     *
     * @param filename name of file to serialize model to
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void serializeModel(final String filename) throws IOException {

        DataOutputStream out = OLR.getDataOutputStream(filename);
        write(out);
        out.close();
    }

    /**
     * Allows de-serialization of a model from a file. The de-serialized model is not trainable.
     *
     * @param filename name of file to de-serialize
     * @return the OLR cross fold
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static OLRCrossFold deSerializeModel(final String filename) throws IOException {

        DataInputStream in = OLR.getDataInputStream(filename);
        OLRCrossFold olrCrossFold = new OLRCrossFold();
        olrCrossFold.readFields(in);
        in.close();
        return olrCrossFold;
    }

    /**
     * Instantiates a new OLR crossfold model.
     */
    protected OLRCrossFold() {

        modelTrainable = false;
    }

    /**
     * Writes each model to a {@link DataOutputStream}.
     *
     * @param outputStream the out
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void write(final DataOutputStream outputStream) throws IOException {

        outputStream.writeInt(models.size());
        for (OLRPool model : models) {
            model.write(outputStream);
        }
        classifier.write(outputStream);
    }

    /**
     * Read fields from a {@link DataInputStream}.
     *
     * @param inputStream the inputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void readFields(final DataInputStream inputStream) throws IOException {

        int numModels = inputStream.readInt();
        for (int i = 0; i < numModels; i++) {
            OLRPool olrPool = new OLRPool();
            olrPool.readFields(inputStream);
            models.add(olrPool);
        }
        OLR olr = new OLR();
        olr.readFields(inputStream);
        classifier = olr;
    }

    /**
     * The listener interface for receiving stop events.
     * The class that is interested in processing a stop
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addStopListener<code> method. When
     * the stop event occurs, that object's appropriate
     * method is invoked.
     *
     */
    public class StopListener implements Runnable {

        /** The process terminated. */
        private boolean processTerminated;

        private static final String COUNT_COMMAND = "count";
        private static final String STOP_COMMAND = "stop";
        private static final String GET_LOG_LIK_COMMAND = "getloglik";
        private static final String RESET_LOG_LIK_COMMAND = "resetloglik";
        private static final String COUNT_MESSAGE = "\nNumber of record used in training so far (across all models): ";
        private static final String LOG_LIK_MESSAGE = "\nLog likelihood is: ";
        private static final String STOP_MESSAGE = "\nStop call detected. Stopping training...";
        private static final String RESET_MESSAGE = "\nResetting log likelihood.";
        private static final String INSTRUCTION_MESSAGE = "\n#######################--OLRCrossFold Commands--#################################" + "\n# \"" + STOP_COMMAND + "\" will halt the training process and skip straight to classification.\t#" + "\n# \"" + GET_LOG_LIK_COMMAND
                        + "\" will return the current running average log likelihood estimate.\t#" + "\n# \"" + RESET_LOG_LIK_COMMAND + "\" will reset the running average log likelihood statistic.\t#" + "\n# \"" + COUNT_COMMAND + "\" will return the number of records used in training so far.\t\t#"
                        + "\n#################################################################################";

        /**
         * Terminates the process. Sets the processTerminated flat to true and handles the thread shutdown.
         */
        public void terminateProcess() {

            stop();
            processTerminated = true;
        }

        /**
         * Initiates the command line stopListener. This waits for input from the command line and processes the input.
         */
        public void commandLineStopListener() {

            LOGGER.info(instructions());
            processTerminated = false;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in, FileManipulation.FILE_CHARSET))) {
                String line;
                while (true) {
                    line = in.readLine();
                    if (line != null && processInput(line)) {
                        break;
                    }
                }
                in.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Processes the input from the command line. Calls the correct Logger reaction on input and
         * returns true if a message is printed.
         *
         * @param line the line to process
         * @return true, if successful output is printed.
         * @throws InterruptedException the interrupted exception
         */
        private boolean processInput(final String line) throws InterruptedException {

            switch (line.toLowerCase()) {
                case STOP_COMMAND:
                    LOGGER.info(stopCalled());
                    break;
                case GET_LOG_LIK_COMMAND:
                    LOGGER.info(getLogLik());
                    break;
                case RESET_LOG_LIK_COMMAND:
                    LOGGER.info(resetLogLik());
                    break;
                case COUNT_COMMAND:
                    LOGGER.info(countCalled());
                    break;
                default:
                    LOGGER.info(instructions());
            }
            return line.equalsIgnoreCase(STOP_COMMAND) || processTerminated;
        }

        private String countCalled() {

            return COUNT_MESSAGE + Long.toString(getNumTrained());
        }

        /**
         * Calls the Stop method and returns the stop message.
         *
         * @return the string
         */
        private String stopCalled() {

            stop();
            return STOP_MESSAGE;
        }

        /**
         * Gets the log likelihood.
         *
         * @return the log likelihood
         */
        private String getLogLik() {

            return LOG_LIK_MESSAGE + Double.toString(getAverageRunningLogLikelihood());
        }

        /**
         * Reset log likelihood.
         *
         * @return the string
         * @throws InterruptedException the interrupted exception
         */
        private String resetLogLik() throws InterruptedException {

            final int threadSleepMillis = 10;
            String message = getLogLik() + RESET_MESSAGE;
            resetRunningLogLikelihoods();
            Thread.sleep(threadSleepMillis);
            return message + getLogLik();
        }

        /**
         * Returns the instructions.
         *
         * @return the string
         */
        private String instructions() {

            return INSTRUCTION_MESSAGE;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            commandLineStopListener();
        }
    }
}

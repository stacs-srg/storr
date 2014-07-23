package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR;

import java.io.*;
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
 * @author fraserdunlop
 */
public class OLRCrossFold {

    private static final Logger LOGGER = LoggerFactory.getLogger(OLRCrossFold.class);

    /**
     * The model trainable.
     */
    private final boolean modelTrainable;

    /**
     * The models.
     */
    private ArrayList<OLRPool> models = new ArrayList<>();

    /**
     * The folds.
     */
    private int folds;

    /**
     * The properties.
     */
    private Properties properties;
    private OLR classifier;


    public double getAverageRunningLogLikelihood() {
        double ll = 0.;
        for (OLRPool model : models) {
            ll += model.getAverageRunningLogLikelihood();
        }
        ll /= models.size();
        return ll;
    }

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
        executorService.shutdown();
        final int timeout = 365;
        executorService.awaitTermination(timeout, TimeUnit.DAYS);
        stopListener.terminateProcess();
        stopService.shutdown();
        prepareClassifier();
    }

    private void prepareClassifier() {
        List<OLRShuffled> survivors = getSurvivors();
        Matrix classifierMatrix = getClassifierMatrix(survivors);
        classifier = new OLR(classifierMatrix);
    }

    private List<OLRShuffled> getSurvivors() {
        List<OLRShuffled> survivors = new ArrayList<>();
        for (OLRPool model : models) {
            survivors.addAll(model.getSurvivors());
        }
        return survivors;
    }

    private Matrix getClassifierMatrix(List<OLRShuffled> survivors) {
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
     * Train if possible.
     */
    private void trainIfPossible() {
        checkTrainable();
        try {
            this.trainAllModels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check trainable.
     */
    private void checkTrainable() {
        if (!modelTrainable) {
            throw new UnsupportedOperationException("This model has no files to train " + "on and may only be used for classification.");
        }
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
     *
     * @param actual   the actual classification
     * @param instance the instance vector
     * @return log likelihood
     */
    public double logLikelihood(final int actual, final Vector instance) {
        return classifier.logLikelihood(actual, instance);
    }

    /**
     * Gets the config options.
     *
     * @return the config options
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
     * Instantiates a new OLR cross fold.
     */
    protected OLRCrossFold() {
        modelTrainable = false;
    }

    /**
     * Write.
     *
     * @param out the out
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void write(final DataOutputStream out) throws IOException {
        out.writeInt(models.size());
        for (OLRPool model : models) {
            model.write(out);
        }
        classifier.write(out);
    }

    /**
     * Read fields.
     *
     * @param in the in
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void readFields(final DataInputStream in) throws IOException {

        int numModels = in.readInt();
        for (int i = 0; i < numModels; i++) {
            OLRPool olrPool = new OLRPool();
            olrPool.readFields(in);
            models.add(olrPool);
        }
        OLR olr = new OLR();
        olr.readFields(in);
        classifier = olr;
    }

    public class StopListener implements Runnable {
        private boolean processTerminated;
        private final String stopCommand =
                "stop";
        private final String getLogLikCommand =
                "getloglik";
        private final String resetLogLikCommand =
                "resetloglik";
        private final String logLikelihoodMessage =
                "\nLog likelihood is: ";
        private final String stopMessage =
                "\nStop call detected. Stopping training...";
        private final String resetMessage =
                "\nResetting log likelihood.";
        private final String instructionMessage =
                "\n#######################--OLRCrossFold Commands--#################################" +
                "\n# \"" + stopCommand + "\" will halt the training process and skip straight to classification.\t#" +
                "\n# \"" + getLogLikCommand + "\" will return the current running average log likelihood estimate.\t#" +
                "\n# \"" + resetLogLikCommand + "\" will reset the running average log likelihood statistic.\t\t#" +
                "\n#################################################################################";


        public void terminateProcess() {
            stop();
            processTerminated = true;
        }

        public void commandLineStopListener() {
            LOGGER.info(instructions());
            processTerminated = false;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in, FileManipulation.FILE_CHARSET))) {
                String line;
                while (true) {
                    line = in.readLine();
                    if (processInput(line)) break;
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean processInput(String line) throws InterruptedException {
            switch (line.toLowerCase()) {
                case stopCommand:
                    LOGGER.info(stopCalled());
                    break;
                case getLogLikCommand:
                    LOGGER.info(getLogLik());
                    break;
                case resetLogLikCommand:
                    LOGGER.info(resetLogLik());
                    break;
                default:
                    LOGGER.info(instructions());
            }
            return line.equalsIgnoreCase(stopCommand) || processTerminated;
        }

        private String stopCalled() {
            stop();
            return stopMessage;
        }

        private String getLogLik() {
            return logLikelihoodMessage + Double.toString(getAverageRunningLogLikelihood());
        }

        private String resetLogLik() throws InterruptedException {
            String message = getLogLik() + resetMessage;
            resetRunningLogLikelihoods();
            Thread.sleep(10);
            return message + getLogLik();
        }

        private String instructions() {
            return instructionMessage;
        }

        @Override
        public void run() {
            commandLineStopListener();
        }
    }
}

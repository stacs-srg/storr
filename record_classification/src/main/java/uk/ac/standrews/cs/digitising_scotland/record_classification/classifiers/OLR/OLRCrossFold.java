package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.function.DoubleDoubleFunction;
import org.apache.mahout.math.function.Functions;

/**
 * Distributes training vectors across {@link OLRPool}s in a cross fold manner. Allows concurrent training
 * of the {@link OLRPool}s and provides a classify method that averages the classifications given by each pool.
 *
 * @author fraserdunlop
 */
public class OLRCrossFold {

    /** The model trainable. */
    private final boolean modelTrainable;

    /** The models. */
    private ArrayList<OLRPool> models = new ArrayList<>();

    /** The folds. */
    private int folds;

    /** The properties. */
    private Properties properties;

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

    public void stop(){
        for(OLRPool model : models){
            model.stop();
        }
    }

    public class StopListener implements Runnable{
        public void commandLineStopListener() throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = "";

            while (!line.equalsIgnoreCase("stop")) {
                line = in.readLine();
            }

            stop();

            in.close();
        }

        @Override
        public void run() {
            try {
                commandLineStopListener();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        executorService.awaitTermination(365, TimeUnit.DAYS);
        stop();
        stopService.shutdown();

        for (OLRPool model : models) {
            model.getSurvivors();
        }
    }

    /**
     * Train if possible.
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
     * Check trainable.
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

        Vector r = new DenseVector(numCategories());
        DoubleDoubleFunction scale = Functions.plusMult(1.0 / (models.size()));
        for (OLRPool model : models) {
            r.assign(model.classifyFull(instance), scale);
        }
        return r;
    }

    /**
     * Gets the log likelihood averaged over the models in the pool.
     * @param actual the actual classification
     * @param instance the instance vector
     * @return log likelihood
     */
    public double logLikelihood(final int actual, final Vector instance) {

        double logLikelihood = 0;
        for (OLRPool model : models) {
            logLikelihood += model.logLikelihood(actual, instance) / models.size();
        }
        return logLikelihood;
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
     * Num categories.
     *
     * @return the int
     */
    protected int numCategories() {

        return models.get(0).numCategories();
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
    }
}

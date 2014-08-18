package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.function.DoubleDoubleFunction;
import org.apache.mahout.math.function.Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Trains a number of models on the training vectors provided. These models are then used to make predictions
 * on the testing vectors and ranked from best to worst. Only the best models are kept and used for prediction.
 *
 * @author fraserdunlop
 */
public class OLRPool implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(OLRPool.class);
    private final boolean modelTrainable;
    private List<OLRShuffled> models = Lists.newArrayList();
    private Properties properties;
    private int poolSize;
    private int numSurvivors;
    private ArrayList<NamedVector> testingVectorList = Lists.newArrayList();
    private ArrayList<OLRShuffled> survivors;

    //    public Iterator<Matrix> getBetas() {
    //        Matrix[] matrices = new Matrix[models.size()];
    //        for (int i = 0; i < models.size(); i++) {
    //            matrices[i] = models.get(i).getBeta();
    //        }
    //        return new ArrayIterator<>(matrices);
    //    }
    //
    //    public void setBetas(Matrix beta) {
    //        for(OLRShuffled model : models){
    //            model.setBeta(beta);
    //        }
    //    }

    /**
     * Gets the number of records used for training so far across all the models in the pool.
     * @return int the number of training records used so far
     */

    public long getNumTrained() {

        long numTrained = 0;
        for (OLRShuffled model : models) {
            numTrained += model.getNumTrained();
        }
        return numTrained;
    }

    /**
     * Constructor.
     *
     * @param internalTrainingVectorList internal training vector list
     * @param testingVectorList          internal testing vector list
     * @param properties                 properties
     */
    public OLRPool(final Properties properties, final ArrayList<NamedVector> internalTrainingVectorList, final ArrayList<NamedVector> testingVectorList) {

        this.properties = properties;
        this.testingVectorList = testingVectorList;
        getConfigOptions();

        for (int i = 0; i < poolSize; i++) {
            OLRShuffled model = new OLRShuffled(properties, internalTrainingVectorList);
            models.add(model);
        }
        modelTrainable = true;
    }

    /**
     * Gets the average running log likelihood totals. Sums across each of the models and divides by pool size to get the average.
     * @return double the running loglikelihood average across all models
     */
    public double getAverageRunningLogLikelihood() {

        double ll = 0.;
        for (OLRShuffled model : models) {
            ll += model.getRunningLogLikelihood();
        }
        ll /= models.size();
        return ll;
    }

    /**
     * Rests the running log likelihood count in each of the {@link OLRShuffled} models.
     */
    public void resetRunningLogLikelihoods() {

        for (OLRShuffled model : models) {
            model.resetRunningLogLikelihood();
        }
    }

    /**
     * Trains, tests, packages, sorts.
     */
    @Override
    public void run() {

        trainIfPossible();
        //  getSurvivors();
    }

    /**
     * Stops the current pool of models from training.
     */
    public void stop() {

        for (OLRShuffled model : models) {
            model.stop();
        }
    }

    /**
     * Get the survivors from the model pool.
     * @return List of {@link OLRShuffled} models
     */
    public List<OLRShuffled> getSurvivors() {

        LOGGER.info("Getting survivors...");
        LOGGER.info("Calling testAndPackageModels");
        ArrayList<ModelDoublePair> modelPairs = testAndPackageModels();
        LOGGER.info("Calling getSurvivors");
        survivors = getSurvivors(modelPairs);
        return survivors;
    }

    private void trainIfPossible() {

        checkTrainable();
        try {
            this.trainAllModels();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkTrainable() {

        if (!modelTrainable) { throw new UnsupportedOperationException("This model has no files to train " + "on and may only be used for classification."); }
    }

    /**
     * Takes a vote from the surviving models.
     *
     * @param instance vector to classify
     * @return vector encoding probability distribution over output classes
     */
    public Vector classifyFull(final Vector instance) {

        Vector r = new DenseVector(numCategories());
        DoubleDoubleFunction scale = Functions.plusMult(1.0 / survivors.size());
        for (OLRShuffled model : survivors) {
            r.assign(model.classifyFull(instance), scale);
        }
        return r;
    }

    /**
     * Gets the average log likelihood of the surviving models.
     * @param actual actual classification
     * @param instance instance vector
     * @return log likelihood
     */
    public double logLikelihood(final int actual, final Vector instance) {

        double logLikelihood = 0;
        for (OLRShuffled model : survivors) {
            logLikelihood += model.logLikelihood(actual, instance) / survivors.size();
        }
        return logLikelihood;
    }

    private void getConfigOptions() {

        poolSize = Integer.parseInt(properties.getProperty("OLRPoolSize"));
        numSurvivors = Integer.parseInt(properties.getProperty("OLRPoolNumSurvivors"));

    }

    private void trainAllModels() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        for (OLRShuffled model : models) {
            executorService.submit(model);
        }
        executorService.awaitTermination(365, TimeUnit.DAYS);
        executorService.shutdown();
    }

    private double getProportionTestingVectorsCorrectlyClassified(final OLRShuffled model) {

        int countCorrect = 0;
        for (NamedVector vector : testingVectorList) {
            Vector classificationVector = model.classifyFull(vector);
            int classification = classificationVector.maxValueIndex();
            if (Integer.parseInt(vector.getName()) == classification) {
                countCorrect++;
            }
        }
        return ((double) countCorrect) / ((double) testingVectorList.size());
    }

    private ArrayList<ModelDoublePair> testAndPackageModels() {

        ArrayList<ModelDoublePair> modelPairs = new ArrayList<ModelDoublePair>();
        for (OLRShuffled model : models) {
            double proportionCorrect = getProportionTestingVectorsCorrectlyClassified(model);
            modelPairs.add(new ModelDoublePair(model, proportionCorrect));
        }
        return modelPairs;
    }

    protected int numCategories() {

        return survivors.get(0).numCategories();
    }

    private ArrayList<OLRShuffled> getSurvivors(final ArrayList<ModelDoublePair> modelPairs) {

        ArrayList<OLRShuffled> survivors = new ArrayList<OLRShuffled>();
        Collections.sort(modelPairs);
        for (int i = modelPairs.size() - 1; i >= modelPairs.size() - numSurvivors; i--) {
            survivors.add(modelPairs.get(i).getModel());
        }
        return survivors;
    }

    protected OLRPool() {

        modelTrainable = false;
    }

    protected void write(final DataOutputStream outputStream) throws IOException {

        outputStream.writeInt(survivors.size());
        for (OLRShuffled survivor : survivors) {
            survivor.write(outputStream);
        }
    }

    protected void readFields(final DataInputStream inputStream) throws IOException {

        survivors = new ArrayList<OLRShuffled>();
        int numModels = inputStream.readInt();
        for (int i = 0; i < numModels; i++) {
            OLRShuffled olrShuffled = new OLRShuffled();
            olrShuffled.readFields(inputStream);
            survivors.add(olrShuffled);

        }
    }
}

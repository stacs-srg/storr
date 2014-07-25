/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.MatrixWritable;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.function.Functions;

import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * An online logistic regression model that allows standard SGD or per term annealing with the option of
 * pseudo Bayesian L1 prior regularisation.
 */
public class OLR {

    /** The gradient. */
    private Gradient gradient = new Gradient();

    /** The beta. */
    protected org.apache.mahout.math.Matrix beta;

    /** The properties. */
    private Properties properties;

    /** The mu0. */
    private double mu0;

    /** The prior. */
    private L1 prior;

    /** The decay factor. */
    private double decayFactor;

    /** The per term annealing rate. */
    private double perTermAnnealingRate;

    /** Are we per term annealing. */
    private boolean weArePerTermAnnealing;

    /** Are we regularizing. */
    private boolean weAreRegularizing;

    /** The number of features. */
    private int numFeatures;

    /** The number of output categories. */
    private int numCategories;

    /** The update steps. */
    private Vector updateSteps;

    /** The update counts. */
    private Vector updateCounts;

    /** The step. */
    private int step;

    /** The running log likelihood. */
    private volatile double runningLogLikelihood;

    /** The number log likelihood sum updates. */
    private volatile AtomicInteger numLogLikelihoodSumUpdates;

    /**
     * Gets the step.
     *
     * @return the step
     */
    public int getStep() {

        return step;
    }

    /**
     * Gets the beta.
     *
     * @return the beta
     */
    public Matrix getBeta() {

        return beta;
    }

    /**
     * Sets the beta matrix.
     *
     * @param beta the new beta
     */
    public void setBeta(final Matrix beta) {

        this.beta = beta;
    }

    /**
     * Classifies an instance vector and returns a result vector.
     *
     * @param instance the instance vector to classify
     * @return the result vector 
     */
    public Vector classifyFull(final Vector instance) {

        Vector r = new DenseVector(numCategories);
        r.viewPart(1, numCategories - 1).assign(classify(instance));
        r.setQuick(0, 1.0 - r.zSum());
        return r;
    }

    /**
     * Gets the running log likelihood.
     *
     * @return the running log likelihood
     */
    public double getRunningLogLikelihood() {

        return runningLogLikelihood / numLogLikelihoodSumUpdates.get();
    }

    /**
     * Reset running log likelihood.
     */
    public void resetRunningLogLikelihood() {

        runningLogLikelihood = 0.;
        numLogLikelihoodSumUpdates = new AtomicInteger(1);
    }

    /**
     * Calculates the log likelihood.
     *
     * @param actual the actual output category ID
     * @param instance the instance vector
     * @return the double log likelihood
     */
    public double logLikelihood(final int actual, final Vector instance) {

        Vector p = classify(instance);
        if (actual > 0) {
            return Math.max(-100.0, Math.log(p.get(actual - 1)));
        }
        else {
            return Math.max(-100.0, Math.log1p(-p.zSum()));
        }
    }

    /**
     * Update log likelihood sum.
     *
     * @param actual the actual
     * @param classification the classification
     */
    private void updateLogLikelihoodSum(final int actual, final Vector classification) {

        double thisloglik;
        if (actual > 0) {
            thisloglik = Math.max(-100.0, Math.log(classification.get(actual - 1)));
        }
        else {
            thisloglik = Math.max(-100.0, Math.log1p(-classification.zSum()));
        }

        if (numLogLikelihoodSumUpdates.get() != 0) {
            runningLogLikelihood += (thisloglik - runningLogLikelihood) / numLogLikelihoodSumUpdates.get();
        }
        else {
            runningLogLikelihood = thisloglik;
        }

        numLogLikelihoodSumUpdates.getAndIncrement();
    }

    /**
     * Gets the num categories.
     *
     * @return the num categories
     */
    public int getNumCategories() {

        return numCategories;
    }

    /**
     * The Class Gradient.
     */
    private class Gradient {

        /**
         * Apply.
         *
         * @param instance the instance
         * @return the vector
         */
        public final Vector apply(final NamedVector instance) {

            int actual = Integer.parseInt(instance.getName());
            // what does the current model say?
            Vector v = classify(instance);
            updateLogLikelihoodSum(actual, v);
            Vector r = v.like();
            if (actual != 0) {
                r.setQuick(actual - 1, 1);
            }
            r.assign(v, Functions.MINUS);
            return r;
        }
    }

    /**
     * Constructor with default properties.
     */
    public OLR() {

        this(MachineLearningConfiguration.getDefaultProperties());
    }

    /**
     * Constructor that allows default properties to be overridden.
     *
     * @param properties properties
     */
    public OLR(final Properties properties) {

        resetRunningLogLikelihood();
        this.properties = properties;
        this.prior = new L1();
        getConfigOptions();
        initialiseModel();
    }

    /**
     * Instantiates a new olr.
     *
     * @param beta the beta
     */
    public OLR(final Matrix beta) {

        initialiseMode(beta);
    }

    /**
     * Trains an OLR model on a instance vector with a known 'actual' output class.
     *
     * @param instance feature vector
     */
    public void train(final NamedVector instance) {

        updateModelParameters(instance);
        updateCountsAndSteps(instance);
        nextStep();
    }

    /**
     * Next step.
     */
    private void nextStep() {

        step++;
    }

    /**
     * Update model parameters.
     *
     * @param instance the instance
     */
    private void updateModelParameters(final NamedVector instance) {

        Vector gradient = this.gradient.apply(instance);
        for (int category = 0; category < numCategories - 1; category++) {
            updateBetaCategory(instance, gradient, category);
        }
    }

    /**
     * Update beta category.
     *
     * @param instance the instance
     * @param gradient the gradient
     * @param category the category
     */
    private void updateBetaCategory(final Vector instance, final Vector gradient, final int category) {

        double gradientBase = gradient.get(category);
        Iterable<Element> element = instance.nonZeroes();
        for (Element nonZeroFeature : element) {
            updateCategoryAtNonZeroFeature(category, gradientBase, nonZeroFeature);
        }

    }

    /**
     * Update category at non zero feature.
     *
     * @param category the category
     * @param gradientBase the gradient base
     * @param featureElement the feature element
     */
    private void updateCategoryAtNonZeroFeature(final int category, final double gradientBase, final Vector.Element featureElement) {

        final double aSmallNumber = 0.000001;
        int feature = featureElement.index();

        if (weAreRegularizing) {
            regularize(category, feature);
        }
        if (gradientBase > aSmallNumber || gradientBase < -aSmallNumber) {
            updateCoefficient(category, feature, featureElement, gradientBase);
        }
    }

    /**
     * Update coefficient.
     *
     * @param category the category
     * @param feature the feature
     * @param featureElement the feature element
     * @param gradientBase the gradient base
     */
    private void updateCoefficient(final int category, final int feature, final Vector.Element featureElement, final double gradientBase) {

        double newValue = beta.getQuick(category, feature) + gradientBase * getLearningRate(feature) * featureElement.get();
        beta.setQuick(category, feature, newValue);
    }

    /**
     * Regularize.
     *
     * @param category the category
     * @param feature the feature
     */
    private void regularize(final int category, final int feature) {

        double lastUpdated = updateSteps.get(feature);
        double missingUpdates = getStep() - lastUpdated;
        if (missingUpdates > 0) {
            regularizeInProportionToMissingUpdates(category, feature, missingUpdates);
        }
    }

    /**
     * Regularize in proportion to missing updates.
     *
     * @param category the category
     * @param feature the feature
     * @param missingUpdates the missing updates
     */
    private void regularizeInProportionToMissingUpdates(final int category, final int feature, final double missingUpdates) {

        double rate = getLearningRate(feature);
        double newValue = prior.age(beta.get(category, feature), missingUpdates, rate);
        beta.set(category, feature, newValue);
    }

    /**
     * Gets the learning rate.
     *
     * @param feature the feature
     * @return the learning rate
     */
    private double getLearningRate(final int feature) {

        if (weArePerTermAnnealing) {
            return perTermLearningRate(feature);
        }
        else {
            return currentLearningRate();
        }
    }

    /**
     * Returns n-1 probabilities, one for each category but the 0-th.  The probability of the 0-th
     * category is 1 - sum(this result).
     *
     * @param instance A vector of features to be classified.
     * @return A vector of probabilities, one for each of the first n-1 categories.
     */
    public Vector classify(final Vector instance) {

        return link(classifyNoLink(instance));
    }

    /**
     * Link.
     *
     * @param v the v
     * @return the vector
     */
    public Vector link(final Vector v) {

        double max = v.maxValue();
        if (max >= 40) {
            // if max > 40, we subtract the large offset first
            // the size of the max means that 1+sum(exp(v)) = sum(exp(v)) to within round-off
            v.assign(Functions.minus(max)).assign(Functions.EXP);
            return v.divide(v.norm(1));
        }
        else {
            v.assign(Functions.EXP);
            return v.divide(1 + v.norm(1));
        }
    }

    /**
     * Classify no link.
     *
     * @param instance the instance
     * @return the vector
     */
    public Vector classifyNoLink(final Vector instance) {

        //must be overridden due to fact that superclass regularizes here
        //classifyNoLink gets called in gradient so in the original code regularization happens
        //twice for no reason!
        return beta.times(instance);
    }

    /**
     * Current learning rate.
     *
     * @return the double
     */
    public double currentLearningRate() {

        return mu0 * Math.pow(decayFactor, getStep());
    }

    /**
     * Per term learning rate.
     *
     * @param j the j
     * @return the double
     */
    public double perTermLearningRate(final int j) {

        return mu0 * Math.pow(perTermAnnealingRate, updateCounts.get(j));
    }

    /**
     * Update counts and steps.
     *
     * @param instance the instance
     */
    private void updateCountsAndSteps(final Vector instance) {

        Iterable<Element> instanceFeatures = instance.nonZeroes();

        for (Element feature : instanceFeatures) {
            updateCountsAndStepsAtIndex(feature.index());
        }

    }

    /**
     * Update counts and steps at index.
     *
     * @param j the j
     */
    private void updateCountsAndStepsAtIndex(final int j) {

        if (weAreRegularizing) {
            updateSteps(j);
        }
        if (weArePerTermAnnealing) {
            updateCounts(j);
        }
    }

    /**
     * Update counts.
     *
     * @param j the j
     */
    private void updateCounts(final int j) {

        updateCounts.setQuick(j, updateCounts.getQuick(j) + 1);
    }

    /**
     * Update steps.
     *
     * @param j the j
     */
    private void updateSteps(final int j) {

        updateSteps.setQuick(j, getStep());
    }

    /**
     * Gets the config options.
     *
     * @return the config options
     */
    private void getConfigOptions() {

        weArePerTermAnnealing = Boolean.parseBoolean(properties.getProperty("perTermLearning"));
        weAreRegularizing = Boolean.parseBoolean(properties.getProperty("olrRegularisation"));
        mu0 = Double.parseDouble(properties.getProperty("initialLearningRate"));
        decayFactor = Double.parseDouble(properties.getProperty("decayFactor"));
        perTermAnnealingRate = Double.parseDouble(properties.getProperty("perTermAnnealingRate"));
        numCategories = Integer.parseInt(properties.getProperty("numCategories"));
        numFeatures = Integer.parseInt(properties.getProperty("numFeatures"));
    }

    /**
     * Initialise model.
     */
    private void initialiseModel() {

        updateSteps = new DenseVector(numFeatures);
        updateCounts = new DenseVector(numFeatures);
        beta = new DenseMatrix(numCategories - 1, numFeatures);
    }

    /**
     * Initialise mode.
     *
     * @param beta the beta
     */
    private void initialiseMode(final Matrix beta) {

        this.beta = beta;
        numFeatures = beta.numCols();
        numCategories = beta.numRows() + 1;
        updateSteps = new DenseVector(numFeatures);
        updateCounts = new DenseVector(numFeatures);
    }

    /**
     * Writes model to file.
     *
     * @param filename name of file to write model to
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void serializeModel(final String filename) throws IOException {

        DataOutputStream dataOutputStream = getDataOutputStream(filename);
        write(dataOutputStream);
        dataOutputStream.close();
    }

    /**
     * Reads model from file.
     *
     * @param filename name of file to read model from
     * @return model in file
     * @throws IOException if file cannot be read. Indicates IO error
     */
    public static OLR deSerializeModel(final String filename) throws IOException {

        OLR olr;
        DataInputStream dataInputStream = getDataInputStream(filename);
        olr = createNewOLR(dataInputStream);
        return olr;
    }

    /**
     * Creates the new olr.
     *
     * @param dataInputStream the data input stream
     * @return the olr
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected static OLR createNewOLR(final DataInputStream dataInputStream) throws IOException {

        OLR olr = new OLR();
        olr.readFields(dataInputStream);
        return olr;
    }

    /**
     * Gets the data output stream.
     *
     * @param filename the filename
     * @return the data output stream
     * @throws FileNotFoundException the file not found exception
     */
    protected static DataOutputStream getDataOutputStream(final String filename) throws FileNotFoundException {

        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        return new DataOutputStream(fileOutputStream);
    }

    /**
     * Gets the data input stream.
     *
     * @param filename the filename
     * @return the data input stream
     * @throws FileNotFoundException the file not found exception
     */
    protected static DataInputStream getDataInputStream(final String filename) throws FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream(filename);
        return new DataInputStream(fileInputStream);
    }

    /**
     * Write.
     *
     * @param out the out
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void write(final DataOutputStream out) throws IOException {

        out.writeDouble(mu0);
        out.writeDouble(decayFactor);
        out.writeDouble(perTermAnnealingRate);
        out.writeBoolean(weArePerTermAnnealing);
        out.writeBoolean(weAreRegularizing);
        MatrixWritable.writeMatrix(out, beta);
        out.writeInt(numCategories);
        out.writeInt(numFeatures);
        out.writeInt(step);
        VectorWritable.writeVector(out, updateSteps);
        VectorWritable.writeVector(out, updateCounts);

    }

    /**
     * Read fields.
     *
     * @param in the in
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void readFields(final DataInputStream in) throws IOException {

        mu0 = in.readDouble();
        decayFactor = in.readDouble();
        perTermAnnealingRate = in.readDouble();
        weArePerTermAnnealing = in.readBoolean();
        weAreRegularizing = in.readBoolean();
        beta = MatrixWritable.readMatrix(in);
        numCategories = in.readInt();
        numFeatures = in.readInt();
        step = in.readInt();
        updateSteps = VectorWritable.readVector(in);
        updateCounts = VectorWritable.readVector(in);
        this.prior = new L1();
    }
}

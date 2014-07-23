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
import org.apache.mahout.math.*;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.function.Functions;

import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * An online logistic regression model that allows standard SGD or per term annealing with the option of
 * pseudo Bayesian L1 prior regularisation.
 */
public class OLR {

    private Gradient gradient = new Gradient();
    protected org.apache.mahout.math.Matrix beta;
    private Properties properties;
    private double mu0;
    private L1 prior;
    private double decayFactor;
    private double perTermAnnealingRate;
    private boolean weArePerTermAnnealing;
    private boolean weAreRegularizing;
    private int numFeatures;
    private int numCategories;
    private Vector updateSteps;
    private Vector updateCounts;
    private int step;
    private volatile double runningLogLikelihood;
    private volatile AtomicInteger numLogLikelihoodSumUpdates;

    public int getStep() {

        return step;
    }

    public Matrix getBeta() {
        return beta;
    }
    public void setBeta(Matrix beta) {
        this.beta = beta;
    }

    public Vector classifyFull(final Vector instance) {

        Vector r = new DenseVector(numCategories);
        r.viewPart(1, numCategories - 1).assign(classify(instance));
        r.setQuick(0, 1.0 - r.zSum());
        return r;
    }

    public double getRunningLogLikelihood() {

        return runningLogLikelihood / numLogLikelihoodSumUpdates.get();
    }

    public void resetRunningLogLikelihood() {
        runningLogLikelihood = 0.;
        numLogLikelihoodSumUpdates = new AtomicInteger(1);
    }

    public double logLikelihood(final int actual, final Vector instance) {

        Vector p = classify(instance);
        if (actual > 0) {
            return Math.max(-100.0, Math.log(p.get(actual - 1)));
        }
        else {
            return Math.max(-100.0, Math.log1p(-p.zSum()));
        }
    }

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

    public int getNumCategories() {

        return numCategories;
    }

    private class Gradient {

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

    public OLR(final Matrix beta){
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

    private void nextStep() {

        step++;
    }

    private void updateModelParameters(final NamedVector instance) {

        Vector gradient = this.gradient.apply(instance);
        for (int category = 0; category < numCategories - 1; category++) {
            updateBetaCategory(instance, gradient, category);
        }
    }

    private void updateBetaCategory(final Vector instance, final Vector gradient, final int category) {

        double gradientBase = gradient.get(category);
        Iterable<Element> element = instance.nonZeroes();
        for (Element nonZeroFeature : element) {
            updateCategoryAtNonZeroFeature(category, gradientBase, nonZeroFeature);
        }

    }

    private void updateCategoryAtNonZeroFeature(final int category, final double gradientBase, final Vector.Element featureElement) {

        int feature = featureElement.index();
        if (weAreRegularizing) {
            regularize(category, feature);
        }
        if (gradientBase > 0.000001 || gradientBase < -0.000001) {
            updateCoefficient(category, feature, featureElement, gradientBase);
        }
    }

    private void updateCoefficient(final int category, final int feature, final Vector.Element featureElement, final double gradientBase) {

        double newValue = beta.getQuick(category, feature) + gradientBase * getLearningRate(feature) * featureElement.get();
        beta.setQuick(category, feature, newValue);
    }

    private void regularize(final int category, final int feature) {

        double lastUpdated = updateSteps.get(feature);
        double missingUpdates = getStep() - lastUpdated;
        if (missingUpdates > 0) {
            regularizeInProportionToMissingUpdates(category, feature, missingUpdates);
        }
    }

    private void regularizeInProportionToMissingUpdates(final int category, final int feature, final double missingUpdates) {

        double rate = getLearningRate(feature);
        double newValue = prior.age(beta.get(category, feature), missingUpdates, rate);
        beta.set(category, feature, newValue);
    }

    private double getLearningRate(final int feature) {

        if (weArePerTermAnnealing) {
            return perTermLearningRate(feature);
        } else {
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

    public Vector link(final Vector v) {

        double max = v.maxValue();
        if (max >= 40) {
            // if max > 40, we subtract the large offset first
            // the size of the max means that 1+sum(exp(v)) = sum(exp(v)) to within round-off
            v.assign(Functions.minus(max)).assign(Functions.EXP);
            return v.divide(v.norm(1));
        } else {
            v.assign(Functions.EXP);
            return v.divide(1 + v.norm(1));
        }
    }

    public Vector classifyNoLink(final Vector instance) {

        //must be overridden due to fact that superclass regularizes here
        //classifyNoLink gets called in gradient so in the original code regularization happens
        //twice for no reason!
        return beta.times(instance);
    }

    public double currentLearningRate() {

        return mu0 * Math.pow(decayFactor, getStep());
    }

    public double perTermLearningRate(final int j) {

        return mu0 * Math.pow(perTermAnnealingRate, updateCounts.get(j));
    }

    private void updateCountsAndSteps(final Vector instance) {

        Iterable<Element> instanceFeatures = instance.nonZeroes();

        for (Element feature : instanceFeatures) {
            updateCountsAndStepsAtIndex(feature.index());
        }

    }

    private void updateCountsAndStepsAtIndex(final int j) {

        if (weAreRegularizing) {
            updateSteps(j);
        }
        if (weArePerTermAnnealing) {
            updateCounts(j);
        }
    }

    private void updateCounts(final int j) {

        updateCounts.setQuick(j, updateCounts.getQuick(j) + 1);
    }

    private void updateSteps(final int j) {

        updateSteps.setQuick(j, getStep());
    }

    private void getConfigOptions() {

        weArePerTermAnnealing = Boolean.parseBoolean(properties.getProperty("perTermLearning"));
        weAreRegularizing = Boolean.parseBoolean(properties.getProperty("olrRegularisation"));
        mu0 = Double.parseDouble(properties.getProperty("initialLearningRate"));
        decayFactor = Double.parseDouble(properties.getProperty("decayFactor"));
        perTermAnnealingRate = Double.parseDouble(properties.getProperty("perTermAnnealingRate"));
        numCategories = Integer.parseInt(properties.getProperty("numCategories"));
        numFeatures = Integer.parseInt(properties.getProperty("numFeatures"));
    }

    private void initialiseModel() {

        updateSteps = new DenseVector(numFeatures);
        updateCounts = new DenseVector(numFeatures);
        beta = new DenseMatrix(numCategories - 1, numFeatures);
    }

    private void initialiseMode(final Matrix beta){
        this.beta = beta;
        numFeatures = beta.numCols();
        numCategories = beta.numRows()+1;
        updateSteps = new DenseVector(numFeatures);
        updateCounts = new DenseVector(numFeatures);
    }

    /**
     * Writes model to file.
     *
     * @param filename name of file to write model to
     * @throws IOException
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

    protected static OLR createNewOLR(final DataInputStream dataInputStream) throws IOException {

        OLR olr = new OLR();
        olr.readFields(dataInputStream);
        return olr;
    }

    protected static DataOutputStream getDataOutputStream(final String filename) throws FileNotFoundException {

        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        return new DataOutputStream(fileOutputStream);
    }

    protected static DataInputStream getDataInputStream(final String filename) throws FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream(filename);
        return new DataInputStream(fileInputStream);
    }

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

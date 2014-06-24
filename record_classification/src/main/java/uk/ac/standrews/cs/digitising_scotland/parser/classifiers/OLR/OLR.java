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

package uk.ac.standrews.cs.digitising_scotland.parser.classifiers.OLR;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.mahout.classifier.sgd.AbstractOnlineLogisticRegression;
import org.apache.mahout.classifier.sgd.DefaultGradient;
import org.apache.mahout.classifier.sgd.Gradient;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.MatrixWritable;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;

import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * An online logistic regression model that allows standard SGD or per term annealing with the option of
 * pseudo Bayesian L1 prior regularisation.
 */
public class OLR extends AbstractOnlineLogisticRegression {

    private Gradient gradient = new DefaultGradient();

    private Properties properties;
    //set in properties
    private double mu0;
    private double decayFactor;
    private double perTermAnnealingRate;
    private boolean weArePerTermAnnealing;
    private boolean weAreRegularizing;
    private int numFeatures;

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

        this.properties = properties;
        this.prior = new L1();
        getConfigOptions();
        initialiseModel();
    }

    /**
     * Trains an OLR model on a instance vector with a known 'actual' output class.
     *
     * @param actual   known 'gold standard' coding
     * @param instance feature vector
     */
    public void train(final int actual, final Vector instance) {

        updateModelParameters(actual, instance);
        updateCountsAndSteps(instance);
        nextStep();
    }

    private void updateModelParameters(final int actual, final Vector instance) {

        Vector gradient = this.gradient.apply(null, actual, instance, this);
        for (int category = 0; category < numCategories - 1; category++) {
            updateBetaCategory(instance, gradient, category);
        }
        // System.out.println(beta);
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
        updateCoefficient(category, feature, featureElement, gradientBase);
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
    @Override
    public Vector classify(final Vector instance) {

        return link(classifyNoLink(instance));
    }

    @Override
    public Vector classifyNoLink(final Vector instance) {

        //must be overridden due to fact that superclass regularizes here
        //classifyNoLink gets called in gradient so in the original code regularization happens
        //twice for no reason!
        return beta.times(instance);
    }

    @Override
    public double currentLearningRate() {

        return mu0 * Math.pow(decayFactor, getStep());
    }

    @Override
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

    /**
     * Unsupported operation. This should be set in the config file.
     *
     * @param alpha New value of decayFactor, the exponential decay rate for the learning rate.
     * @return this, so other configurations can be chained.
     */
    public OLR alpha(final double alpha) {

        throw new UnsupportedOperationException("alpha could not be set to " + alpha + ". Parameter alpha set in config file.");
    }

    /**
     * Unsupported operation. This should be set in the config file.
     *
     * @param lambda prior weighting
     * @return OLR object (chainable)
     */
    @Override
    public OLR lambda(final double lambda) {

        throw new UnsupportedOperationException("Lambda could not be set to " + lambda + "Parameter lambda set in config file");
    }

    /**
     * Unsupported operation. This should be set in the config file.
     *
     * @param learningRate New value of initial learning rate.
     * @return This, so other configurations can be chained.
     */
    public OLR learningRate(final double learningRate) {

        throw new UnsupportedOperationException("The learning rate could not be set to " + learningRate + ". Learning rate set in config file");
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

        super.updateSteps = new DenseVector(numFeatures);
        super.updateCounts = new DenseVector(numFeatures);
        super.beta = new DenseMatrix(numCategories - 1, numFeatures);
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
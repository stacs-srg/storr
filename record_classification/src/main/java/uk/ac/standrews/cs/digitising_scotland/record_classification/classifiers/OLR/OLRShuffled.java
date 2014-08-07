/**
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;

import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * Training involves shuffling the training vectors and training the model. This process is repeated for the desired
 * number of repetitions.
 *
 * @author fraserdunlop
 */
public class OLRShuffled implements Runnable {

    //prevents training of models that have been read from disk as these do not have vectors to train on
    private boolean modelTrainable;
    private OLR model;
    private Properties properties = MachineLearningConfiguration.getDefaultProperties();
    private int reps; //set in config
    private ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();
    private boolean stopped = false;

    //----constructors---

    /**
     * Constructor.
     *
     * @param trainingVectorList the training vector list
     */
    public OLRShuffled(final ArrayList<NamedVector> trainingVectorList) {

        this(MachineLearningConfiguration.getDefaultProperties(), trainingVectorList);
    }

    /**
     * Gets the running log likelihood.
     *
     * @return the running log likelihood
     */
    public double getRunningLogLikelihood() {

        return model.getRunningLogLikelihood();
    }

    /**
     * Reset running log likelihood.
     */
    public void resetRunningLogLikelihood() {

        model.resetRunningLogLikelihood();
    }

    /**
     * Constructor.
     *
     * @param properties         the properties object that specifies among other things the number of training repetitions
     * @param trainingVectorList the training vector list
     */
    public OLRShuffled(final Properties properties, final ArrayList<NamedVector> trainingVectorList) {

        this.properties = properties;
        getConfigOptions();
        this.trainingVectorList = trainingVectorList;
        this.model = new OLR(properties);
        modelTrainable = true;
    }

    /**
     * Allows train() to be run in its own thread.
     */
    @Override
    public void run() {

        trainIfPossible();
    }

    /**
     * This method performs the training of the model.
     * Shuffles the training files, trains on all files and repeats this process
     * for the number of repetitions specified in the config file.
     */
    private void train() {

        for (int rep = 0; rep < reps; rep++) {
            if (stopped()) {
                break;
            }
            //  logger.info("Performing rep " + rep);
            shuffleAndTrainOnAllVectors();
        }
    }

    /**
     * Classifies a vector.
     *
     * @param instance vector to classify
     * @return vector encoding probability distribution over output classes
     */
    public Vector classifyFull(final Vector instance) {

        Vector classifyFull = model.classifyFull(instance);
        return classifyFull;
    }

    /**
     * Gets the log likelihood.
     *
     * @param actual   actual classification
     * @param instance instance vector
     * @return log likelihood
     */
    public double logLikelihood(final int actual, final Vector instance) {

        return model.logLikelihood(actual, instance);
    }

    /**
     * Gets the configuration options.
     *
     * @return the configuration options
     */
    private void getConfigOptions() {

        reps = Integer.parseInt(properties.getProperty("OLRShuffledReps"));
    }

    /**
     * Checks if the models are trainable and trains the models if possible.
     */
    private void trainIfPossible() {

        checkTrainable();
        this.train();
    }

    /**
     * Checks if the model is trainable. Models read back in from file are not trainable.
     */
    private void checkTrainable() {

        if (!modelTrainable) { throw new UnsupportedOperationException("This model has no files to train " + "on and may only be used for classification."); }
    }

    /**
     * Shuffle and train on all vectors.
     */
    private void shuffleAndTrainOnAllVectors() {

        Collections.shuffle(trainingVectorList);
        for (NamedVector vector : trainingVectorList) {
            if (stopped()) {
                break;
            }
            this.model.train(vector);
        }
    }

    /**
     * Sets the 'stopped' flag to true.
     */
    public void stop() {

        stopped = true;
    }

    /**
     * Returns the value of the stopped flag.
     *
     * @return true, if stopped
     */
    private boolean stopped() {

        return stopped;
    }

    /**
     * Allows serialization of the model to file.
     *
     * @param filename name of file to serialize model to
     * @throws IOException Indicates IO error on writing model
     */
    public void serializeModel(final String filename) throws IOException {

        model.serializeModel(filename);
    }

    /**
     * Allows de-serialization of a model from a file. The de-serialized model is not trainable.
     *
     * @param filename name of file to de-serialize
     * @return OLRShuffled deserialized model
     * @throws IOException Indicates IO error on reading model
     */
    public static OLRShuffled deSerializeModel(final String filename) throws IOException {

        OLRShuffled olrShuffled = new OLRShuffled();
        DataInputStream inputStream = OLR.getDataInputStream(filename);
        olrShuffled.readFields(inputStream);
        return olrShuffled;
    }

    /**
     * Writes the model to a {@link DataOutputStream}.
     *
     * @param outputStream the output stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void write(final DataOutputStream outputStream) throws IOException {

        model.write(outputStream);
    }

    /**
     * Reads the fields from an inputStream and creates an OLR model.
     *
     * @param inputStream the input stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void readFields(final DataInputStream inputStream) throws IOException {

        OLR olr = new OLR();
        olr.readFields(inputStream);
        model = olr;
    }

    /**
     * Instantiates a new OLR shuffled.
     */
    protected OLRShuffled() {

        modelTrainable = false;
    }

    /**
     * Gets the number of output categories.
     *
     * @return int the number of categories.
     */
    protected int numCategories() {

        return model.getNumCategories();
    }

    /**
     * Gets the beta matrix.
     *
     * @return the beta maxtrix
     */
    protected Matrix getBeta() {

        return model.getBeta();
    }

    /**
     * Sets the beta matrix.
     *
     * @param beta the new beta matrix
     */
    protected void setBeta(final Matrix beta) {

        model.setBeta(beta);
    }

    /**
     * Gets the number of records used for training so far across all the models in the pool.
     * @return int the number of training records used so far
     */
    public int getNumTrained() {

        return model.getNumTrained();
    }

}

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

    public double getRunningLogLikelihood() {

        return model.getRunningLogLikelihood();
    }

    public void resetRunningLogLikelihood() {

        model.resetRunningLogLikelihood();
    }

    /**
     * Constructor.
     *
     * @param trainingVectorList the training vector list
     * @param properties         the properties object that specifies among other things the number of training repetitions
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

    private void getConfigOptions() {

        reps = Integer.parseInt(properties.getProperty("OLRShuffledReps"));
    }

    private void trainIfPossible() {

        checkTrainable();
        this.train();
    }

    private void checkTrainable() {

        if (!modelTrainable) { throw new UnsupportedOperationException("This model has no files to train " + "on and may only be used for classification."); }
    }

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

    protected void write(final DataOutputStream outputStream) throws IOException {

        model.write(outputStream);
    }

    protected void readFields(final DataInputStream inputStream) throws IOException {

        OLR olr = new OLR();
        olr.readFields(inputStream);
        model = olr;
    }

    protected OLRShuffled() {

        modelTrainable = false;
    }

    protected int numCategories() {

        return model.getNumCategories();
    }

}

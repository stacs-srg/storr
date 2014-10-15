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

package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
public class OLRShuffled implements Runnable, Serializable {

    private static final long serialVersionUID = -4877057661440167819L;
    //prevents training of models that have been read from disk as these do not have vectors to train on
    private OLR model;
    //set in config
    private int reps;
    private transient List<NamedVector> trainingVectorList = new ArrayList<>();
    private boolean stopped = false;

    //----constructors---
    /**
     * Constructor.
     *
     * @param properties         the properties object that specifies among other things the number of training repetitions
     * @param trainingVectorList2 the training vector list
     */
    public OLRShuffled(final Properties properties, final List<NamedVector> trainingVectorList2) {

        reps = Integer.parseInt(properties.getProperty("OLRShuffledReps"));
        this.trainingVectorList = trainingVectorList2;
        this.model = new OLR(properties);
    }

    /**
     * Constructor.
     *
     * @param properties the properties object that specifies among other things the number of training repetitions
     * @param betaMatrix the new beta matrix which is used as a starting point for model training
     * @param trainingVectorList the training vector list
     */
    public OLRShuffled(final Properties properties, final Matrix betaMatrix, final List<NamedVector> trainingVectorList) {

        reps = Integer.parseInt(properties.getProperty("OLRShuffledReps"));
        this.trainingVectorList = trainingVectorList;
        this.model = new OLR(properties, betaMatrix);
    }

    /**
     * Constructor.
     *
     * @param trainingVectorList the training vector list
     */
    public OLRShuffled(final ArrayList<NamedVector> trainingVectorList) {

        this(MachineLearningConfiguration.getDefaultProperties(), trainingVectorList);
    }

    public OLRShuffled() {

    }

    //----------------

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

    //
    //    /**
    //     * Gets the configuration options.
    //     *
    //     * @return the configuration options
    //     */
    //    private void getConfigOptions() {
    //
    //        reps = Integer.parseInt(properties.getProperty("OLRShuffledReps"));
    //    }

    /**
     * Checks if the models are trainable and trains the models if possible.
     */
    private void trainIfPossible() {

        this.train();
    }

    /**
     * Shuffle and train on all vectors.
     */
    private void shuffleAndTrainOnAllVectors() {

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
     * Gets the number of records used for training so far across all the models in the pool.
     * @return int the number of training records used so far
     */
    public long getNumTrained() {

        return model.getNumTrained();
    }

}

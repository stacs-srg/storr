/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.hmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import cc.mallet.fst.HMM;
import cc.mallet.fst.HMMTrainerByLikelihood;
import cc.mallet.fst.PerClassAccuracyEvaluator;
import cc.mallet.fst.Transducer;
import cc.mallet.fst.TransducerEvaluator;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SimpleTaggerSentence2TokenSequence;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;

/**
 * The Class HiddenMarkovModel uses Mallet functionality to build a Hidden Markov Model from labeled training data.
 */
public class HiddenMarkovModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(HiddenMarkovModel.class);

    /** The training filename. */
    private String trainingFilename;

    /** The testing filename. */
    private String testingFilename;

    /**
     * Instantiates a new hidden markov model.
     *
     * @param trainingFilename the training filename
     * @param testingFilename the testing filename
     */
    public HiddenMarkovModel(final String trainingFilename, final String testingFilename) {

        this.trainingFilename = trainingFilename;
        this.testingFilename = testingFilename;
    }

    /**
     * Trains an hidden markov model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void trainHmm(final String outputFile) throws IOException {

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        SimpleTaggerSentence2TokenSequence tagger = new SimpleTaggerSentence2TokenSequence();
        LOGGER.info("new SimpleTaggerSentence2TokenSequence target processing?:" + tagger.isTargetProcessing());

        Alphabet alphabet = generateAlphabet(testingFilename);
        tagger.setDataAlphabet(alphabet);
        pipes.add(tagger);

        Pipe tokenSequence2FeatureSequence = new TokenSequence2FeatureSequence();
        tokenSequence2FeatureSequence.setDataAlphabet(alphabet);
        pipes.add(tokenSequence2FeatureSequence);

        Pipe pipe = new SerialPipes(pipes);

        InstanceList trainingInstances = new InstanceList(pipe);
        LOGGER.info("alphabet matches: " + Alphabet.alphabetsMatch(pipe, trainingInstances));
        trainingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new FileInputStream(trainingFilename), "UTF-8")), Pattern.compile("^\\s*$"), true));

        tagger.setTargetProcessing(false);
        pipe.setTargetProcessing(false);
        LOGGER.info("new SimpleTaggerSentence2TokenSequence target processing?:" + pipe.isTargetProcessing());

        //  InstanceList testingInstances = new InstanceList(pipe.getDataAlphabet(), pipe.getTargetAlphabet());

        LOGGER.info("alphabet " + trainingInstances.get(0).getAlphabet());

        HMM hmm = new HMM(pipe, null);
        hmm.addStatesForLabelsConnectedAsIn(trainingInstances);
        //hmm.addStatesForBiLabelsConnectedAsIn(trainingInstances);

        HMMTrainerByLikelihood trainer = new HMMTrainerByLikelihood(hmm);
        TransducerEvaluator trainingEvaluator = new PerClassAccuracyEvaluator(trainingInstances, "training");
        //  TransducerEvaluator testingEvaluator = new PerClassAccuracyEvaluator(testingInstances, "testing");
        trainer.train(trainingInstances, 10);

        trainingEvaluator.evaluate(trainer);
        //    testingEvaluator.evaluate(trainer);

        LOGGER.info("new SimpleTaggerSentence2TokenSequence target processing?:" + tagger.isTargetProcessing());

        Transducer transducer = trainer.getTransducer();
        LOGGER.info("transducer.isGenerative(): " + transducer.isGenerative());

        Pipe pip = transducer.getInputPipe();
        InstanceList i = new InstanceList(pip);
        pip.setTargetProcessing(false);

        LOGGER.info("num of states: " + hmm.numStates());
        LOGGER.info("states are: ");
        for (int j = 0; j < hmm.numStates(); j++) {
            LOGGER.info(hmm.getState(j).getName());
        }

        //   i.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new FileInputStream(testingFilename))), Pattern.compile("^\\s*$"), true));
        for (Instance instance : i) {
            Instance out = transducer.label(instance);
            LOGGER.info(instance.getData() + ": \t " + out.getTarget());

        }

        Sequence<Integer> newSequence = new FeatureSequence(transducer.getInputPipe().getAlphabet(), new int[]{1, 9, 10});

        LOGGER.info(newSequence.toString());
        LOGGER.info(transducer.transduce(newSequence).toString());

        alphabet = dumpAlphabet(transducer);

        StringBuilder sb = printSequenceFeatures(alphabet, transducer);

        Utils.writeToFile(sb.toString(), outputFile);

    }

    private Alphabet dumpAlphabet(final Transducer transducer) throws IOException {

        Alphabet alphabet;
        alphabet = transducer.getInputPipe().getAlphabet();
        PrintWriter writer = new PrintWriter(new File("target/alphabet"), "UTF-8");
        alphabet.dump(writer);
        writer.flush();
        return alphabet;
    }

    private StringBuilder printSequenceFeatures(final Alphabet alphabet, final Transducer transducer) throws IOException {

        ArrayList<FeatureSequence> sequenceList = createFeatureSequenceList(testingFilename, alphabet);

        StringBuilder sb = new StringBuilder();

        for (FeatureSequence sequence : sequenceList) {
            Sequence hmmSequence = transducer.transduce(sequence);
            for (int j = 0; j < sequence.size(); j++) {
                sb.append((sequence.get(j)) + " " + hmmSequence.get(j) + "\n");
            }
            sb.append("\n");

            LOGGER.info(sequence.toString() + "\n" + hmmSequence);
        }
        return sb;
    }

    /**
     * Creates the feature sequence list.
     *
     * @param testingFilename2 the testing filename2
     * @param alphabet the alphabet
     * @return the array list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private ArrayList<FeatureSequence> createFeatureSequenceList(final String testingFilename2, final Alphabet alphabet) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testingFilename2), "UTF8"));
        String line = "";
        ArrayList<FeatureSequence> list = new ArrayList<FeatureSequence>();

        line = reader.readLine();

        FeatureSequence sequence = new FeatureSequence(alphabet);

        while (line != null && line.length() != 0) {
            sequence.add(alphabet.lookupIndex(line.trim()));
            line = reader.readLine();
        }

        list.add(sequence);
        line = reader.readLine();

        reader.close();
        return list;
    }

    /**
     * Generates an alphabet.
     * @return Alphabet wordAlphabet
     * @throws IOException Indicates {@link IOException}
     */
    public Alphabet generateAlphabet(final String testingFilename2) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testingFilename2), "UTF8"));
        String line = "";
        HashMap<String, Integer> m = new HashMap<String, Integer>();
        while ((line = reader.readLine()) != null) {
            m.put(line, 1);
        }
        Object[] alphabetEntries = m.keySet().toArray();
        Alphabet wordAlphabet = new Alphabet(alphabetEntries);
        wordAlphabet.dump();
        reader.close();
        return wordAlphabet;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(final String[] args) throws Exception {

        HiddenMarkovModel trainer = new HiddenMarkovModel("kilm3100HMM.txt", "kilmTesting100Prepared.txt");
        trainer.trainHmm("1000ExamplesToCorrect.txt");
    }
}

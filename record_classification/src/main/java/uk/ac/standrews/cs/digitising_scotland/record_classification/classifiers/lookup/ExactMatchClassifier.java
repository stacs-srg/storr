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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * Uses a lookup table to return matches as classifications.
 * @author frjd2, jkc25
 *
 */
public class ExactMatchClassifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExactMatchClassifier.class);
    private Map<String, Set<Classification>> lookupTable;
    private String modelFileName = "target/lookupTable";

    /**
     * Can be used to overwrite the default file path for model writing.
     * @param modelFileName new model path
     */
    public void setModelFileName(final String modelFileName) {

        this.modelFileName = modelFileName;
    }

    /**
     * Creates a new {@link ExactMatchClassifier} and creates an empty lookup table.
     */
    public ExactMatchClassifier() {

        this.lookupTable = new HashMap<>();

    }

    /**
     * Creates a new {@link ExactMatchClassifier} and creates and fills the lookup table with the contents of the bucket.
     * Equivalent to call @train().
     * @param bucket Bucket containing records to put in the lookup table.
     * @throws IOException IO error if location to write model cannot be accessed
     */
    public ExactMatchClassifier(final Bucket bucket) throws IOException {

        this();
        fillLookupTable(bucket);
        writeModel(modelFileName);

    }

    public void train(final Bucket bucket) throws Exception {

        fillLookupTable(bucket);
        writeModel(modelFileName);

    }

    /**
     * Adds each gold standard {@link Classification} in the records to the lookupTable.
     * @param record to add
     */
    private void addRecordToLookupTable(final Record record, final Map<String, Set<Classification>> lookup) {

        final Set<Classification> goldStandardCodes = record.getOriginalData().getGoldStandardClassifications();
        String concatDescription = getConcatenatedDescription(goldStandardCodes);
        List<String> blacklist = new ArrayList<>();
        addToLookup(lookup, goldStandardCodes, concatDescription, blacklist);
    }

    protected void addToLookup(final Map<String, Set<Classification>> lookup, final Set<Classification> goldStandardCodes, final String concatDescription, final List<String> blacklist) {

        if (!blacklist.contains(concatDescription)) {
            if (!lookup.containsKey(concatDescription)) {

                Set<Classification> editClassification = changeConfidences(goldStandardCodes);
                lookup.put(concatDescription, editClassification);
            }
            else if (!goldStandardCodes.equals(lookup.get(concatDescription))) {
                blacklist.add(concatDescription);
                lookup.remove(concatDescription);
                LOGGER.info(concatDescription + " removed");
            }
        }
    }

    private Set<Classification> changeConfidences(final Set<Classification> goldStandardCodes) {

        Set<Classification> editedSet = new HashSet<>();
        for (Classification classification : goldStandardCodes) {
            // Make new code witj -1 as confidence so we can tell where classifications came from later.
            // -2 means exact match, -1 means cache classifier
            Classification editClassification = new Classification(classification.getCode(), classification.getTokenSet(), -2.0);
            editedSet.add(editClassification);
        }
        return editedSet;
    }

    private String getConcatenatedDescription(final Set<Classification> goldStandardCodes) {

        boolean isFirst = true;
        String concat = "";
        for (Classification classification : goldStandardCodes) {
            if (isFirst) {
                concat += classification.getTokenSet().toString();
                isFirst = false;
            }
            else {
                concat += ", " + classification.getTokenSet().toString();
            }
        }

        return concat;
    }

    private void fillLookupTable(final Bucket bucket) {

        for (Record record : bucket) {
            addRecordToLookupTable(record, lookupTable);
        }
    }

    /**
     * Writes model to file. File name is fileName.ser
     *
     * @param fileName name of file to write model to
     * @throws IOException if model location cannot be read
     * */
    public void writeModel(final String fileName) throws IOException {

        FileOutputStream fos = new FileOutputStream(fileName + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        write(oos);
    }

    protected ExactMatchClassifier readModel(final String fileName) throws ClassNotFoundException, IOException {

        //deserialize the .ser file
        InputStream file = new FileInputStream(fileName + ".ser");
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);
        try {

            Map<String, Set<Classification>> recoveredMap = (Map<String, Set<Classification>>) input.readObject();
            lookupTable = recoveredMap;

        }
        finally {
            closeStreams(file, input);

        }
        return this;
    }

    private void closeStreams(final InputStream file, final ObjectInput input) throws IOException {

        if (input != null) {
            input.close();
            file.close();
        }
    }

    private void write(final ObjectOutputStream oos) throws IOException {

        oos.writeObject(lookupTable);
        oos.close();
    }

    public ExactMatchClassifier getModelFromDefaultLocation() {

        ExactMatchClassifier classifier = null;
        try {
            classifier = readModel(modelFileName);
        }
        catch (ClassNotFoundException e) {
            LOGGER.error("Could not get model from default location. Class not found exception.", e.getException());
        }
        catch (IOException e) {
            LOGGER.error("Could not get model from default location. IOException.", e.getCause());
        }
        return classifier;
    }

    /**
     * Classifies a {@link TokenSet} to a set of {@link Classification}s using the classifiers lookup table.
     * @param tokenSet to classify
     * @return Set<CodeTripe> code triples from lookup table
     * @throws IOException Indicates an I/O error
     */
    public Set<Classification> classifyTokenSetToCodeTripleSet(final TokenSet tokenSet) throws IOException {

        Set<Classification> result = lookupTable.get(tokenSet);

        if (result != null) {
            result = setConfidenceLevels(result, 2.0);
            return result;

        }
        else {
            return null;
        }
    }

    private Set<Classification> setConfidenceLevels(final Set<Classification> result, final double i) {

        Set<Classification> newResults = new HashSet<Classification>();
        for (Classification codeTriple : result) {
            Classification newCodeT = new Classification(codeTriple.getCode(), codeTriple.getTokenSet(), i);
            newResults.add(newCodeT);
        }
        return newResults;
    }

    public Pair<Code, Double> classify(final TokenSet tokenSet) throws IOException {

        Set<Classification> result = lookupTable.get(tokenSet);

        if (result != null) {
            Classification current = result.iterator().next();
            return new Pair<Code, Double>(current.getCode(), current.getConfidence());

        }
        else {
            return null;
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((lookupTable == null) ? 0 : lookupTable.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        ExactMatchClassifier other = (ExactMatchClassifier) obj;
        if (lookupTable == null) {
            if (other.lookupTable != null) { return false; }
        }
        else if (!lookupTable.equals(other.lookupTable)) { return false; }
        return true;
    }

}

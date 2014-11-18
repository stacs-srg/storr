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
package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import java.util.HashMap;
import java.util.Set;


/**
 * Builds feature/code profiles for analysis of the feature/code space.
 * Created by fraserdunlop on 13/10/2014 at 15:59.
 */
public class FeatureSpaceAnalyser {

    /**
     * A map of codes to their predicting features. Each code
     * maps to a map whos keyset is all of the features which
     * predict the code in the training set with counts.
     */
    private HashMap<Code,HashMap<String,Integer>> featureProfiles;

    /**
     * A map of features to the codes which they predict.
     * Each feature maps to a map whos keyset is all of
     * the codes which are predicted by the feature in
     * the training set with counts.
     */
    private HashMap<String, HashMap<Code,Integer>> codeProfiles;

    /**
     * The distribution of the features in the training set.
     * Simply a count of the number of times each feature has
     * occurred.
     */
    private HashMap<String,Integer> featureDist;

    public boolean isFeature(final String feature){
        return codeProfiles.containsKey(feature);
    }

    public double featureFrequencyInverseCodeFrequency(Code code, String feature){
        return getNormalisedFeatureFrequency(code, feature) / logScaledInverseCodeFrequency(feature);
    }

    private double logScaledInverseCodeFrequency(String feature) {
        return Math.log( (double) getTotalNumberOfCodes() / (double) getNumberOfCodesContainingFeature(feature));
    }

    protected int getNumberOfCodesContainingFeature(String feature) {
        return codeProfiles.get(feature).size();
    }

    private int getTotalNumberOfCodes() {
        return featureProfiles.size();
    }

    private double getNormalisedFeatureFrequency(Code code, String feature) {
        return (double) featureProfiles.get(code).get(feature) / (double) featureDist.get(feature);
    }


    /**
     * Takes an iterable container of records e.g. a bucket.
     * Builds profiles of codes and features to enable
     * feature/code space analysis.
     * @param records records for building profiles.
     */
    public FeatureSpaceAnalyser(Iterable<Record> records) {
        featureProfiles = new HashMap<>();
        featureDist = new HashMap<>();
        codeProfiles = new HashMap<>();
        buildMap(records);
    }

    private void buildMap(Iterable<Record> records) {
        for (Record record : records){
            for(Classification classification : record.getGoldStandardClassificationSet()){
                addToFeatureProfiles(classification);
                addToFeatureDist(classification);
                addToCodeProfile(classification);
            }
        }
    }

    private void addToFeatureDist(Classification classification) {
        for(String token : classification.getTokenSet()){
            if(!featureDist.containsKey(token)){
                featureDist.put(token,1);
            } else {
                int currentValue = featureDist.get(token);
                featureDist.put(token,currentValue+1);
            }

        }
    }

    private void addToCodeProfile(Classification classification) {
        Code code = classification.getCode();
        TokenSet tokenSet = classification.getTokenSet();
        for(String token : tokenSet) {
            if (!codeProfiles.containsKey(token)){
                codeProfiles.put(token,new HashMap<Code, Integer>());
            }
            HashMap<Code,Integer> profile = codeProfiles.get(token);
            if(profile.containsKey(code)){
               int count = profile.get(code);
               count++;
               profile.put(code,count);
            } else {
               profile.put(code,1);
            }
        }
    }

    private void addToFeatureProfiles(Classification classification) {
        Code code = classification.getCode();
        if (!featureProfiles.containsKey(code)) {
            featureProfiles.put(code, new HashMap<String, Integer>());
        }
        for (String token : classification.getTokenSet()){
            if(!featureProfiles.get(code).containsKey(token)){
                featureProfiles.get(code).put(token,1);
            } else {
                int currentValue = getInCodeFeatureFrequency(code, token);
                featureProfiles.get(code).put(token,currentValue+1);
            }
        }
    }

    private int getInCodeFeatureFrequency(Code code, String feature) {
        return featureProfiles.get(code).get(feature);
    }

    public HashMap<String, Integer> featureProfile(Code code) {
        return featureProfiles.get(code);
    }

    public HashMap<Code, Integer> codeProfile(String feature) {
        return codeProfiles.get(feature);
    }

    public boolean contains(Code code) {
        return featureProfiles.containsKey(code);
    }

    public int featureCount(String feature) {
        return featureDist.get(feature);
    }


    public CodeProfile  getAnalysis(Code code) {
        Set<String> features = featureProfile(code).keySet();
        HashMap<String, FeatureProfile> feMap = new HashMap<>();
        for (String feature : features) {
            int inCodeFeatureCount = featureProfile(code).get(feature);
            int countInTotal = featureCount(feature);
            HashMap<Code, Integer> profile = codeProfile(feature);
            double ffIcf = featureFrequencyInverseCodeFrequency(code,feature);
            feMap.put(feature,new FeatureProfile(feature,ffIcf,inCodeFeatureCount,countInTotal,profile));
        }
        return new CodeProfile(code, feMap);
    }

    public Set<Code> codes() {
        return featureProfiles.keySet();
    }
}

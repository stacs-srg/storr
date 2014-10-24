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
                int currentValue = featureProfiles.get(code).get(token);
                featureProfiles.get(code).put(token,currentValue+1);
            }
        }
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

    public int featureDist(String feature) {
        return featureDist.get(feature);
    }


    public CodeProfile  getAnalysis(Code code) {
        Set<String> features = featureProfile(code).keySet();
        HashMap<String, FeatureProfile> feMap = new HashMap<>();
        for (String feature : features) {
            int inCodeFeatureCount = featureProfile(code).get(feature);
            int countInTotal = featureDist(feature);
            double proportionInCode = (double) inCodeFeatureCount / (double) countInTotal;
            HashMap<Code, Integer> profile = codeProfile(feature);
            feMap.put(feature,new FeatureProfile(feature,inCodeFeatureCount,countInTotal,proportionInCode,profile));

        }
        return new CodeProfile(code, feMap);
    }

    private int maxInCodeFeatureCountLength(Set<String> features, Code code) {
        Integer maxLength = 0;
        for (String feature : features) {
            Integer numLength = featureProfile(code).get(feature);
            if (numLength > maxLength)
                maxLength = numLength;
        }
        return maxLength.toString().length();
    }
}

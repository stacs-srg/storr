package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;

import java.util.HashMap;
import java.util.Set;

/**
 * Datastructure for holding information on the feature distribution for a code.
 * FeatureSpaceAnalyser returns one of these objects when asked to getAnalysis for a code.
 * Created by fraserdunlop on 24/10/2014 at 12:27.
 */
public class CodeProfile {

    /**
     * Map of predicting features to data structures encoding information
     * on the feature profiles.
     */
    private final HashMap<String, FeatureProfile> feMap;

    /**
     * The code.
     */
    private Code code;

    /**
     *     * ** used in formatting **
     * Count of the feature with the largest count
     * in records predicting the code.
     */
    private Integer maxInCodeFeatureCount;

    /**
     *      * ** used in formatting **
     * Count of the feature with the largest count
     * in the whole data set.
     */
    private Integer maxTotalFeatureCount;

    /**
     *      * ** used in formatting **
     * String length of the longest feature.
     * Calculated on instantiation of CodeProfile.
     */
    private int maxFeatureStringLength;

    /**
     * Instantiate data structure with code and Hash Map of features to FeatureProfile
     * objects calculated for the features.
     * TODO the hashmap reveals to much of the internal structure -> move Strubg FeatureProfile map into FeatureSpaceAnalyser?
     * @param code the code for which the datastructure contains data.
     * @param feMap the map of features to their data structures.
     */
    public CodeProfile(Code code, HashMap<String, FeatureProfile> feMap) {
        this.code = code;
        this.feMap = feMap;
        this.maxFeatureStringLength = maxStringLength(getFeatures());
        this.maxTotalFeatureCount = calcMaxTotalFeatureCount();
        this.maxInCodeFeatureCount = calcMaxInCodeFeatureCount();
    }

    /**
     * Calculates the max total feature count across all codes from the FeatureProfiles
     * upon instantiation of CodeProfile.
     * @return Integer.
     */
    private Integer calcMaxTotalFeatureCount() {
        int max= 0;
        for(String feature : getFeatures()){
            int inCodeFeatureCount = getProfile(feature).getCountInTotal();
            if(inCodeFeatureCount >max)
                max = inCodeFeatureCount;
        }
        return max;
    }

    /**
     * Calculates the max in code feature count from the FeatureProfiles
     * upon instantiation of CodeProfile.
     * @return int.
     */
    private int calcMaxInCodeFeatureCount() {
        int max= 0;
        for(String feature : getFeatures()){
            int inCodeFeatureCount = getProfile(feature).getInCodeFeatureCount();
            if(inCodeFeatureCount >max)
                max = inCodeFeatureCount;
        }
        return max;
    }

    /**
     * Gets the number of features associated with the code.
     * @return int.
     */
    public int size() {
        return feMap.size();
    }

    /**
     * Gets data structure containing further info on a feature's "profile" in
     * the data set.
     * @param feature feature of interest.
     * @return FeatureProfile data structure.
     */
    public FeatureProfile getProfile(String feature) {
        return feMap.get(feature);
    }

    /**
     * Gets the set of all features which predict this code from the data set.
     * @return Set<String>
     */
    public Set<String> getFeatures() {
        return feMap.keySet();
    }

    /**
     *      * ** used in formatting **
     * Gets the string length of the max in code feature count.
     * @return int.
     */
    public int getMaxInCodeFeatureCountLength() {
        return maxInCodeFeatureCount.toString().length();
    }

    /**
     *      * ** used in formatting **
     * Gets the string length of the max total feature count for the data set.
     * @return int.
     */
    public int getMaxTotalFeatureCountLength() {
        return maxTotalFeatureCount.toString().length();
    }


    /**
     *      * ** used in formatting **
     * Gets the string length of the feature with the longest
     * string length.
     * @return int.
     */
    public int getMaxFeatureStringLength() {
        return maxFeatureStringLength;
    }


    /**
     * Returns the max string length in an iterable of strings. TODO move to util class?
     * @param strings iterable of strings.
     * @return int.
     */
    private int maxStringLength(Iterable<String> strings) {
        int maxLength = 0;
        for (String line : strings) {
            if (line.length() > maxLength)
                maxLength = line.length();
        }
        return maxLength;
    }

    /**
     * Gets the code.
     * @return the code.
     */
    public Code getCode() {
        return code;
    }
}
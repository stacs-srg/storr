package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;

import java.util.HashMap;
import java.util.Set;

/**
 * Data structure for holding information on the feature distribution for a code.
 * FeatureSpaceAnalyser returns one of these objects when asked to getAnalysis for a code.
 * Created by fraserdunlop on 24/10/2014 at 12:27.
 */
public class CodeProfile {

    /**
     * The code.
     */
    private Code code;

    /**
     * Map of predicting features to data structures encoding information
     * on the feature profiles.
     */
    private final HashMap<String, FeatureProfile> feMap;

    /**
     * Instantiate data structure with code and Hash Map of features to FeatureProfile
     * objects calculated for the features.
     * TODO perhaps hashmap reveals to much of the internal structure? -> move String-FeatureProfile map into FeatureSpaceAnalyser?
     *
     * @param code  the code for which the datastructure contains data.
     * @param feMap the map of features to their data structures.
     */
    public CodeProfile(Code code, HashMap<String, FeatureProfile> feMap) {
        this.code = code;
        this.feMap = feMap;
    }

    /**
     * Gets the number of features associated with the code.
     *
     * @return int.
     */
    public int size() {
        return feMap.size();
    }

    /**
     * Gets data structure containing further info on a feature's "profile" in
     * the data set.
     *
     * @param feature feature of interest.
     * @return FeatureProfile data structure.
     */
    public FeatureProfile getProfile(String feature) {
        return feMap.get(feature);
    }

    /**
     * Gets the set of all features which predict this code from the data set.
     *
     * @return Set<String>
     */
    public Set<String> getFeatures() {
        return feMap.keySet();
    }

    /**
     * Gets the code.
     *
     * @return the code.
     */
    public Code getCode() {
        return code;
    }
}
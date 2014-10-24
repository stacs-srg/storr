package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * Created by fraserdunlop on 24/10/2014 at 12:27.
 */
public class CodeProfile {

    private final HashMap<String, FeatureProfile> feMap;
    private Integer maxInCodeFeatureCount;
    private Integer maxTotalFeatureCount;
    private Code code;
    private int maxFeatureStringLength;

    public CodeProfile(Code code, HashMap<String, FeatureProfile> feMap) {

        this.code = code;
        this.feMap = feMap;
        this.maxFeatureStringLength = maxLineLength(getFeatures());
        this.maxTotalFeatureCount = calcMaxTotalFeatureCount();
        this.maxInCodeFeatureCount = calcMaxInCodeFeatureCount();
    }

    private Integer calcMaxTotalFeatureCount() {
        int max= 0;
        for(String feature : getFeatures()){
            int inCodeFeatureCount = feMap.get(feature).getCountInTotal();
            if(inCodeFeatureCount >max)
                max = inCodeFeatureCount;
        }
        return max;
    }

    public int getMaxTotalFeatureCountLength() {
        return maxTotalFeatureCount.toString().length();
    }

    private int calcMaxInCodeFeatureCount() {
        int max= 0;
        for(String feature : getFeatures()){
            int inCodeFeatureCount = feMap.get(feature).getInCodeFeatureCount();
            if(inCodeFeatureCount >max)
                max = inCodeFeatureCount;
        }
        return max;
    }

    public int size() {
        return feMap.size();
    }

    public FeatureProfile getProfile(String feature) {
        return feMap.get(feature);
    }

    public Set<String> getFeatures() {
        return feMap.keySet();
    }

    public int getMaxInCodeFeatureCountLength() {
        return maxInCodeFeatureCount.toString().length();
    }


    public int getMaxFeatureStringLength() {
        return maxFeatureStringLength;
    }


    private int maxLineLength(Iterable<String> lines) {
        int maxLength = 0;
        for (String line : lines) {
            if (line.length() > maxLength)
                maxLength = line.length();
        }
        return maxLength;
    }

    public Code getCode() {
        return code;
    }
}
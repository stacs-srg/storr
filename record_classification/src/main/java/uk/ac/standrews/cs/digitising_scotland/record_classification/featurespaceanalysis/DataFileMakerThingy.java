package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;

/**
 * Created by fraserdunlop on 27/10/2014 at 18:39.
 */
public class DataFileMakerThingy {
    private final FeatureSpaceAnalyser featureSpaceAnalyser;
    private final static String delimiter = ",";

    public DataFileMakerThingy(FeatureSpaceAnalyser featureSpaceAnalyser) {
        this.featureSpaceAnalyser = featureSpaceAnalyser;
    }

    public String make(Code code, boolean headers, String level) {
        StringBuilder sb = new StringBuilder();
        CodeProfile codeProfile = featureSpaceAnalyser.getAnalysis(code);

        if(headers) {
            sb.append("feature").append(delimiter);
            sb.append("ff-icf").append(delimiter);
             sb.append("filename").append("\n");
        }
        for(String feature : codeProfile.getFeatures()){
            sb.append(feature).append(delimiter);
            sb.append(codeProfile.getProfile(feature).getFfIcf()).append(delimiter);
            sb.append(level).append("\n");
        }
        return sb.toString();
    }
}

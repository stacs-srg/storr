package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by fraserdunlop on 23/10/2014 at 14:37.
 */
public class FeatureSpaceAnalyserFormatter {

    private final FeatureSpaceAnalyser featureSpaceAnalyser;

    public FeatureSpaceAnalyserFormatter(final FeatureSpaceAnalyser featureSpaceAnalyser){
        this.featureSpaceAnalyser = featureSpaceAnalyser;
    }

    public String formatReport(Code code){
        if( featureSpaceAnalyser.contains(code)) {
            String codeAndDescription = buildCodeAndDescriptionLine(code);
            String[] featureLines = buildFeatureLines(code);
            return prettify(codeAndDescription, featureLines);
        } else {
            return "\nCode does not appear in training data.";
        }

    }

    private String prettify(String codeAndDescription, String[] featureLines) {
        int maxLine = maxLineLength(Arrays.asList(featureLines));
        String codeDescInfo = "(Code and Description)\n";
        String feaInfo = "(Feature count  ; associated with code ; in total)\n";
        if(codeAndDescription.length()>maxLine){
            maxLine = codeAndDescription.length();
        }
        if(codeDescInfo.length()>maxLine){
            maxLine = codeDescInfo.length();
        }
        if(feaInfo.length()>maxLine){
            maxLine = feaInfo.length();
        }
        String sepLine = makeSeparationLines(maxLine);
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(sepLine);
        sb.append(codeDescInfo);
        sb.append(codeAndDescription);
        sb.append(sepLine);
        sb.append(feaInfo);
        for (String line : featureLines){
            sb.append(line);
        }
        sb.append(sepLine);
        return sb.toString();
    }

    private String makeSeparationLines(int maxLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < maxLine ; i++){
            sb.append("-");
        }
        sb.append("\n");
        return sb.toString();
    }

    private int maxLineLength(Iterable<String> lines) {
        int maxLength = 0;
        for(String line : lines){
            if (line.length()>maxLength)
                maxLength = line.length();
        }
        return maxLength;
    }

    private int maxInCodeFeatureCountLength(Set<String> features, Code code) {
        Integer maxLength = 0;
        for(String feature : features){
            Integer numLength = featureSpaceAnalyser.featureProfile(code).get(feature);
            if (numLength > maxLength)
                maxLength = numLength;
        }
        return maxLength.toString().length();
    }

    private String[] buildFeatureLines(Code code) {
        Set<String> features = featureSpaceAnalyser.featureProfile(code).keySet();
        String[] featureLines = new String[features.size()];
        int offset = 8;
        int maxFeatureLength = maxLineLength(features);
        int maxInCodeFeatureCountLength = maxInCodeFeatureCountLength(features,code);
        int i = 0;
        for(String feature : features){
            int inCodeFeatureCount = featureSpaceAnalyser.featureProfile(code).get(feature);
            int countInTotal = featureSpaceAnalyser.featureDist(feature);
            featureLines[i++] = feature +
                                repeatConcatString(" ", maxFeatureLength - feature.length() + offset) +
                                inCodeFeatureCount +
                                repeatConcatString(" ",maxInCodeFeatureCountLength - inCodeFeatureCount + offset) +
                                countInTotal + "\n";
        }
        return featureLines;
    }


    protected String repeatConcatString(String str, int reps) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < reps ; i++){
            sb.append(str);
        }
        return sb.toString();
    }

    private String buildCodeAndDescriptionLine(Code code) {
        return code.getCodeAsString() + " - " + "\"" + code.getDescription() + "\"\n";
    }
}

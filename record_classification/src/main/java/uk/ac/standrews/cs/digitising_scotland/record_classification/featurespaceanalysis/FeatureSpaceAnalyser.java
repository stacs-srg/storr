package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


/**
 * builds a map of codes to counts of features which predict that code in the set
 * Created by fraserdunlop on 13/10/2014 at 15:59.
 */
public class FeatureSpaceAnalyser {

    HashMap<Code,HashMap<String,Integer>> featureProfiles;

    HashMap<String,Integer> featureDist;
    public FeatureSpaceAnalyser(Iterable<Record> records) {
        featureProfiles = new HashMap<>();
        featureDist = new HashMap<>();
       buildMap(records);
    }

    private void buildMap(Iterable<Record> records) {
        for (Record record : records){
            for(Classification classification : record.getGoldStandardClassificationSet()){
                addToFeatureProfiles(classification);
                addToFeatureDist(classification);
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

    public String formatReport(Code code){
        if( featureProfiles.containsKey(code)) {
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
        String feaInfo = "(Feature count with ; code ; other codes)\n";
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

    private String[] buildFeatureLines(Code code) {
        Set<String> features = featureProfiles.get(code).keySet();
        String[] featureLines = new String[features.size()];
        int maxTabDepth = (int) Math.ceil(maxLineLength(features)/4) + 3;
        int i = 0;
        for(String feature : features){
            featureLines[i++] = feature + getTabs(maxTabDepth - feature.length()/4) + "-\t" + featureProfiles.get(code).get(feature) + "\t-\t" + featureDist.get(feature) + "\n";
        }
        return featureLines;
    }

    private String getTabs(int tabDepth) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < tabDepth ; i++){
            sb.append("\t");
        }
        return sb.toString();
    }

    private String buildCodeAndDescriptionLine(Code code) {
        return code.getCodeAsString() + " - " + "\"" + code.getDescription() + "\"\n";
    }

}

package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 *
 * Created by fraserdunlop on 23/10/2014 at 14:37.
 */
public class CodeReportFormatter {

    DecimalFormat df = new DecimalFormat("0.000");
    private final FeatureSpaceAnalyser featureSpaceAnalyser;
    private final static int offset = 2;

    public CodeReportFormatter(final FeatureSpaceAnalyser featureSpaceAnalyser) {
        this.featureSpaceAnalyser = featureSpaceAnalyser;
    }

    /**
     * Formats a report designed to be printed in the terminal.
     * Used by Interactive Analyser to format reports for command
     * line exploration of data set.
     * @param code the code that we want information on.
     * @return a report detailing the features in the dataset which predict
     * the code, their "Feature Frequency - Inverse Code Frequency" scores,
     * the proportion of the features occurrence in predicting the code and
     * a list of other codes predicted by the feature along with their
     * associated proportions.
     * @throws Exception
     */
    public String formatReport(Code code) throws Exception {
        if (featureSpaceAnalyser.contains(code)) {
            String codeAndDescription = buildCodeAndDescriptionLine(code);
            CodeProfile profile = featureSpaceAnalyser.getAnalysis(code);
            String[] featureHeaders = getFeatureHeaders();
            String[][] content = buildContent(profile);
            String[] featureLines = buildFeatureLines2(featureHeaders, offset, content);
            return prettify(codeAndDescription, featureLines);
        } else {
            return "\nCode does not appear in training data.";
        }

    }

    public boolean isFeature(final String feature){
        return featureSpaceAnalyser.isFeature(feature);
    }

    private String prettify(String codeAndDescription, String[] featureLines) {
        int sepLineLength = maxLineLength(Arrays.asList(featureLines));

        String sepLine = makeSeparationLines(sepLineLength);
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(sepLine);
        sb.append(codeAndDescription);
        sb.append(sepLine);
        for (String line : featureLines) {
            sb.append(line);
        }
        sb.append(sepLine);
        return sb.toString();
    }

    private String makeSeparationLines(int maxLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLine; i++) {
            sb.append("-");
        }
        sb.append("\n");
        return sb.toString();
    }

    private int maxLineLength(Iterable<String> lines) {
        int maxLength = 0;
        for (String line : lines) {
            if (line.length() > maxLength)
                maxLength = line.length();
        }
        return maxLength;
    }


    private String[][] buildContent(CodeProfile profile) {
        int i = 0;
        String[][] content = new String[profile.getFeatures().size()][];
        for(String feature : profile.getFeatures()){
            content[i++] = buildLineContent(profile, feature);
        }
        return content;
    }

    private String[] buildLineContent(CodeProfile profile, String feature) {
        String[] line = new String[4];
        FeatureProfile featureProfile = profile.getProfile(feature);
        line[0] = feature;
        line[1] = getFeatureFreqInvCodeFreq(featureProfile);
        line[2] = getProportionInCode(featureProfile);
        line[3] = formatCodes(featureProfile,profile.getCode());
        return line;
    }

    private String[] getFeatureHeaders() {
        String[] headers = new String[4];
        headers[0] = "feature";
        headers[1] = "ff-idf";
        headers[2] = "proportion instances associated with code";
        headers[3] = "occurrence in other codes (ff-icf, proportion)";
        return headers;
    }

    private String getFeatureFreqInvCodeFreq(FeatureProfile featureProfile) {
        return String.valueOf(df.format(featureProfile.getFfIcf()));
    }

    private String getProportionInCode(FeatureProfile featureProfile) {
        Integer inCodeFeatureCount = featureProfile.getInCodeFeatureCount();
        Integer countInTotal = featureProfile.getCountInTotal();
        return inCodeFeatureCount + "/" + countInTotal;
    }

    private String[] buildFeatureLines2(String[] headers,int offset ,String[][] content) throws Exception {
        String[][] headAndContent = attachHeaders(headers, content);
        String[] lines = new String[headAndContent.length];
        for(int i = 0 ; i < headAndContent.length ; i++){
            lines[i] = buildLine(i,headAndContent, offset);
        }
        return lines;
    }

    private String buildLine(int i, String[][] headAndContent, int offset) {
        String[] lineData = headAndContent[i];
        StringBuilder sb = new StringBuilder();
        for(int j = 0 ; j < lineData.length ; j++){
            sb.append(lineData[j]);
            if(j<(lineData.length-1)) {
                sb.append(padWithSpace(i, j, headAndContent));
                sb.append(FormattingUtils.paddedSeparator("|", offset));
            } else {
                sb.append("\n");
                if(i==0) sb.append("\n");
            }

        }
        return sb.toString();
    }

    private String padWithSpace(int i, int j, String[][] headAndContent) {
        int maxLengthInCol = getMaxLengthInCol(j, headAndContent);
        return FormattingUtils.repeatConcatString(" ", maxLengthInCol - headAndContent[i][j].length());
    }

    private int getMaxLengthInCol(int j, String[][] headAndContent) {
        int max = 0;
        for(String[] line : headAndContent){
            int length = line[j].length();
            if(length >max)
                max = length;
        }
        return max;
    }

    private String[][] attachHeaders(String[] headers, String[][] content) throws Exception {
        if(headers.length!=content[0].length)
            throw new Exception("header/content mismatch");
        String[][] headAndContent = new String[content.length+1][headers.length];

        int i = 0;
        headAndContent[i++] = headers;
        for(String[] line : content)
            headAndContent[i++] = line;
        return headAndContent;
    }

    private String formatCodes(FeatureProfile profile,Code currentCode) {
        StringBuilder sb = new StringBuilder();
        for(Code code : profile.getProfile().keySet()){
            if(!code.equals(currentCode)) {
                sb.append(code.getCodeAsString());
                FeatureProfile profile1 = featureSpaceAnalyser.getAnalysis(code).getProfile(profile.getFeature());
                sb.append(" (");
                sb.append(df.format(featureSpaceAnalyser.featureFrequencyInverseCodeFrequency(code,profile.getFeature())));
                sb.append(", ");
                sb.append(profile1.getInCodeFeatureCount());
                sb.append("/");
                sb.append(profile1.getCountInTotal());
                sb.append("), ");
            }
        }
        return FormattingUtils.rmLastChar(sb.toString());
    }


    private String buildCodeAndDescriptionLine(Code code) {
        return code.getCodeAsString() + " - " + "\"" + code.getDescription() + "\"\n";
    }
}

package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;

import java.util.Arrays;

/**
 *
 * Created by fraserdunlop on 23/10/2014 at 14:37.
 */
public class FeatureSpaceAnalyserFormatter {

    private final FeatureSpaceAnalyser featureSpaceAnalyser;
    private final static int offset = 8;

    public FeatureSpaceAnalyserFormatter(final FeatureSpaceAnalyser featureSpaceAnalyser) {
        this.featureSpaceAnalyser = featureSpaceAnalyser;
    }

    public String formatReport(Code code) {
        if (featureSpaceAnalyser.contains(code)) {
            String codeAndDescription = buildCodeAndDescriptionLine(code);
            CodeProfile profile = featureSpaceAnalyser.getAnalysis(code);
            String[] featureHeaders = getFeatureHeaders();
            String[] featureLines = buildFeatureLines(profile, offset, featureHeaders);
            return prettify(codeAndDescription, featureLines,profile);
        } else {
            return "\nCode does not appear in training data.";
        }

    }

    public String[] getFeatureHeaders() {
        String[] headers = new String[3];
        headers[0] = "feature";
        headers[1] = "proportion instances associated with code";
        headers[2] = "occurrence in other codes (proportion of)";
        return headers;
    }

    private String prettify(String codeAndDescription, String[] featureLines,CodeProfile profile) {
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

    private String[] buildFeatureLines(CodeProfile profile, int offset,String[] headers) {
        int i = 0;
        String[] featureLines = new String[profile.size()+1];
        int padToFirstCol = Math.max(headers[0].length(), profile.getMaxFeatureStringLength());
        int padToSecondCol = Math.max(headers[1].length(),
                profile.getMaxInCodeFeatureCountLength()+ profile.getMaxTotalFeatureCountLength()+1+2*offset);



        featureLines[i++] = headers[0] +
                repeatConcatString(" ", padToFirstCol - headers[0].length()) +
                paddedSeparator("/", offset) +
                headers[1] +
                repeatConcatString(" ", padToSecondCol - headers[1].length()) +
                paddedSeparator("/", offset) +
                headers[2] + "\n";





        for (String feature : profile.getFeatures()) {
            FeatureProfile featureProfile = profile.getProfile(feature);
            Integer inCodeFeatureCount = featureProfile.getInCodeFeatureCount();
            Integer countInTotal = featureProfile.getCountInTotal();
            featureLines[i++] =
                    feature +
                    repeatConcatString(" ", padToFirstCol - feature.length()) +
                    paddedSeparator("/", offset) +
                    inCodeFeatureCount + "/" + countInTotal +
                    repeatConcatString(" ", padToSecondCol - inCodeFeatureCount.toString().length() -countInTotal.toString().length()-1) +
                    paddedSeparator("/", offset) +
                    formatCodes(featureProfile,profile.getCode()) +
                     "\n";
        }
        return featureLines;
    }

    private String formatCodes(FeatureProfile profile,Code currentCode) {
        StringBuilder sb = new StringBuilder();
        for(Code code : profile.getProfile().keySet()){
            if(!code.equals(currentCode)) {
                sb.append(code.getCodeAsString());
                FeatureProfile profile1 = featureSpaceAnalyser.getAnalysis(code).getProfile(profile.getFeature());
                sb.append(" (");
                sb.append(profile1.getInCodeFeatureCount());
                sb.append("/");
                sb.append(profile1.getCountInTotal());
                sb.append("), ");
            }
        }
        return rmLastChar(sb.toString());
    }


    private String rmLastChar(String string) {
        if(string.length()>2)
            return string.subSequence(0, string.length() - 2).toString();
        else return string;
    }

    private String paddedSeparator(String sep, int padEachSideBy) {
        String pad = repeatConcatString(" ", padEachSideBy);
        return pad + sep + pad;
    }

    protected String repeatConcatString(String str, int reps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reps; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    private String buildCodeAndDescriptionLine(Code code) {
        return code.getCodeAsString() + " / " + "\"" + code.getDescription() + "\"\n";
    }
}

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

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 *
 * Created by fraserdunlop on 27/10/2014 at 15:22.
 */
public class FeatureReportFormatter {
    private final FeatureSpaceAnalyser featureSpaceAnalyser;
    DecimalFormat df = new DecimalFormat("0.000");
    private final static int offset = 2;


    public FeatureReportFormatter(FeatureSpaceAnalyser featureSpaceAnalyser) {
        this.featureSpaceAnalyser = featureSpaceAnalyser;
    }

    public String formatReport(String feature) throws Exception {

        String[] codeHeaders = getCodeHeaders();
        String[][] content = buildContent(feature);
        String[] lines = buildLines(codeHeaders,content);


        return prettify(feature,lines);
    }

    private String prettify(String feature, String[] lines) {
        int sepLineLength = FormattingUtils.maxLineLength(Arrays.asList(lines));
        String sepLine = FormattingUtils.makeSeparationLines(sepLineLength);
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(sepLine);
        sb.append("\"").append(feature).append("\"");
        sb.append("\n");
        sb.append(sepLine);
        for (String line : lines) {
            sb.append(line);
        }
        sb.append(sepLine);
        return sb.toString();
    }

    private String[] buildLines(String[] headers, String[][] content) throws Exception {
        String[][] headAndContent = FormattingUtils.attachHeaders(headers, content);
        String[] lines = new String[headAndContent.length];
        for(int i = 0 ; i < headAndContent.length ; i++){
            lines[i] = FormattingUtils.buildLine(i, headAndContent, offset);
        }
        return lines;
    }

    private String[][] buildContent(final String feature) {
        int i = 0;
        String[][] content = new String[featureSpaceAnalyser.getNumberOfCodesContainingFeature(feature)][];
        for(Code code : featureSpaceAnalyser.codeProfile(feature).keySet()){
            content[i++] = buildLineContent(code,feature);
        }
        return content;
    }

    private String[] buildLineContent(Code code, String feature) {
        String[] line = new String[2];
        line[0] = code.getCodeAsString() + " - " + "\"" + code.getDescription()  + "\"";
        line[1] = df.format(featureSpaceAnalyser.getAnalysis(code).getProfile(feature).getFfIcf());
        return line;
    }

    public String[] getCodeHeaders() {
         String[] headers = new String[2];
        headers[0] = "code";
        headers[1] = "ff-idf";
        return headers;
    }
}

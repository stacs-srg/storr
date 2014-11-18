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

/**
 *
 * Created by fraserdunlop on 27/10/2014 at 12:24.
 */
public class FormattingUtils {

    public static String rmLastChar(final String string) {
        if(string.length()>2)
            return string.subSequence(0, string.length() - 2).toString();
        else return string;
    }
    public static String paddedSeparator(final String sep,final int padEachSideBy) {
        String pad = repeatConcatString(" ", padEachSideBy);
        return pad + sep + pad;
    }

    public static String repeatConcatString(final String str,final int reps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reps; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static String[][] attachHeaders(String[] headers, String[][] content) throws Exception {
        if(headers.length!=content[0].length)
            throw new Exception("header/content mismatch");
        String[][] headAndContent = new String[content.length+1][headers.length];

        int i = 0;
        headAndContent[i++] = headers;
        for(String[] line : content)
            headAndContent[i++] = line;
        return headAndContent;
    }


    public static String buildLine(int i, String[][] headAndContent, int offset) {
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

    public static String makeSeparationLines(int maxLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLine; i++) {
            sb.append("-");
        }
        sb.append("\n");
        return sb.toString();
    }



    public static int maxLineLength(Iterable<String> lines) {
        int maxLength = 0;
        for (String line : lines) {
            if (line.length() > maxLength)
                maxLength = line.length();
        }
        return maxLength;
    }

    private static String padWithSpace(int i, int j, String[][] headAndContent) {
        int maxLengthInCol = getMaxLengthInCol(j, headAndContent);
        return FormattingUtils.repeatConcatString(" ", maxLengthInCol - headAndContent[i][j].length());
    }

    private static int getMaxLengthInCol(int j, String[][] headAndContent) {
        int max = 0;
        for(String[] line : headAndContent){
            int length = line[j].length();
            if(length >max)
                max = length;
        }
        return max;
    }
}

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
package uk.ac.standrews.cs.digitising_scotland.record_classification.postrocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

import com.google.common.base.Charsets;

/**
 * The Class CodeDescritionChanger is a legacy class that was used to check the effect of coding to
 *  differing levels in the heirarchy when using HISCO codes.
 */
public class CodeDescritionChanger {

    /** The code mapping. */
    private HashMap<String, String> codeMapping;
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeDescritionChanger.class);

    /**
     * Main method. Runs the code description changer on file "outputFile.csv".
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {

        CodeDescritionChanger cdc = new CodeDescritionChanger();
        try {
            cdc.changeDescriptionToCode(new File("outputFile.csv"), new File(""));
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }

    }

    /**
     * Change description to code.
     *
     * @param inputFile the input file
     * @param base the base
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public File changeDescriptionToCode(final File inputFile, final File base) throws IOException {

        File buildCodesFromThis = new File("hiscoMapping.txt");
        //codeMapping = new HashMap<String, String>();
        codeMapping = getCodes(buildCodesFromThis);
        File outputFile = new File(base.getAbsolutePath() + "/outputFileCoded.csv");
        StringBuilder sb = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), Charsets.UTF_8));
        String line = "";

        while ((line = reader.readLine()) != null) {
            String[] lineSplit = line.split(Utils.getCSVComma());
            for (int i = 0; i < lineSplit.length; i++) {
                if (i == 1 || i == 2 || i == 4 || i == 7) {

                    lineSplit[i] = codeMapping.get(lineSplit[i]);
                }
                sb.append(lineSplit[i] + ",");
            }
            sb.append("\n");
        }

        reader.close();
        LOGGER.info("Writing to file: " + outputFile.getAbsolutePath());
        Utils.writeToFile(sb.toString(), outputFile.getAbsolutePath());

        return outputFile;
    }

    /**
     * Gets the codes.
     *
     * @param buildCodesFromThis the build codes from this
     * @return the codes
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private HashMap<String, String> getCodes(final File buildCodesFromThis) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(buildCodesFromThis), "UTF8"));
        String line = "";

        while ((line = reader.readLine()) != null) {
            if (line.contains("Shipsï¿½ï¿½_ Deck Ratings, Barge Crews and Boatmen")) {
                LOGGER.info("problem here");
            }
            String[] lineSplit = line.split("\t");
            codeMapping.put(lineSplit[1], lineSplit[0]);
        }

        reader.close();

        return codeMapping;
    }
}

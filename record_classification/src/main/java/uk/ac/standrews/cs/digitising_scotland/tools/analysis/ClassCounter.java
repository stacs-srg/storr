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
package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Provides methods for counting the number of output classes in a set of input data contained in an tab separated txt
 * file.
 * 
 * @author jkc25
 */
public class ClassCounter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassCounter.class);

    private File input;
    private int classColumn = 0;
    private HashMap<String, String> outputClasses;

    /**
     * Constructs a ClassCounter class with the input file and the column containing the classes you want to count.
     * 
     * @param input
     *            Input file containing the data.
     * @param classColumn
     *            The column in the datafile with the classes you want to count.
     */
    public ClassCounter(final File input, final int classColumn) {

        this.input = input;
        this.classColumn = classColumn;
    }

    /**
     * Constructs a ClassCounter class with the input file and the column containing the classes you want to count.
     * 
     * @param input
     *            Input file containing the data. Default column is used, 0.
     */
    public ClassCounter(final File input) {

        this.input = input;
    }

    /**
      * Counts the number of different output classes in the file.
      * 
      * @return Number of output classes.
      */
    public int count() {

        int numberOfClasses = 0;
        String line = "";
        String[] part;
        String classification = "";
        outputClasses = new HashMap<String, String>();
        String fileType = getFileType();
        BufferedReader br = null;
        try {

            input = new File(input.getAbsolutePath());

            br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF8"));

            while ((line = br.readLine()) != null) {
                if (fileType.equalsIgnoreCase("txt")) {
                    part = line.split("\t");
                }
                else if (fileType.equalsIgnoreCase("csv")) {
                    part = line.split(Utils.getCSVComma());
                }
                else {
                    return -1;
                }

                if (part.length > 0) {
                    classification = part[classColumn];
                    outputClasses.put(classification, classification);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.error("There is a problem with your input file. \n " + "Please ensure it is either tab serperated or a csv file \n" + "and that you have supplied the correct coloumn for your class");

            LOGGER.error(e.getMessage(), e.getCause());

        }
        catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e.getCause());

        }
        finally {
            closeReader(br);
        }

        numberOfClasses = outputClasses.size();

        return numberOfClasses;
    }

    private void closeReader(final BufferedReader br) {

        if (br != null) {
            try {
                br.close();
            }
            catch (IOException e) {
                LOGGER.error(e.getMessage(), e.getCause());

            }
        }
    }

    private String getFileType() {

        String fileName = input.getName();
        final int extensionLength = 3;
        if (fileName.substring(fileName.length() - extensionLength, fileName.length()).equalsIgnoreCase("txt")) { return "txt"; }
        if (fileName.substring(fileName.length() - extensionLength, fileName.length()).equalsIgnoreCase("csv")) { return "csv"; }

        return "unknown";
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {

        ClassCounter c = new ClassCounter(new File("kilm8000.txt"));
        LOGGER.info("Classes in file: " + Integer.valueOf(c.count()));

    }

}

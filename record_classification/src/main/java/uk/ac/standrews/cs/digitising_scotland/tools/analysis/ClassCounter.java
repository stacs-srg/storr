/*
 * | ______________________________________________________________________________________________ | Understanding
 * Scotland's People (USP) project. | | The aim of the project is to produce a linked pedigree for all publicly | |
 * available Scottish birth/death/marriage records from 1855 to the present day. | | | | Digitization of the records is
 * being carried out by the ESRC-funded Digitising | | Scotland project, run by University of St Andrews and National
 * Records of Scotland. | | | | The project is led by Chris Dibben at the Longitudinal Studies Centre at St Andrews. | |
 * The other project members are Lee Williamson (also at the Longitudinal Studies Centre) | | Graham Kirby, Alan Dearle
 * and Jamie Carson at the School of Computer Science at St Andrews; | | and Eilidh Garret and Alice Reid at the
 * Department of Geography at Cambridge. | | | |
 * ______________________________________________________________________________________________
 */
package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Provides methods for counting the number of output classes in a set of input data contained in an tab separated txt
 * file.
 * 
 * @author jkc25
 */
public class ClassCounter {

    private File input;
    private int classColumn = 0;
    private HashMap<String, String> outputClasses;

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {

        ClassCounter c = new ClassCounter(new File("kilm8000.txt"));
        System.out.println(c.count());

    }

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
            System.out.println("There is a problem with your input file. \n " + "Please ensure it is either tab serperated or a csv file \n" + "and that you have supplied the correct coloumn for your class");

            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        numberOfClasses = outputClasses.size();

        return numberOfClasses;
    }

    private String getFileType() {

        String fileName = input.getName();
        final int extensionLength = 3;
        if (fileName.substring(fileName.length() - extensionLength, fileName.length()).equalsIgnoreCase("txt")) { return "txt"; }
        if (fileName.substring(fileName.length() - extensionLength, fileName.length()).equalsIgnoreCase("csv")) { return "csv"; }

        return "unknown";
    }

}

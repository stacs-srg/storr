package uk.ac.standrews.cs.digitising_scotland.tools.fileutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

/**
 * Reads a file in the NRS data transfer format and converts ICD10 codes to codes described in anothe file.
 * @author jkc25
 *
 */
public class ICDCodeConverter {

    public static void main(final String[] args) throws IOException {

        File inputFile = new File("NRSdata.txt");
        File outputFile = new File("convertedNRSdata.txt");
        File mappingFile = new File("mappingFile.txt");

        ICDCodeConverter.convert(inputFile, outputFile, mappingFile);

    }

    private static void convert(final File inputFile, final File outputFile, final File mappingFile) throws IOException {

        HashMap<String, String> map = buildMapping(mappingFile);

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));

        String str;
        StringBuilder sb = new StringBuilder();

        while ((str = in.readLine()) != null) {
            String newLine = getNewLine(str, map);
            sb.append(newLine + "\n");
        }
        write(sb.toString(), outputFile);
        in.close();
    }

    private static HashMap<String, String> buildMapping(final File mappingFile) throws IOException {

        HashMap<String, String> map = new HashMap<>();

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(mappingFile), "UTF8"));

        String str;

        while ((str = in.readLine()) != null) {
            String oldValue = str.split("\t")[0];
            String newValue = str.split("\t")[1] + "\t" + str.split("\t")[2];
            oldValue = oldValue.replaceAll("\\.", "");
            map.put(oldValue, newValue);
        }
        in.close();

        return map;

    }

    private static String getNewLine(final String str, final HashMap<String, String> map) {

        String[] splitString = str.split("\t");
        String[] splitStringNew = splitString.clone();
        String newLine = "";

        if (splitString.length > 5) {

            for (int i = 5; i < splitStringNew.length; i = i + 3) {

                String old = splitString[i];

                String newCodeAndDesc = map.get(old);

                if (newCodeAndDesc != null) {
                    String newCode = newCodeAndDesc.split("\t")[0];
                    String newDesc = newCodeAndDesc.split("\t")[1];
                    splitStringNew[i] = newCode;
                    splitStringNew[i + 1] = newDesc;

                }
                else {
                    System.err.println(old + " isn't in map... record id: " + splitString[0]);
                }

            }
            for (String string : splitStringNew) {
                newLine += string + "\t";
            }
            return newLine;

        }
        return str;

    }

    private static void write(final String newLine, final File outputFile) throws IOException {

        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile.getAbsoluteFile()), "UTF-8"));
        try {
            out.write(newLine + "\t");
        }
        finally {
            try {
                out.close();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}

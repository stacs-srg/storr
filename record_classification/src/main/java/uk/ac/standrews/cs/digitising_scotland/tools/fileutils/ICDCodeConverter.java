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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;

/**
 * Reads a file in the NRS data transfer format and converts ICD10 codes to codes described in another file.
 * @author jkc25
 *
 */
public class ICDCodeConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ICDCodeConverter.class);

    public static void main(final String[] args) throws IOException {

        File inputFile = new File("NRSDataEditedTab.txt");
        File outputFile = new File("convertedNRSdata.txt");
        File mappingFile = new File("mappingFile.txt");

        ICDCodeConverter.convert(inputFile, outputFile, mappingFile);

    }

    private ICDCodeConverter() {

    }

    private static void convert(final File inputFile, final File outputFile, final File mappingFile) throws IOException {

        Map<String, String> map = buildMapping(mappingFile);

        BufferedReader in = ReaderWriterFactory.createBufferedReader(inputFile);

        String str;
        StringBuilder sb = new StringBuilder();

        while ((str = in.readLine()) != null) {
            String newLine = getNewLine(str, map);
            sb.append(newLine + "\n");
        }
        write(sb.toString(), outputFile);
        in.close();
    }

    private static Map<String, String> buildMapping(final File mappingFile) throws IOException {

        Map<String, String> map = new HashMap<>();

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

    private static String getNewLine(final String str, final Map<String, String> map) {

        String[] splitString;
        splitString = str.split("\t");
        String[] splitStringNew = splitString.clone();
        StringBuilder sb = new StringBuilder();

        if (splitString.length >= 6) {

            for (int i = 5; i < splitStringNew.length; i = i + 2) {

                String old = splitString[i];
                String newCodeAndDesc = map.get(old);

                if (newCodeAndDesc != null) {
                    setNewString(splitStringNew, i, newCodeAndDesc);
                }
                else {
                    checkWithoutDot(map, splitString, splitStringNew, i, old);
                }

            }

            for (String string : splitStringNew) {
                sb.append(string + "\t");
            }

            return sb.toString();

        }
        return str;

    }

    private static void checkWithoutDot(final Map<String, String> map, String[] splitString, String[] splitStringNew, int i, String old) {

        String newCodeAndDesc;
        if ((newCodeAndDesc = map.get(old.replaceAll("\\.", ""))) != null) {
            setNewString(splitStringNew, i, newCodeAndDesc);
        }
        else {
            LOGGER.error(old + " isn't in map... record id: " + splitString[1]);

        }
    }

    private static void setNewString(final String[] splitStringNew, final int i, final String newCodeAndDesc) {

        String newCode = newCodeAndDesc.split("\t")[0];
        splitStringNew[i] = newCode;
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
                LOGGER.error(e.getMessage(), e);
            }
        }

    }

}

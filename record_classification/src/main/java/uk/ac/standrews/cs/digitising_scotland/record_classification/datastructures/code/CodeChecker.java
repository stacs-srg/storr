package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

public class CodeChecker {

    private Map<String, String> validCodes;

    public CodeChecker(final File correctCodes) throws IOException {

        validCodes = new HashMap<>();
        validCodes = initMap(correctCodes);
    }

    private Map<String, String> initMap(final File correctCodes) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(correctCodes), FileManipulation.FILE_CHARSET));
        String line;

        while ((line = br.readLine()) != null) {
            parseLineAndAddToMap(line);
        }

        br.close();

        return validCodes;
    }

    private void parseLineAndAddToMap(final String line) {

        String[] splitLine = line.split("\t");
        String codeFromFile = splitLine[0].trim();
        String descriptionFromFile = splitLine[1].trim();
        validCodes.put(codeFromFile, descriptionFromFile);
    }

    public boolean isValidCode(final String code) {

        return validCodes.containsKey(code);
    }

    public int getTotalNumberOfCodes() {

        return validCodes.size();
    }

}

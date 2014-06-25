package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

public class FormatConverter {

    public static List<Record> convert(File inputFile) throws IOException, InputFormatException {

        File codeFile = new File("/Users/fraserdunlop/IdeaProjects/digitising_scotland/record_classification/ModData/testCodeMap.txt");
        CodeFactory.getInstance().loadDictionary(codeFile);

        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line = "";
        List<Record> recordList = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split(Utils.getCSVComma());

            checkLineLength(lineSplit);

            int id = Integer.parseInt(lineSplit[0]);
            int imageQuality = 1;
            int ageGroup = convertAgeGroup(removeQuotes(lineSplit[34]));
            int sex = convertSex(removeQuotes(lineSplit[35]));
            String description = formDescription(lineSplit, 1, 4);
            int year = Integer.parseInt(removeQuotes(lineSplit[37]));
            CODOrignalData originalData = new CODOrignalData(description, year, ageGroup, sex, imageQuality, inputFile.getName());
            HashSet<CodeTriple> goldStandard = new HashSet<>();

            for (int i = 6; i < 31; i = i + 3) {
                if (lineSplit[i].length() != 0) {
                    int causeIdentifier = Integer.parseInt(lineSplit[i]);

                    if (causeIdentifier != 6) {
                        Code code = CodeFactory.getInstance().getCode(removeQuotes(lineSplit[i + 2]));

                        TokenSet tokenSet = new TokenSet(lineSplit[causeIdentifier]);

                        CodeTriple codeTriple = new CodeTriple(code, tokenSet, 1.0);
                        goldStandard.add(codeTriple);
                    }
                }
            }

            Record r = new Record(originalData);
            r.getOriginalData().setGoldStandardClassification(goldStandard);
            recordList.add(r);
        }
        br.close();
        return recordList;
    }

    private static String formDescription(String[] lineSplit, int i, int j) {

        String description = "";

        for (int k = i; k <= j; k++) {
            if (lineSplit[k].length() != 0) {
                if (k != i) {
                    description = description + "," + lineSplit[k];
                }
                else {
                    description = lineSplit[k];
                }
            }
        }

        return description;

    }

    private static int convertAgeGroup(String lineSplit) {

        int group = Integer.parseInt(lineSplit);
        if (group > 5) return 5;

        return group;
    }

    private static int convertSex(String lineSplit) {

        if (lineSplit.equals("M")) { return 1; }
        return 0;
    }

    private static String removeQuotes(String string) {

        string = string.replaceAll("\"", "").trim();

        return string;
    }

    private static void checkLineLength(String[] lineSplit) {

        if (lineSplit.length != 38) {
            System.err.println("Line is wrong length, should be 38, is " + lineSplit.length);
        }
    }

}

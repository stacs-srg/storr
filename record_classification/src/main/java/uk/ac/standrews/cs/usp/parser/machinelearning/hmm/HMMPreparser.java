package uk.ac.standrews.cs.usp.parser.machinelearning.hmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.standrews.cs.usp.tools.Utils;

public class HMMPreparser {

    public File prepareFile(final File input, final File output) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF8"));
        String line = "";

        while ((line = reader.readLine()) != null) {
            String preparedLine = seperatePuncutation(line);
            preparedLine = tokenise(preparedLine);
            Utils.writeToFile(preparedLine, output.getAbsolutePath(), true);
        }
        reader.close();
        return output;

    }

    protected String splitIntoMultipuleLines(final ArrayList<String> preparedLine) {

        StringBuilder sb = new StringBuilder();
        for (String string : preparedLine) {
            sb.append(string + "\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    protected String tokenise(final String preparedLine) {

        StringTokenizer st = new StringTokenizer(preparedLine);
        ArrayList<String> tokenisedLine = new ArrayList<String>();
        System.out.println("---- Split by space ------");
        while (st.hasMoreElements()) {
            tokenisedLine.add(st.nextElement().toString());
        }

        return splitIntoMultipuleLines(tokenisedLine);
    }

    protected String seperatePuncutation(final String line) {

        System.out.println(line);
        String newLine = removeQuotes(line);
        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile("[;,&12/]").matcher(newLine);

        while (m.find()) {
            allMatches.add(m.group());
        }

        for (int i = 0; i < allMatches.size(); i++) {
            newLine = newLine.replaceAll(allMatches.get(i), " " + allMatches.get(i));
        }

        System.out.println(newLine);

        return newLine.trim();
    }

    protected String removeQuotes(final String line) {

        return line.replaceAll("\"", "");
    }
}

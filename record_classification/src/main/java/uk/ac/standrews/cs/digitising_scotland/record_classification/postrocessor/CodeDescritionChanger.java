package uk.ac.standrews.cs.digitising_scotland.record_classification.postrocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

import com.google.common.base.Charsets;

/**
 * The Class CodeDescritionChanger is a legacy class that was used to check the effect of coding to
 *  differing levels in the heirarchy when using HISCO codes.
 */
public class CodeDescritionChanger {

    /** The code mapping. */
    private HashMap<String, String> codeMapping;

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
            e.printStackTrace();
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
        System.out.println("Writing to file: " + outputFile.getAbsolutePath());
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
                System.out.println("problem here");
            }
            String[] lineSplit = line.split("\t");
            codeMapping.put(lineSplit[1], lineSplit[0]);
        }

        reader.close();

        return codeMapping;
    }
}

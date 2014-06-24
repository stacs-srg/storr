package uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * Factory for the creation of {@link Code} objects.
 * Codes can only be created if they are within a pre-determined list of codes, this prevents
 * malformed codes being used for classification.
 *
 * @author jkc25, frjd2
 */
public final class CodeFactory {

    private static final CodeFactory INSTANCE = new CodeFactory();
    private HashMap<String, Code> codeMap = new HashMap<String, Code>();
    private HashMap<Integer, Code> idToCodeMap = new HashMap<Integer, Code>();
    private boolean doOnce = true;
    private File inputFile;
    private int currentMaxID;
    private static final String ENCODING = "UTF-8";

    private CodeFactory() {

        Properties properties = MachineLearningConfiguration.getDefaultProperties();
        String property = properties.getProperty("codeDictionaryFile");
        String path = System.getProperty("user.dir") + "/" + property;
        inputFile = new File(path);
    }

    /**
     * Returns the single instance of this class that is being used.
     *
     * @return The instance of this class
     */
    public static CodeFactory getInstance() {

        return INSTANCE;
    }

    /**
     * Gets a code object from the code mapping in memory.
     *
     * @param code String representation of the {@link Code}.
     * @return {@link Code} with code as String and description as String.
     */
    public Code getCode(final String code) {

        if (doOnce) {
            tryInitCodeMap();
        }
        return tryGetCodeFromMap(code);
    }

    /**
     * Returns the code that this id is mapped to.
     * @param id associated with mapped code
     * @return Code assosciated with this id
     */
    public Code getCode(final Integer id) {

        if (doOnce) {
            tryInitCodeMap();
        }
        return idToCodeMap.get(id);
    }

    private void tryInitCodeMap() {

        try {
            initCodeMap();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (CodeNotValidException e) {
            e.printStackTrace();
        }
    }

    private Code tryGetCodeFromMap(final String code) {

        Code codeFromMap = null;
        try {
            codeFromMap = getCodeFromMap(code);
        }
        catch (CodeNotValidException e) {
            e.printStackTrace();
        }
        return codeFromMap;
    }

    protected Code getCodeFromMap(final String code) throws CodeNotValidException {

        Code codeFromMap = codeMap.get(code);
        if (codeFromMap == null) { throw new CodeNotValidException(code + " is not a valid code, or is not in the code dictionary"); }
        return codeFromMap;
    }

    private void initCodeMap() throws IOException, CodeNotValidException {

        doOnce = false;

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), ENCODING));
        String line;

        boolean isCoDCode = isCauseOfDeath();

        if (isCoDCode) {
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split("\t");
                String codeFromFile = splitLine[0].trim();
                String descriptionFromFile = splitLine[1].trim();

                putCoDCodeInMap(codeFromFile, descriptionFromFile);

            }
        }
        else {

            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split("\t");
                String codeFromFile = splitLine[0].trim();
                String descriptionFromFile = splitLine[1].trim();

                putOccCodeInMap(codeFromFile, descriptionFromFile);

            }

        }

        br.close();

    }

    private boolean isCauseOfDeath() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), ENCODING));
        String firstLine = br.readLine();
        br.close();
        if (firstLine == null) { return false; }
        if (firstLine.substring(0, 1).matches("[a-zA-Z]")) { return true; }

        return false;
    }

    /**
     * Loads a <b>new</b> set of codes in to the code factory.
     * @param codeFile File with set of tab separated codes and descriptions. 1 per line.
     */
    public void loadDictionary(final File codeFile) {

        resetParams();
        inputFile = codeFile;
        try {
            initCodeMap();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (CodeNotValidException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Resets the internal code map to an empty map as well as resetting the current max ID count.
     * Should be used for testing purposes only.
     */
    public void resetParams() {

        codeMap = new HashMap<String, Code>();
        currentMaxID = 0;
        idToCodeMap = new HashMap<Integer, Code>();
    }

    /**
     * Puts a cause of death code in the map.
     *
     * @param codeFromFile
     * @param descriptionFromFile
     * @throws CodeNotValidException
     */
    private void putCoDCodeInMap(final String codeFromFile, final String descriptionFromFile) throws CodeNotValidException {

        if (codeMap.get(codeFromFile) == null) {
            CoDCode code = new CoDCode(codeFromFile, descriptionFromFile, currentMaxID);
            codeMap.put(codeFromFile, code);
            idToCodeMap.put(currentMaxID++, code);
        }
    }

    /**
     * Puts an occupation code in the map.
     *
     * @param codeFromFile
     * @param descriptionFromFile
     * @throws CodeNotValidException
     */
    private void putOccCodeInMap(final String codeFromFile, final String descriptionFromFile) throws CodeNotValidException {

        if (codeMap.get(codeFromFile) == null) {
            OccCode code = new OccCode(codeFromFile, descriptionFromFile, currentMaxID);
            codeMap.put(codeFromFile, code);
            idToCodeMap.put(currentMaxID++, code);

        }
    }

}